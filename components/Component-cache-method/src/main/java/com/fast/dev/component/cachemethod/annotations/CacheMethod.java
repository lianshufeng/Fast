package com.fast.dev.component.cachemethod.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheMethod {
	/**
	 * 缓存数据集名，支持多个
	 * 
	 * @return
	 */
	String collectionName();

	/**
	 * 最大缓存的结果集对象
	 * 
	 * @return
	 */
	int maxMemoryCount() default 1000;

	/**
	 * 内存不够是否存放磁盘，需要返回的对象实现序列化接口
	 * 
	 * @return
	 */
	boolean overflowToDisk() default false;

	/**
	 * 最后一次访问缓存的日期至失效之时的时间间隔，单位秒
	 * 
	 * @return
	 */
	long timeToIdleSeconds() default 10;

	/**
	 * 缓存总存放时间，单位秒
	 * 
	 * @return
	 */
	long timeToLiveSeconds() default 10;

}
