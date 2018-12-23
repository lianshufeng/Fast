package com.fast.dev.core.util.result;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 * 处理统一用的系统异常
 */
public class InvokerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        e.printStackTrace();
        ModelAndView mv = new ModelAndView();
        mv.addObject("state", InvokerState.Exception);
        mv.setView(new MappingJackson2JsonView());
        Map<String, Object> exception = new HashMap<>();
        exception.put("type", e.getClass().getSimpleName());
        exception.put("class", e.getClass().getName());
        exception.put("message", e.getMessage());
        mv.addObject("exception", exception);
        return mv;
    }
}
