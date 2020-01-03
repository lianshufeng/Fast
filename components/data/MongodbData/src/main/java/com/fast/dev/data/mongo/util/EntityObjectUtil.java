package com.fast.dev.data.mongo.util;


import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.data.mongo.domain.SuperEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;

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
        update.set("updateTime", getTime());
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


    /**
     * 实体到更新模型
     *
     * @param entity
     * @param update
     * @param ignore
     */
    public static void entity2Update(Object entity, Update update, String... ignore) {
        Set<String> ignoreSet = null;
        if (ignore != null) {
            ignoreSet = new HashSet<>(Arrays.asList(ignore));
        }
        entity2Update(entity, entity.getClass(), update, ignoreSet);
    }

    /**
     * 实体判断是否为null，非null并设置到update中的修改
     *
     * @param entity
     * @param update
     */
    public static void entity2Update(Object entity, Update update, Set<String> ignore) {
        entity2Update(entity, entity.getClass(), update, ignore);
    }

    /**
     * 实体判断是否为null，非null并设置到update中的修改
     *
     * @param entity
     * @param update
     */
    public static void entity2Update(Object entity, Update update) {
        entity2Update(entity, entity.getClass(), update, null);
    }

    private static void entity2Update(Object entity, Class<?> meClass, Update update, Set<String> ignore) {
        Map<String, Object> m = BeanUtil.bean2Map(entity);
        m.entrySet().forEach((it) -> {
            //没有value则不更新
            if (it.getValue() == null) {
                return;
            }
            //忽略列表
            if (ignore == null) {
                update.set(it.getKey(), it.getValue());
            } else if (!ignore.contains(it.getKey())) {
                update.set(it.getKey(), it.getValue());
            }
        });

    }


}
