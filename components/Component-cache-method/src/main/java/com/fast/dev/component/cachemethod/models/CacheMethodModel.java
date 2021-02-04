package com.fast.dev.component.cachemethod.models;

import java.lang.reflect.Method;

public class CacheMethodModel {
	// 数据集名称
	private String collectionName;
	// 总共缓存的时间（单位秒）
	private long timeToLiveSeconds;
	// 最后一次访问缓存的日期至失效之时的时间间隔（单位秒）
	private long timeToIdleSeconds;
	// 最大的缓存对象的数量
	private int maxMemoryCount;
	// 内存不够是否自动写到磁盘上
	private boolean overflowToDisk;
	// 对应的方法
	private Method method;
	// 参数类型
	private Boolean[] argTypes;

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Boolean[] getArgTypes() {
		return argTypes;
	}

	public void setArgTypes(Boolean[] argTypes) {
		this.argTypes = argTypes;
	}

	public long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	public void setTimeToLiveSeconds(long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	public long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public void setTimeToIdleSeconds(long timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	public int getMaxMemoryCount() {
		return maxMemoryCount;
	}

	public void setMaxMemoryCount(int maxMemoryCount) {
		this.maxMemoryCount = maxMemoryCount;
	}

	public boolean isOverflowToDisk() {
		return overflowToDisk;
	}

	public void setOverflowToDisk(boolean overflowToDisk) {
		this.overflowToDisk = overflowToDisk;
	}

}
