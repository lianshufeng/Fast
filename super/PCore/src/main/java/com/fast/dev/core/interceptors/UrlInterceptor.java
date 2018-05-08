package com.fast.dev.core.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * url拦截器，请实现本接口，将自动注册到spring的MVC中
 * 
 * @作者: 练书锋
 * @联系: 251708339@qq.com
 */
public interface UrlInterceptor extends HandlerInterceptor {

	/**
	 * 添加需要拦截的URL
	 * 
	 * @return
	 */
	public String[] addPathPatterns();

	/**
	 * 排除不拦截的URL
	 * 
	 * @return
	 */
	public String[] excludePathPatterns();

	/**
	 * 优先级，取之返回负整数到整数，按照从小到大依次排序
	 * 
	 * @return
	 */
	public int level();

}
