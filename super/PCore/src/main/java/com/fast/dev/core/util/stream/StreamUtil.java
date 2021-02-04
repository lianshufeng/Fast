package com.fast.dev.core.util.stream;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {

    /**
     * 转化为Stream对象
     *
     * @param iterable
     * @param parallel
     * @param <T>
     * @return
     */
    public static <T> Stream<T> iterableToStream(Iterable<T> iterable, boolean parallel) {
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    public static <T> Stream<T> iterableToStream(Iterable<T> iterable) {
        return iterableToStream(iterable, false);
    }

}
