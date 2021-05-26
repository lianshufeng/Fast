package com.fast.dev.data.mongo.data;

import com.fast.dev.data.base.data.DataCleanManager;
import com.fast.dev.data.mongo.data.dao.DataCleanTaskDao;
import com.fast.dev.data.mongo.data.domain.DataCleanTask;
import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
@Component
public class DataCleanManagerMongo implements DataCleanManager {


    //默认的线程池数量
    private final static int ThreadPoolCount = Runtime.getRuntime().availableProcessors() * 2;


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DataCleanTaskDao dataCleanTaskDao;

    @Autowired
    private DBHelper dbHelper;

    private Map<String, MongoDataCleanTask> _taskGroup = new ConcurrentHashMap<>();


    @Autowired
    private void init() {
        this._taskGroup.clear();
        this.applicationContext.getBeansOfType(MongoDataCleanTask.class).values().forEach((it) -> {
            String taskName = it.taskName();
            if (_taskGroup.containsKey(taskName)) {
                throw new RuntimeException("清洗任务名不能重复!!!!!");
            }
            this._taskGroup.put(taskName, it);
        });
    }


    @Override
    public boolean execute(String taskName, boolean all) {
        MongoDataCleanTask mongoDataCleanTask = this._taskGroup.get(taskName);
        if (mongoDataCleanTask == null) {
            return false;
        }
        final DataCleanTask dataCleanTask = this.dataCleanTaskDao.executeTask(mongoDataCleanTask);
        if (dataCleanTask == null) {
            return false;
        }

        //开始执行数据清洗任务
        new Thread(new Runnable() {
            @Override
            public void run() {
                runMongoDataCleanTask(dataCleanTask, all);
            }
        }).start();
        return true;
    }

    @Override
    public boolean execute(String taskName) {
        return execute(taskName, false);
    }

    //开始数据清洗任务
    @SneakyThrows
    private void runMongoDataCleanTask(DataCleanTask dataCleanTask, boolean all) {

        MongoDataCleanTask mongoDataCleanTask = _taskGroup.get(dataCleanTask.getTaskName());

        //通知开始执行任务事件
        mongoDataCleanTask.onStart();

        //任务心跳调度器，任务正在执行中
        @Cleanup("cancel") Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dataCleanTaskDao.activeTask(dataCleanTask.getTaskName());
            }
        }, 3000, 3000);


        //查询并构建清洗目标
        long startTime = all ? 0 : dataCleanTask.getLimitUpdateTime();
        dataCleanTask.setLimitUpdateTime(this.dbHelper.getTime());
//        Query entityQuery = createUpdateQuery(startTime, dataCleanTask.getLimitUpdateTime());
//        @Cleanup("delete") File tmpFile = File.createTempFile("dc-" + dataCleanTask.getEntityName(), ".tmp");
//        long total = saveRecords2TempFile(mongoDataCleanTask, entityQuery, tmpFile);

        long lastTime = dataCleanTask.getLimitUpdateTime();
        Document query = new Document("$and", List.of(
                new Document("updateTime", new Document("$gt", startTime)),
                new Document("updateTime", new Document("$lt", lastTime))
        ));
        final MongoCollection<Document> mongoCollection = this.mongoTemplate.getCollection(this.mongoTemplate.getCollectionName(mongoDataCleanTask.getEntity()));
        final long total = mongoCollection.countDocuments(query);
        //统计需要处理数据总量
        dataCleanTask.setTotal(total);
        this.mongoTemplate.save(dataCleanTask);

        //如果需要处理的数据为0 则表示不更新数据了
        if (total == 0) {
            return;
        }

        @Cleanup final MongoCursor<Document> mongoCursor = mongoCollection.find(query).cursor();

        final BigDecimal currentCount = new BigDecimal(0L);
        //线程池启动数据清洗流程
        CountDownLatch countDownLatch = new CountDownLatch(ThreadPoolCount);
        @Cleanup("shutdownNow") ExecutorService executorService = Executors.newFixedThreadPool(ThreadPoolCount);
