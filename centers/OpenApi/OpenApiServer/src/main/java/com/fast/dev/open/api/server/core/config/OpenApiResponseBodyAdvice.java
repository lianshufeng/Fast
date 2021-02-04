package com.fast.dev.open.api.server.core.config;

import com.fast.dev.open.api.server.core.service.BodyWriteServiceManager;
import com.fast.dev.openapi.client.util.OpenApiUrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class OpenApiResponseBodyAdvice {


    @Bean
    public ResponseBodyAdvice responseBodyAdvice() {
        return new OpenApiResponseBodyAdvice.UserApiResponseBodyAdvice();
    }


    @RestControllerAdvice
    public class UserApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

        @Autowired
        private HttpServletRequest request;


        @Autowired
        private BodyWriteServiceManager bodyWriteServiceManager;


        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            return true;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            String url = this.request.getRequestURI();
            String version = OpenApiUrlUtil.getFirst(url);
            return bodyWriteServiceManager.beforeBodyWrite(version, body, returnType, selectedContentType, selectedConverterType, request, response);
        }
    }
}
