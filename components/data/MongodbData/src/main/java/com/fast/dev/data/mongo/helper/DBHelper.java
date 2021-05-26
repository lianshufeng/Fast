package com.fast.dev.data.mongo.helper;

import com.fast.dev.data.mongo.domain.DBHelperEntity;
import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.experimental.Delegate;
import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DBHelper {


    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private DocumentCodec documentCodec;


    //引用事务助手
    @Autowired
    @Delegate(types = TransactionHelper.class)
    private TransactionHelper transactionHelper;


    //引用mql查询助手
    @Autowired
    @Delegate(types = MongoQueryLanguageHelper.class)
    private MongoQueryLanguageHelper mongoQueryLanguageHelper;


    //引用批量查询
    @Autowired
    @Delegate(types = BatchQueryHelper.class)
    private BatchQueryHelper batchQueryHelper;


    // 缓存集合与类名
    private Map<Class, String> collectionCache = new ConcurrentHashMap();


    /**
     * 设置更新时间
     *
     * @param entity
     */
    public void updateTime(SuperEntity entity) {
        entity.setUpdateTime(getTime());
    }

    /**
     * 设置创建时间
     *
     * @param entity
     */
    public void saveTime(SuperEntity entity) {
        entity.setCreateTime(getTime());
        updateTime(entity);
    }


    /**
     * 设置更新时间
     */
    public void updateTime(Update update) {
        update.set("updateTime", getTime());
    }

    /**
     * 构建一个 update
     *
     * @return
     */
    public Update buildUpdate() {
        Update update = new Update();
        updateTime(update);
        return update;
    }


    /**
     * 设置创建时间
     */
    public void saveTime(Update update) {
        update.setOnInsert("createTime", getTime());
        updateTime(update);
    }


    /**
     * 取时间
     *
     * @return
     */
    public long getTime() {
//        Query query = new Query(Criteria.where("key").is("time"));
//        Update update = new Update();
//        update.setOnInsert("key", "time");
//        update.currentDate("value");
//        FindAndModifyOptions options = new FindAndModifyOptions();
//        options.upsert(true);
//        DBHelperEntity dbHelperEntity = this.mongoTemplate.findAndModify(query, update, options, DBHelperEntity.class);
//        if (dbHelperEntity==null){
//            return getTime();
//        }
//        return Long.valueOf(String.valueOf(((Date) dbHelperEntity.getValue()).getTime()));
        return  System.currentTimeMillis();
    }


    /**
     * 分页查询
     *
     * @param query
     * @param pageable
     * @param cls
     * @param <T>
     * @return
     */
    public <T> Page<T> pages(Query query, Pageable pageable, Class cls) {
        long total = this.mongoTemplate.count(query, cls);
        query.with(pageable);
        return new PageImpl<T>(this.mongoTemplate.find(query, cls), pageable, total);
    }


    /**
     * 分页查询
     *
     * @param query
     * @param pageable
     * @param cls
     * @param collectionName
     * @param <T>
     * @return
     */
    public <T> Page<T> pages(Query query, Pageable pageable, Class cls, String collectionName) {
        long total = this.mongoTemplate.count(query, collectionName);
        query.with(pageable);
        return new PageImpl<T>(this.mongoTemplate.find(query, cls, collectionName), pageable, total);
    }


    /**
     * 支持 aggregate 的分页查询语法
     *
     * @param pageable
     * @param cls
     * @return
     */
    public Page<Document> pages(Criteria criteria, Pageable pageable, Class<? extends SuperEntity> cls, AggregationOperation... aggregationOperations) {
        //分页
        AggregationOperation skip = Aggregation.skip(pageable.getOffset());
        AggregationOperation limit = Aggregation.limit(pageable.getPageSize());


        //排序
        AggregationOperation sort = Aggregation.sort(pageable.getSort());
        //分页模型
        List<AggregationOperation> pageAggregationOperation = new ArrayList<AggregationOperation>() {{
            //分页
            add(skip);
            add(limit);
        }};


        //总量
        AggregationOperation count = Aggregation.count().as("count");


        //构建分页查询条件
        List<AggregationOperation> aggregations = new ArrayList<AggregationOperation>() {{

            //查询条件
            if (criteria != null) {
                add(Aggregation.match(criteria));
            }

            //其他聚合业务
            addAll(Arrays.asList(aggregationOperations));

            //排序,必须放在分页模型外面，负责排序是分页排序
            if (pageable.getSort() != null && !pageable.getSort().isEmpty()) {
                add(sort);
            }

            //分页模型
            add(Aggregation.facet().and(pageAggregationOperation.toArray(new AggregationOperation[0])).as("items").and(count).as("count"));
        }};


        //总量,用于分页, 移除外部的分页统计接口，用 facet 进行分页统计
        Document result = this.mongoTemplate.aggregate(Aggregation.newAggregation(aggregations), this.getCollectionName(cls), Document.class).getMappedResults().get(0);

        //转换输出类型
        List items = result.getList("items", Document.class);

        long total = 0;
        List<Document> rets = result.getList("count", Document.class);
        if (rets == null || rets.size() == 0) {
            total = 0;
        } else {
            total = Long.parseLong(String.valueOf(rets.get(0).get("count")));
        }


        //记录的总数


        //转换为分页模型
        return new PageImpl<Document>(items, pageable, total);
    }


    /**
     * 获取实体的名称
     *
     * @param cls
     * @return
     */
    public String getCollectionName(Class cls) {
        String name = this.collectionCache.get(cls);
        if (name == null) {
            name = this.mongoTemplate.getCollectionName(cls);
            this.collectionCache.put(cls, name);
        }
        return name;
    }


    /**
     * Document 转 json 对象
     *
     * @return
     */
    public String toJson(Document document) {
        return document.toJson(documentCodec);
    }


    /**
     * 支持查询对象转Json
     *
     * @param query
     * @return
     */
    public String toJson(Query query) {
        return toJson(query.getQueryObject());
    }


}
