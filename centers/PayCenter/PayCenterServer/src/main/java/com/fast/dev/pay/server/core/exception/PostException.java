package com.fast.dev.pay.server.core.exception;

import com.fast.dev.core.util.result.InvokerExceptionResolver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 发送exception
 */
@Component
public class PostException extends InvokerExceptionResolver {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        ModelAndView modelAndView = super.resolveException(httpServletRequest, httpServletResponse, o, e);

        ExceptionInfo exceptionInfo = new ExceptionInfo();
        exceptionInfo.setModelAndView(modelAndView);
        exceptionInfo.setO(o);
        exceptionInfo.setE(e);

        //全局通知异常
        this.applicationContext.publishEvent(new ExceptionEvent(exceptionInfo));

        return modelAndView;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExceptionInfo {

        private ModelAndView modelAndView;

        private Object o;

        private Exception e;


    }


}
