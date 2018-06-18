package com.fast.dev.data.mongo.util;


import com.fast.dev.data.mongo.domain.SuperEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 实体工具类
 *
 * @作者 练书锋
 * @时间 2016年5月23日
 */
public class EntityObjectUtil {

    /**
     * 保存之前执行
     *
     * @param superEntity
     */
    public static void preInsert(SuperEntity superEntity) {
        superEntity.setId(null);
        superEntity.setCreateTime(getTime());
        superEntity.setUpdateTime(getTime());
    }

    /**
     * 更新之前执行
     *
     * @param superEntity
     * @return
     */
    public static void preUpdate(SuperEntity superEntity) {
        superEntity.setUpdateTime(getTime());
    }

    /**
     * 更新之前
     *
     * @param update
     */
    public static void preUpdate(Update update) {
        update.set("updateTime", new Date().getTime());
    }

    /**
     * 创建查询_批量
     *
     * @param fieldName
     * @param values
     * @return
     */
    public static Criteria createQueryBatch(String fieldName, String... values) {
        if (values == null || values.length < 1) {
            return null;
        }
        List<Criteria> wheres = new ArrayList<Criteria>();
        for (String value : values) {
            Criteria where = Criteria.where(fieldName).is(value);
            wheres.add(where);
        }
        return new Criteria().orOperator(wheres.toArray(new Criteria[wheres.size()]));
    }

    /**
     * 创建查询_批量
     *
     * @param fieldName
     * @param values
     * @return
     */
    public static Criteria createQueryBatch(String fieldName, Object[] values) {
        if (values == null || values.length < 1) {
            return null;
        }
        List<Criteria> wheres = new ArrayList<Criteria>();
        for (Object value : values) {
            Criteria where = Criteria.where(fieldName).is(value);
            wheres.add(where);
        }
        return new Criteria().orOperator(wheres.toArray(new Criteria[wheres.size()]));
    }

    /**
     * 取出时间
     *
     * @return
     */
    public static long getTime() {
        return System.currentTimeMillis();
    }

}
