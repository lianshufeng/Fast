package com.fast.dev.data.mongo.data;

import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.core.util.script.GroovyUtil;
import com.fast.dev.data.base.data.DataCleanCondition;
import com.fast.dev.data.base.data.DataSyncHelper;
import com.fast.dev.data.base.data.annotations.sync.DataSync;
import com.fast.dev.data.base.data.annotations.sync.DataSyncs;
import com.fast.dev.data.base.data.model.UpdateSyncDetails;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.mongodb.bulk.BulkWriteResult;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DataSyncHelperMongo implements DataSyncHelper {


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    /**
     * 默认的最大通知同步数
     */
    private static final int DefaultMaxNoticeCount = 3;


    //实体与注解的缓存
    Map<Class<? extends AbstractPersistable>, Map<Field, DataSync[]>> dataSyncCache = new ConcurrentHashMap<>();


    @Override
    public Set<UpdateSyncDetails> update(Class<? extends AbstractPersistable> entityClasses, Object id) {
        return this.update(entityClasses, id, DefaultMaxNoticeCount);
    }

    @Override
    public Set<UpdateSyncDetails> update(Class<? extends AbstractPersistable> entityClasses, Object id, int maxNoticeCount) {
        Map<Class<? extends AbstractPersistable>, UpdateSyncDetails> updateSyncDetailsSet = new HashMap<>();

        //首次同步当前表的所有字段
        Map<Field, DataSync[]> dataSyncs = getDataSyncAnnotations(entityClasses);
        this.update(entityClasses, String.valueOf(id), dataSyncs.keySet(), maxNoticeCount, updateSyncDetailsSet);


        return updateSyncDetailsSet.values().stream().collect(Collectors.toSet());
    }

    /**
     * 同步实体的数据
     *
     * @param entityClasses
     * @param id
     * @param fields               , 需要同步的字段名
     * @param maxNoticeCount
     * @param updateSyncDetailsSet
     */
    private void update(Class<? extends AbstractPersistable> entityClasses, String id, Set<Field> fields, int maxNoticeCount, Map<Class<? extends AbstractPersistable>, UpdateSyncDetails> updateSyncDetailsSet) {
        //非空过滤
        if (!StringUtils.hasText(id) || entityClasses == null) {
            return;
        }

        //判断需要同步的字段
        Map<Field, DataSync[]> dataSyncs = new HashMap<>();
        getDataSyncAnnotations(entityClasses).entrySet().forEach((it) -> {
            if (fields.contains(it.getKey())) {
                dataSyncs.put(it.getKey(), it.getValue());
            }
        });
        if (dataSyncs.size() == 0) {
            return;
        }


        //取出文档数据
        Object source = getDocument(entityClasses, id);
        if (source == null) {
            return;
        }


        //进行注解的数据同步
        updateDataSync(BeanUtil.bean2Map(source), dataSyncs, updateSyncDetailsSet);


        //继续通知同步数据
        continueNoticeDataSync(maxNoticeCount, dataSyncs, updateSyncDetailsSet);

    }


    /**
     * 继续通知下去同步数据
     */
    @SneakyThrows
    private void continueNoticeDataSync(int maxNoticeCount, Map<Field, DataSync[]> dataSyncs, Map<Class<? extends AbstractPersistable>, UpdateSyncDetails> updateSyncDetailsSet) {
        //是否需要继续通知下去
        for (Map.Entry<Field, DataSync[]> it : dataSyncs.entrySet()) {
            for (DataSync dataSync : it.getValue()) {
                //目标的字段名
                String targetFieldName = dataSync.targetField();
                //目标的实体
                Class<? extends AbstractPersistable> targetEntity = dataSync.targetEntity();
                //目标实体上所有的注解
                Map<Field, DataSync[]> targetDataSyncs = getDataSyncAnnotations(targetEntity);

                // 该实体下有数据同步注解 且 该实体的数据同步次数 没有超过编制次数
                if (targetDataSyncs.size() > 0 && getUpdateSyncDetails(updateSyncDetailsSet, targetEntity).getNoticeCount() < maxNoticeCount) {

                    //取出目标表的字段
                    Set<Field> targetFields = new HashSet<Field>() {{
                        add(targetEntity.getDeclaredField(targetFieldName));
                    }};

                    //需要同步的数据id
                    Set<String> ids = new HashSet<>(updateSyncDetailsSet.get(targetEntity).getIds());
                    ids.forEach((id) -> {
                        this.update(targetEntity, id, targetFields, maxNoticeCount, updateSyncDetailsSet);
                    });
                }
            }
        }

    }


    /**
     * 更新数据
     *
     * @param source
     * @param dataSyncs
     */
    private void updateDataSync(Map<String, Object> source, Map<Field, DataSync[]> dataSyncs, Map<Class<? extends AbstractPersistable>, UpdateSyncDetails> updateSyncDetailsSet) {
        //批量执行集合更新
        Map<Class<? extends AbstractPersistable>, BulkOperationsModel> bulkOperationsMap = new HashMap<>();
        //记录当前更新的文档中，所包含的详细描述,去重
        Map<Class<? extends AbstractPersistable>, UpdateSyncDetails> updateSyncDetailsExtsSets = new HashMap<>();
        //更新所有的数据同步
        dataSyncs.entrySet().forEach((it) -> {
            String fieldName = it.getKey().getName();
            //遍历注解
            for (DataSync dataSync : it.getValue()) {
                //取出更新实体的详细描述
                UpdateSyncDetails updateSyncDetails = getUpdateSyncDetails(updateSyncDetailsSet, dataSync.targetEntity());
                updateSyncDetailsExtsSets.put(dataSync.targetEntity(), updateSyncDetails);

                //进行单实体的数据更新
                Set<String> ids = updateData(fieldName, source, dataSync, bulkOperationsMap);

                //记录数据更新
                recordNoticeUpdateSyncDetails(updateSyncDetails, ids, false);
            }
        });


        bulkOperationsMap.entrySet().forEach((it) -> {
            Class<? extends AbstractPersistable> cls = it.getKey();
            BulkWriteResult bulkWriteResult = it.getValue().execute();
            if (bulkWriteResult != null) {
                long size = bulkWriteResult.getModifiedCount();
                log.debug(String.format("update : %s  , size : %s ", cls, size));
            }
        });


        //记录不重复的数据
        updateSyncDetailsExtsSets.entrySet().forEach((it) -> {
            recordNoticeUpdateSyncDetails(it.getValue(), null, true);
        });


    }


    /**
     * 获取批量操作的单集合
     *
     * @param bulkOperationsMap
     * @param entityCls
     * @return
     */
    private BulkOperationsModel getBulkOperations(Map<Class<? extends AbstractPersistable>, BulkOperationsModel> bulkOperationsMap, Class<? extends AbstractPersistable> entityCls) {
        BulkOperationsModel bulkOperationsModel = bulkOperationsMap.get(entityCls);
        if (bulkOperationsModel == null) {
            bulkOperationsModel = new BulkOperationsModel(this.mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, entityCls));
            bulkOperationsMap.put(entityCls, bulkOperationsModel);
        }
        return bulkOperationsModel;
    }

    /**
     * 更新数据
     *
     * @param fieldName
     * @param source
     * @param dataSync
     */
    private Set<String> updateData(String fieldName, Map<String, Object> source, DataSync dataSync, Map<Class<? extends AbstractPersistable>, BulkOperationsModel> bulkOperationsMap) {

        //取出需要更新的容器id
        Set<String> ids = new HashSet<>();

        //更新条件的判断
        if (!checkUpdateCondition(dataSync, source.get("id"))) {
            log.debug("过滤数据: " + source);
            return ids;
        }

        //查询条件
        String filter = GroovyUtil.textTemplate(source, dataSync.targetQuery());

        //目标实体
        Class<? extends AbstractPersistable> targetEntity = dataSync.targetEntity();


        log.debug(String.format("%s : %s  ", targetEntity, filter));

        //批量处理
        BulkOperationsModel bulkOperations = getBulkOperations(bulkOperationsMap, targetEntity);


        for (Document document : this.mongoTemplate.getCollection(this.dbHelper.getCollectionName(targetEntity)).find(Document.parse(filter))) {

            //目标文档需要修改的记录id
            String id = String.valueOf(document.get("_id"));

            //查询条件
            Query query = Query.query(Criteria.where("_id").is(id));

            //更新数据
            Update update = buildUpdateDocument(source, source.get(fieldName), dataSync);

            //加入更新操作
            bulkOperations.updateOne(query, update);

            //目标表中的id
            ids.add(id);
        }

        return ids;

    }

    /**
     * 检查更新条件
     */
    private boolean checkUpdateCondition(DataSync dataSync, Object id) {
        Class<? extends DataCleanCondition>[] conditionCls = dataSync.condition();
        if (conditionCls == null || conditionCls.length == 0) {
            return true;
        }
        for (Class<? extends DataCleanCondition> cls : conditionCls) {
            if (!this.applicationContext.getBean(cls).check(id)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取数据同步的注解
     *
     * @param entityClasses
     * @return
     */
    @SneakyThrows
    public Map<Field, DataSync[]> getDataSyncAnnotations(Class<? extends AbstractPersistable> entityClasses) {
        //通过缓存获取实体上的注解
        Map<Field, DataSync[]> ret = this.dataSyncCache.get(entityClasses);
        if (ret != null) {
            return ret;
        }
        //遍历所有的注解
        ret = new HashMap<>();
        this.dataSyncCache.put(entityClasses, ret);
        listDataSyncAnnotations(ret, entityClasses);
        return ret;
    }


    /**
     * 构建更新文档
     *
     * @param source
     * @param dataSync
     * @return
     */
    private Update buildUpdateDocument(Map<String, Object> source, Object me, DataSync dataSync) {
        Update update = new Update();
        String expression = dataSync.targetExpression();
        Object value = null;
        //优先表达式
        if (StringUtils.hasText(expression)) {
            value = GroovyUtil.runScript(source, expression);
        } else {
            value = me;
        }
        update.set(dataSync.targetField(), value);
        this.dbHelper.updateTime(update);
        return update;
    }


    /**
     * 遍历数据同步的注解
     *
     * @param ret
     * @param entityClasses
     */
    private static void listDataSyncAnnotations(Map<Field, DataSync[]> ret, Class<? extends AbstractPersistable> entityClasses) {
        for (Field field : entityClasses.getDeclaredFields()) {
            field.setAccessible(true);

            //判断是否有集合注解
            DataSyncs dataSyncs = field.getAnnotation(DataSyncs.class);
            if (dataSyncs != null) {
                ret.put(field, dataSyncs.value());
            }
            //判断是否单注解
            DataSync dataSync = field.getAnnotation(DataSync.class);
            if (dataSync != null) {
                ret.put(field, new DataSync[]{dataSync});
            }
        }
        //递归遍历所有属性
        Class superCls = entityClasses.getSuperclass();
        if (superCls != null && !superCls.equals(AbstractPersistable.class)) {
            listDataSyncAnnotations(ret, (Class<? extends AbstractPersistable>) entityClasses.getSuperclass());
        }
    }


    /**
     * 查询单文档的指定id的记录
     *
     * @param entityClasses
     * @param id
     * @return
     */
    private Object getDocument(Class<? extends AbstractPersistable> entityClasses, Object id) {
        return this.mongoTemplate.findById(id, entityClasses);
    }


    /**
     * 获取同步表详情信息
     *
     * @param updateSyncDetailsSet
     * @param entityClasses
     * @return
     */
    private static UpdateSyncDetails getUpdateSyncDetails(Map<Class<? extends AbstractPersistable>, UpdateSyncDetails> updateSyncDetailsSet, Class<? extends AbstractPersistable> entityClasses) {
        UpdateSyncDetails updateSyncDetails = updateSyncDetailsSet.get(entityClasses);
        if (updateSyncDetails == null) {
            updateSyncDetails = new UpdateSyncDetails();
            updateSyncDetails.setEntity(entityClasses);
            updateSyncDetails.setNoticeCount(0);
            updateSyncDetails.setIds(new HashSet<>());
            updateSyncDetailsSet.put(entityClasses, updateSyncDetails);
        }
        return updateSyncDetails;
    }

    /**
     * 记录数据同步的详情信息
     *
     * @param updateSyncDetails
     */
    private static void recordNoticeUpdateSyncDetails(UpdateSyncDetails updateSyncDetails, Set<String> ids, boolean isNoticeCountInc) {

        //id不为空在累加
        if (ids != null) {
            updateSyncDetails.getIds().addAll(ids);
        }

        //是否累加通知次数
        if (isNoticeCountInc) {
            updateSyncDetails.setNoticeCount(updateSyncDetails.getNoticeCount() + 1);
        }
    }


    class BulkOperationsModel {

        @Getter
        private BulkOperations bulkOperations;

        //计次
        private long updateCount = 0;

        /**
         * 修改记录
         *
         * @param query
         * @param update
         */
        public void updateOne(Query query, Update update) {
            bulkOperations.updateOne(query, update);
            updateCount++;
        }

        public BulkOperationsModel(BulkOperations bulkOperations) {
            this.bulkOperations = bulkOperations;
        }


        /**
         * 执行
         *
         * @return
         */
        public BulkWriteResult execute() {
            if (updateCount == 0) {
                return null;
            }
            return this.bulkOperations.execute();
        }


    }


}
