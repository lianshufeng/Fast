package com.fast.dev.data.mongo.helper;

import com.fast.dev.data.mongo.domain.SuperEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DBHelper {


    @Autowired
    private MongoTemplate mongoTemplate;

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
        return System.currentTimeMillis();
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
        query.with(pageable);
        return new PageImpl<T>(this.mongoTemplate.find(query, cls), pageable, this.mongoTemplate.count(query, cls));
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


}
