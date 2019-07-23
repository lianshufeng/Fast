package com.fast.dev.promise.server.core.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class BeanUtil {

    /**
     * 取出值为null的属性名
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


    /**
     * 获取bean的某个属性
     *
     * @param o
     * @param varName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object get(Object o, String varName) throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(o, varName);
        return field.get(o);
    }

    /**
     * 设置bean的某个属性
     *
     * @param o
     * @param varName
     * @param value
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void set(Object o, String varName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(o, varName);
        field.set(o, value);
    }


    /**
     * 找到属性
     *
     * @param o
     * @param varName
     * @return
     * @throws NoSuchFieldException
     */
    private static Field findField(Object o, String varName) throws NoSuchFieldException {
        Field field = o.getClass().getDeclaredField(varName);
        field.setAccessible(true);
        return field;
    }


}
