package com.fast.dev.component.mongodb.dao;

import com.fast.dev.component.mongodb.model.Orders;
import com.fast.dev.component.mongodb.util.EntityObjectUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用的一些方法实现
 *
 * @param <T>
 * @作者 练书锋
 * @时间 2016年5月13日
 */
public abstract class MongoDao<T> {

    @Autowired
    protected MongoTemplate mongoTemplate;


    // 取出当前的实体类型
    protected Class<T> entityClass = getCls();

    private Class<T> getCls() {
        Type t = this.getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            return (Class<T>) p[0];
        }
        return null;
    }

    /**
     * 保存对象
     *
     * @param t
     */
    public void save(T t) {
        this.mongoTemplate.save(t);
    }


    /**
     * 批量删除记录
     *
     * @param ids
     * @return
     */
    public long remove(String... ids) {
        if (ids == null || ids.length < 1) {
            return 0;
        }
        Criteria criteria = createMultiIdsCriteria(ids);
        Query query = new Query();
        query.addCriteria(criteria);
        DeleteResult deleteResult = this.mongoTemplate.remove(query, this.entityClass);
        return deleteResult.getDeletedCount();
    }


    /**
     * 获取一条记录
     *
     * @param id
     * @return
     */
    public T get(String id) {
        return (T) this.mongoTemplate.findById(id, this.entityClass);
    }


    /**
     * 统计总记录数
     *
     * @param criteria
     * @return
     */
    public long count(Criteria criteria) {
        return this.mongoTemplate.count(criteria == null ? new Query() : new Query(criteria), this.entityClass);
    }

    /**
     * 获取当前的实体
     *
     * @return
     */
    public Class<T> getEntityClass() {
        return this.entityClass;
    }


    /**
     * list分页查询
     *
     * @param skip
     * @param limit
     * @param descSort
     * @return
     */
    public List<T> list(int skip, int limit, Orders... descSort) {
        Query query = new Query();
        query.skip(skip);
        query.limit(limit);
        return this.list(query, descSort);
    }

    /**
     * 条件排序查询
     *
     * @param query
     * @param descSort
     * @return
     */
    public List<T> list(Query query, Orders... descSort) {
        if (descSort != null && descSort.length > 0) {
            List<Order> orders = new ArrayList<Order>();
            for (Orders sort : descSort) {
                orders.add(new Order(sort.isDesc() ? Direction.DESC : Direction.ASC, sort.getName()));
            }
            query.with(new Sort(orders));
        }
        return this.mongoTemplate.find(query, this.entityClass);
    }


    /**
     * 更新记录时间
     *
     * @param id
     * @return
     */
    public boolean updateTime(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        EntityObjectUtil.preUpdate(update);
        UpdateResult updateResult = this.mongoTemplate.updateFirst(query, update, entityClass);
        return updateResult.getModifiedCount() > 0;
    }


    public void drop() {
        this.mongoTemplate.dropCollection(entityClass);
    }


    public boolean exists(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return this.mongoTemplate.exists(query, entityClass);
    }

    /**
     * 创建同时操作多个id的查询条件
     *
     * @param ids
     * @return
     */
    protected static Criteria createMultiIdsCriteria(String... ids) {
        return EntityObjectUtil.createQueryBatch("_id", ids);
    }


}
