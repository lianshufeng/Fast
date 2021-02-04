package com.fast.dev.core.util.iterator;

/**
 * 迭代工具
 */
public class IteratorUtil {


    /**
     * 执行迭代
     *
     * @param process
     * @param filter
     * @param result
     * @param <T>
     */
    public static <T> void execute(Process<T> process, Filter<T> filter, Result<T> result) {
        executeProcess(process, filter, result);
    }

    private static <T> void executeProcess(Process<T> process, Filter<T> filter, Result<T> result) {
        T t = process.execute();
        if (filter.execute(t)) {
            result.execute(t);
            executeProcess(process, filter, result);
        }
    }


    /**
     * 过滤器
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface Filter<T> {
        boolean execute(T t);
    }


    /**
     * 结果集
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface Result<T> {
        void execute(T t);

    }

    /**
     * 功能代码
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface Process<T> {
        T execute();
    }


}



