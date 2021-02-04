package com.fast.dev.open.api.server.core.service;

import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

public abstract class BodyWriteService implements ResponseBodyAdvice<Object> {

    /**
     * 版本号
     *
     * @return
     */
    public abstract String version();


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

}
