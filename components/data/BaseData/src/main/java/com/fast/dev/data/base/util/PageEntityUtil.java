package com.fast.dev.data.base.util;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者：练书锋
 * 时间：2018/6/26
 * 分页实体转换工具
 */
public class PageEntityUtil {


    /**
     * 转换到模型
     *
     * @param pages
     * @return
     */
    public static <S, T> Page<T> toPageModel(Page<S> pages, DataClean<S, T> dataClean) {
        List<T> result = pages.stream().map((it) -> {
            return dataClean.execute(it);
        }).collect(Collectors.toList());
        //转换模型
        return new PageImpl<T>(result, PageRequest.of(pages.getNumber(), pages.getSize(), pages.getSort()), pages.getTotalElements());
    }

    /**
     * 数据处理
     *
     * @param <S>
     * @param <T>
     */
    public interface DataClean<S, T> {

        /**
         * 数据处理
         *
         * @param data
         * @return
         */
        T execute(S data);
    }


    /**
     * 转换到模型
     *
     * @param pages
     * @return
     */
    public static <S, T> Page<T> toPageModel(Page<S> pages, Class<T> cls) {
        return toPageModel(pages, new DataClean<S, T>() {

            @Override
            public T execute(S data) {
                try {
                    Object o = cls.newInstance();
                    BeanUtils.copyProperties(data, o);
                    return (T) o;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }


}
