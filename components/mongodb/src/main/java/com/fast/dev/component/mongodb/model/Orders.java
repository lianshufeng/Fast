package com.fast.dev.component.mongodb.model;

/**
 * 排序
 * 
 * @作者 练书锋
 * @时间 2016年9月22日
 *
 */
public class Orders {

	// 字段名
	private String name;
	// 倒序
	private boolean desc;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}

	public Orders() {
		// TODO Auto-generated constructor stub
	}

	public Orders(String name, boolean desc) {
		super();
		this.name = name;
		this.desc = desc;
	}

}
