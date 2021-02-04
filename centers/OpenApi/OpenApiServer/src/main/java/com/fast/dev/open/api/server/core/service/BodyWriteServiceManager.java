package com.fast.dev.open.api.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BodyWriteServiceManager {

    private Map<String, BodyWriteService> cache = new ConcurrentHashMap<>();


    @Autowired
    private void init(ApplicationContext applicationContext) {
        cache.clear();
        applicationContext.getBeansOfType(BodyWriteService.class).values().forEach((it) -> {
            cache.put(it.version(), it);
        });
    }


    /**
     * 处理参数写入
     *
     * @param version
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */
    public Object beforeBodyWrite(final String version, Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        BodyWriteService bodyWriteService = cache.get(version);
        if (bodyWriteService == null) {
            return body;
        }
        return bodyWriteService.beforeBodyWrite(body, returnType, selectedContentType, selectedConverterType, request, response);
    }


}
