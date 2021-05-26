package com.fast.dev.data.base.util;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 作者：练书锋
 * 时间：2018/6/26
 * 分页实体转换工具
 */
public class PageEntityUtil {


    /**
     * 线程数
     */
    private final static int threadCount = Runtime.getRuntime().availableProcessors() * 2;


    /**
     * 集合转换
     *
     * @param collections
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> Map<S, T> concurrentCollection(Collection<S> collections, DataClean<S, T> dataClean) {
        return concurrentCollection(collections, dataClean, threadCount);
    }

    /**
     * 集合转换
     *
     * @param collections
     * @param <S>
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <S, T> Map<S, T> concurrentCollection(Collection<S> collections, DataClean<S, T> dataClean, int threadCountSize) {
        Map<S, T> contents = new ConcurrentHashMap<>();
        @Cleanup("shutdownNow") ExecutorService executorService = Executors.newFixedThreadPool(threadCountSize);
        CountDownLatch countDownLatch = new CountDownLatch(collections.size());
        collections.forEach((it) -> {
            executorService.execute(() -> {
                try {
                    contents.put(it, dataClean.execute(it));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        countDownLatch.await();


        return contents;
    }


    /**
     * 并行分页模型转换
     *
     * @param pages
     * @param dataClean
     * @param threadCountSize
     * @param <S>
     * @param <T>
     * @return
     */
    @SneakyThrows
    @Deprecated
    public static <S, T> Page<T> concurrent2PageModel(Page<S> pages, DataClean<S, T> dataClean, int threadCountSize) {
        //数据源
        List<S> sources = pages.getContent();
        if (sources == null) {
            return new PageImpl<T>(new ArrayList<>(), PageRequest.of(pages.getNumber(), pages.getSize(), pages.getSort()), pages.getTotalElements());
        }

        //容器，保留序号与数据
        Map<Integer, S> sourcesContents = new ConcurrentHashMap<>();
        for (int i = 0; i < sources.size(); i++) {
            sourcesContents.put(i, sources.get(i));
        }

        //目标容器
        Map<Integer, T> newContents = new ConcurrentHashMap<>();

        //线程池与计数器
        @Cleanup("shutdownNow") ExecutorService executorService = Executors.newFixedThreadPool(threadCountSize);
        CountDownLatch countDownLatch = new CountDownLatch(sourcesContents.size());

        //进行线程并发
        sourcesContents.entrySet().forEach((it) -> {
            executorService.execute(() -> {
                try {
                    newContents.put(it.getKey(), dataClean.execute(it.getValue()));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        });
        //阻塞多线直到结束
        countDownLatch.await();

        //转换为新的集合，且保证顺序
        List<T> newList = new ArrayList<>();
        for (int i = 0; i < newContents.entrySet().size(); i++) {
            newList.add(newContents.get(i));
        }

        //转换模型
        return new PageImpl<T>(newList, PageRequest.of(pages.getNumber(), pages.getSize(), pages.getSort()), pages.getTotalElements());
    }

    /**
     * 并行分页模型转换
     *
     * @param pages
     * @param dataClean
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> Page<T> concurrent2PageModel(Page<S> pages, DataClean<S, T> dataClean) {
        List<T> result = pages.getContent().parallelStream().map((it) -> {
            return dataClean.execute(it);
        }).collect(Collectors.toList());
        return new PageImpl<T>(result, PageRequest.of(pages.getNumber(), pages.getSize(), pages.getSort()), pages.getTotalElements());
    }


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
    @FunctionalInterface
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
                    Object o = cls.getDeclaredConstructor().newInstance();
                    BeanUtils.copyProperties(data, o);
                    return (T) o;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    /**
     * 构建一个空分页对象
     *
     * @param pageable
     * @return
     */
    public static Page buildEmptyPage(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    /**
     * 构建一个空分页对象
     *
     * @return
     */
    public static Page buildEmptyPage() {
        return new PageImpl<>(new ArrayList<>());
    }


}
