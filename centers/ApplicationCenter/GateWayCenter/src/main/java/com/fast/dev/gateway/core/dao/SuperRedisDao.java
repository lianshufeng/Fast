package com.fast.dev.gateway.core.dao;

/**
 * redis的父类Dao
 */
public abstract class SuperRedisDao {

    /**
     * 表名
     *
     * @return
     */
    public abstract String tableName();

    /**
     * 构建key
     *
     * @return
     */
    public String buildKey(String... items) {
        StringBuilder sb = new StringBuilder();
        sb.append(tableName() + "_");
        for (String item : items) {
            sb.append(item + "_");
        }
        if (items != null || items.length > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }


}
