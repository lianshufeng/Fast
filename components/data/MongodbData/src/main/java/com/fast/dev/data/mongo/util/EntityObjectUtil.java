package com.fast.dev.data.mongo.util;


import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
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
        for (Field declaredField : meClass.getDeclaredFields()) {
            if (!meClass.equals(SuperEntity.class)) {
                entity2Update(entity, meClass.getSuperclass(), update, ignore);
            }
            declaredField.setAccessible(true);
            try {
                String name = declaredField.getName();
                if (ignore != null && ignore.contains(name)) {
                    continue;
                }
                Object value = declaredField.get(entity);
                if (value != null) {
                    update.set(name, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    public static void main(String[] args) {
//        TestClass s = new TestClass();
//        s.setId("testId");
//        s.setTest1("tes111");
//        s.setTest3("tes11133");
//        Update update = new Update();
//        entity2Update(s, update);
//        System.out.println(JsonUtil.toJson(update));
//    }
//
//    @Data
//    public static class TestClass extends SuperEntity {
//        private String test1;
//        private String test2;
//        private String test3;
//    }

}
