package com.fast.dev.auth.client.log.helper.impl;

import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.auth.client.log.model.UserLogModel;
import com.fast.dev.auth.client.log.service.UserLogService;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.core.event.method.MethodEvent;
import com.fast.dev.core.util.net.IPUtil;
import com.fast.dev.core.util.spring.SpringELUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class UserLogHelperImpl implements UserLogHelper {


    private ThreadLocal<Stack<ThreadModel>> threadModelThreadLocal = new ThreadLocal<>();


    @Autowired
    private UserLogService userLogService;

    @Autowired
    private AuthClientUserHelper userHelper;

    @Value("${spring.application.name}")
    private String applicationName;


    //线程池
    private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);


    @PreDestroy
    private void shutdown() {
        threadPool.shutdownNow();
    }


    /**
     * 初始事件
     */
    public void preEvent() {
        threadModelThreadLocal.set(new Stack<>());
    }


    /**
     * 销毁事件
     */
    public void afterEvent() {
        threadModelThreadLocal.remove();
    }


    /**
     * 初始化当前线程
     */
    public void end(MethodEvent.Source source) {
        if (threadModelThreadLocal.get() == null) {
            return;
        }
        //request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //方法
        Method method = source.getMethod();

        //取出注解
        UserLog userLogAnnotation = AnnotationUtils.findAnnotation(method, UserLog.class);

        //取出注解
        final Optional<UserLog> userLog = userLogAnnotation == null ? Optional.empty() : Optional.of(userLogAnnotation);

        final UserLogModel userLogModel = new UserLogModel();
        //应用名
        userLogModel.setAppName(applicationName);
        //设置访问时间
        userLogModel.setAccessTime(System.currentTimeMillis());
        //UA
        userLogModel.setUa(request.getHeader("User-Agent"));
        //用户ip
        userLogModel.setIp(IPUtil.getRemoteIp(request));
        //当前用户
        currentUser(userLogModel);

        //当前线程的当前方法
        ThreadModel threadModel = threadModelThreadLocal.get().peek();
        //参数
        userLogModel.setItems(threadModel.getItems());
        //消耗时间
        userLogModel.setCostTime(System.currentTimeMillis() - threadModel.getStartTime());

        //启动线程
        this.threadPool.execute(() -> {
            //方法名
            userLogModel.setMethod(methodName(method));
            //是否有用户注解
            userLog.ifPresent((it) -> {
                userLogModel.setAction(it.action());
                //参数
                userLogModel.setParameter(getRequestParameter(source, it.parameter()));
            });

            //推送到权限中心的日志里
            userLogService.push(userLogModel);
        });

    }

    /**
     * 重置
     */
    public void start(MethodEvent.Source source) {
        Optional.ofNullable(threadModelThreadLocal.get()).ifPresent((it) -> {
            it.push(new ThreadModel());
        });
    }


    @Override
    public void log(String key, Object value) {
        Stack<ThreadModel> stack = threadModelThreadLocal.get();
        if (stack.size() == 0) {
            return;
        }
        stack.peek().getItems().put(key, value);
    }


    /**
     * 获取保存的日志
     *
     * @return
     */
    public Map<String, Object> getLog() {
        Stack<ThreadModel> stack = threadModelThreadLocal.get();
        if (stack == null) {
            return null;
        }
        return new HashMap<>(stack.peek().getItems());
    }


    /**
     * 获取当前登录用户的id
     *
     * @return
     */
    private void currentUser(UserLogModel userLogMode) {
        this.userHelper.getUserOptional().ifPresent((userParmModel) -> {
            BeanUtils.copyProperties(userParmModel, userLogMode);
        });
    }


    /**
     * 获取当前的请求参数
     *
     * @return
     */
    private Map<String, Object> getRequestParameter(MethodEvent.Source source, String[] parameters) {

        //请求的参数
        var methodParm = new HashMap<>();
        String[] parmNames = source.getParmName();
        Object[] parms = source.getParm();
        for (int i = 0; i < parmNames.length; i++) {
            methodParm.put(parmNames[i], parms[i]);
        }

        //返回的结果集
        Map<String, Object> ret = new HashMap<>();
        if (parameters != null && parameters.length > 0) {
            for (String parameter : parameters) {
                String key = parameter.replaceAll("\\#", "").replaceAll("\\$", "").replaceAll("\\.", "");
                Object value = SpringELUtil.parseExpression(methodParm, parameter);
                ret.put(key, value);
            }
        }
        return ret;
    }


    /**
     * 方法转换为方法名
     *
     * @param method
     * @return
     */
    private static String methodName(Method method) {
        return method.getDeclaringClass().getTypeName() + "." + method.getName();
    }

    @Data
    static class ThreadModel {
        //项
        private Map<String, Object> items = new ConcurrentHashMap<>();

        //记录开始时间
        private long startTime = System.currentTimeMillis();

    }


}