//        @Cleanup RandomAccessFile rf = randomAccessFile(tmpFile);
        for (int i = 0; i < ThreadPoolCount; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    dataClean(mongoCursor, currentCount, total, mongoDataCleanTask, countDownLatch, executorService);
                }
            });
        }
        //阻塞直到线程结束
        countDownLatch.await();


        //通知任务执行结束
        mongoDataCleanTask.onEnd();
        log.info("数据清洗任务完成 : " + mongoDataCleanTask.getEntity());
    }


    /**
     * 通过数据库游标读取id
     *
     * @return
     */
    public synchronized final String[] readIdsFromMongoCursor(MongoCursor<Document> mongoCursor, BigDecimal currentCount, long total, int maxSize, String taskName) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < maxSize; i++) {
            if (mongoCursor.hasNext()) {
                ids.add(mongoCursor.next().getObjectId("_id").toHexString());
                currentCount.add(new BigDecimal(1));
            }
        }

        this.dataCleanTaskDao.setTaskprogress(taskName, currentCount.divide(new BigDecimal(total)));

        return ids.toArray(new String[0]);
    }


    /**
     * 开始数据清洗
     *
     * @param mongoDataCleanTask
     * @param countDownLatch
     */
    private void dataClean(MongoCursor<Document> mongoCursor, BigDecimal currentCount, long total, MongoDataCleanTask mongoDataCleanTask, CountDownLatch countDownLatch, ExecutorService executorService) {
        //读取数据实体
        String[] ids = readIdsFromMongoCursor(mongoCursor, currentCount, total, mongoDataCleanTask.batchSize(), mongoDataCleanTask.taskName());
        if (ids == null || ids.length == 0) {
            countDownLatch.countDown();
            return;
        }

        //查询数据
        Criteria criteria = EntityObjectUtil.createQueryBatch("_id", ids);
        List<?> items = this.mongoTemplate.find(Query.query(criteria), mongoDataCleanTask.getEntity());
        //构建数据清洗的数组
        Object entityArrays = Array.newInstance(mongoDataCleanTask.getEntity(), items.size());
        for (int i = 0; i < items.size(); i++) {
            Array.set(entityArrays, i, items.get(i));
        }

        //执行自定义的数据清洗
        try {
            mongoDataCleanTask.clean((Object[]) entityArrays);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //保存清洗后的数据
        for (Object item : items) {
            this.dbHelper.updateTime((SuperEntity) item);
            this.mongoTemplate.save(item);
        }


        //递归继续清洗
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dataClean(mongoCursor, currentCount, total, mongoDataCleanTask, countDownLatch, executorService);
            }
        });
    }


    /**
     * 通过临时文件读取需要数据清洗的实体对应的id
     *
     * @return
     */
//    private synchronized String[] readEntityIdsFromTempFile(MongoCursor<Document> mongoCursor, int maxSize, String taskName) {
//        try {
//            if (!mongoCursor.hasNext()){
//                mongoCursor.close();
//                return null;
//            }
//            if (rf.length() == 0l) {
//                return null;
//            }
//            String rate = String.format("%.2f", (double) rf.getFilePointer() / rf.length());
//            dataCleanTaskDao.setTaskprogress(taskName, new BigDecimal(rate));
//            //读取完整
//            if (rf.getFilePointer() >= rf.length()) {
//                return null;
//            }
//            log.info(String.format("数据清洗进度 : %s ", rate));
//            List<String> ids = new ArrayList<>();
//            for (int i = 0; i < maxSize; i++) {
//                String line = rf.readLine();
//                if (line != null) {
//                    ids.add(new String(line.getBytes("ISO-8859-1"), "UTF-8"));
//                }
//            }
//            return ids.toArray(new String[0]);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    @SneakyThrows
    private RandomAccessFile randomAccessFile(File file) {
        return new RandomAccessFile(file, "r");
    }


    @SneakyThrows
    private long saveRecords2TempFile(MongoDataCleanTask mongoDataCleanTask, Query query, File file) {
        @Cleanup RandomAccessFile ra = new RandomAccessFile(file, "rw");

        long total = 0;

        int page = 0;
        int size = 500;

        //按照更新时间排序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "updateTime"));
        query.with(pageable);
        List<SuperEntity> docs = this.mongoTemplate.find(query, mongoDataCleanTask.getEntity());
        while (docs.size() > 0) {
            for (SuperEntity it : docs) {
                total++;
                String line = it.getId() + "\n";
                ra.write(line.getBytes());
            }
            //翻页查询
            query.with(PageRequest.of(++page, size, Sort.by(Sort.Direction.ASC, "updateTime")));
            docs = this.mongoTemplate.find(query, mongoDataCleanTask.getEntity());
        }

        return total;
    }


    /**
     * 创建更新的查询条件
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private Query createUpdateQuery(long startTime, long endTime) {
        Query query = Query.query(Criteria.where("updateTime").gt(startTime).lt(endTime));
        return query;
    }


}
