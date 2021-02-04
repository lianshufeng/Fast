package com.fast.dev.core.util.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * 业务结果集
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class ServiceResult {

    /**
     * 状态
     */
    private Object state;

    /**
     * 创建实例对象
     *
     * @param cls
     * @param state
     * @param <T>
     * @return
     */
    public static <T> T build(Class<? extends T> cls, Object state) {
        try {
            ServiceResult serviceResult = (ServiceResult) cls.newInstance();
            serviceResult.setState(state);
            return (T) serviceResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 创建实例并复制
     *
     * @param cls
     * @param state
     * @param copySource
     * @param <T>
     * @return
     */
    public static <T> T build(Class<? extends T> cls, Object state, Object copySource) {
        T t = build(cls, state);
        BeanUtils.copyProperties(copySource, t);
        return t;
    }


}
