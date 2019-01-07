package com.fast.dev.component.cachemethod.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CleanMethod {
	/**
	 * 缓存数据集名，支持多个
	 * 
	 * @return
	 */
	String[] collectionName();
	
}
