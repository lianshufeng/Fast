package com.fast.dev.component.cachemethod.models;

import net.sf.ehcache.Ehcache;

/**
 * 返回的结果集
 * 
 * @author 练书锋
 *
 */
public class ResultCacheModel {
	// 返回的结果集对应的索引标识
	private String index;

	// 返回的结果集
	private Object result;

	// 缓存数据集对象
	private Ehcache cache;

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Ehcache getCache() {
		return cache;
	}

	public void setCache(Ehcache cache) {
		this.cache = cache;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

}
