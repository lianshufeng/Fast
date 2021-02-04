package com.fast.dev.component.cachemethod.models;

import java.lang.reflect.Method;

public class CleanMethodModel {
	// 数据集名称
	private String[] collectionName;
	// 对应的方法
	private Method method;
	// 参数类型
	private Boolean[] argTypes;

	public String[] getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String[] collectionName) {
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

}
