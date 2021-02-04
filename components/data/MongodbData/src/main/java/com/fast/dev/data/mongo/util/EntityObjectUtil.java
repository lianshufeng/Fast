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
     * 创建批量查询
     *
     * @param items
     * @return
     */
    public static Criteria createQueryBatch(Collection<Map<String, Object>> items) {
        final Set<Criteria> criteriaSet = new HashSet<>();
        items.stream().map((map) -> {
            return map.entrySet();
        }).forEach((entry) -> {
            Criteria criteria = new Criteria();
            entry.forEach((it) -> {
                criteria.and(it.getKey()).is(it.getValue());
            });
            criteriaSet.add(criteria);
        });
        return new Criteria().orOperator(criteriaSet.toArray(new Criteria[0]));
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


    public static Criteria buildCriteria(Criteria criteria, Object source, CriteriaType type, String... items) {
        if (source == null) {
            return null;
        }
        if (items == null || items.length == 0) {
            return criteria;
        }
        Map<String, Object> objMap = BeanUtil.bean2Map(source);
        for (String key : items) {
            //取出value
            Object value = objMap.get(key);
            if (value == null) {
                continue;
            }
            criteria = criteria.and(key);
            switch (type) {
                case Is:
                    criteria = criteria.is(value);
                    break;
                case Like:
                    criteria = criteria.regex(String.valueOf(value));
                    break;
                case In:
                    criteria = criteria.in(value);
                    break;
            }
        }
        return criteria;
    }

    /**
     * 条件
     */
    public static enum CriteriaType {
        /**
         * 模糊查询
         */
        Like,
        /**
         * 精准查询
         */
        Is,
        /**
         * 集合查询
         */
        In
    }


}
