package com.fast.dev.core.helper;


import lombok.experimental.Delegate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

public class ViewHelper {

    //上下文
    private final static String Context = "context";


    @Value("${spring.application.name:''}")
    private String applicationName;

    @Autowired
    private Environment environment;


    @Autowired
    @Delegate(types = ApplicationContextDelegate.class)
    private ApplicationContext applicationContext;

    private int localPort = 0;

    @PostConstruct
    private void init() {
        Optional.ofNullable(environment.getProperty("server.port")).ifPresent((it) -> {
            localPort = Integer.valueOf(it);
        });
    }


    /**
     * 代理ApplicationContext的方法
     */
    private interface ApplicationContextDelegate {
        Object getBean(String name) throws BeansException;

        <T> T getBean(String name, Class<T> requiredType) throws BeansException;
    }


    /**
     * 获取请求
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


//    /**
//     * 获取请求的url
//     *
//     * @return
//     */
//    public String getRemoteRequestHost() {
//        //取出请求
//        HttpServletRequest request = getRequest();
//        //请求的接口
//        int requestPort = getPortFromHost(request.getHeader("Host"));
//        return request.getScheme() + "://" + request.getRemoteHost() + (requestPort == 80 ? "" : ":" + requestPort);
//    }

    /**
     * 获取客户端的请求的host
     *
     * @return
     */
    public String getRemoteHost() {
        //取出请求
        HttpServletRequest request = getRequest();
        return request.getScheme() + "://" + request.getHeader("Host");
    }


    /**
     * 构建模型
     *
     * @return
     */
    public ModelMap buildModel() {
        //取出请求
        HttpServletRequest request = getRequest();
        //请求的接口
        int requestPort = getPortFromHost(request.getHeader("Host"));
        //参数模型
        ModelMap modelMap = new ModelMap();

        //当前应用名
        modelMap.put("applicationName", this.applicationName);
        //环境变量
        modelMap.put("environment", this.environment);

        //上下文,考虑是否网关转发的情况
        modelMap.put(Context, requestPort == localPort ? "" : "/" + applicationName);
        modelMap.put("viewHelper", this);
        return modelMap;
    }


    /**
     * 构建模型和视图
     *
     * @param viewName
     * @return
     */
    public ModelAndView buildModelAndView(String viewName) {
        return buildModelAndView(viewName, null);
    }

    /**
     * 构建视图模型
     *
     * @param viewName
     * @param modelMap
     * @return
     */
    public ModelAndView buildModelAndView(String viewName, Map<String, Object> modelMap) {
        ModelAndView modelAndView = new ModelAndView();

        //视图
        Optional.ofNullable(viewName).ifPresent((it) -> {
            modelAndView.setViewName(it);
        });

        //属性
        Optional.ofNullable(modelMap).ifPresent((it) -> {
            modelAndView.addAllObjects(it);
        });

        modelAndView.addAllObjects(buildModel());
        return modelAndView;
    }


    /**
     * 构建视图模型
     *
     * @return
     */
    public ModelAndView buildModelAndView() {
        return buildModelAndView(null, null);
    }


    /**
     * 取出Host中的端口
     *
     * @param host
     * @return
     */
    private int getPortFromHost(String host) {
        if (host.indexOf(":") < 1) {
            return 80;
        }
        return Integer.parseInt(host.split(":")[1]);
    }


}
