package com.fast.dev.core.mvc;

import com.fast.dev.core.endpoints.SuperEndpoints;
import com.fast.dev.core.util.result.InvokerExceptionResolver;
import com.fast.dev.core.util.result.InvokerResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Configuration
public class MVCResponseConfiguration {


    /**
     * 通用的异常捕获
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public InvokerExceptionResolver invokerExceptionResolver() {
        return new InvokerExceptionResolver();
    }

    @Bean
    public ResponseBodyAdvice responseBodyAdvice() {
        return new UserApiResponseBodyAdvice();
    }


    @RestControllerAdvice
    public class UserApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            return true;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            if (body instanceof InvokerResult) {
                return body;
            }
            String path = request.getURI().getPath();
            return StringUtils.hasText(path) && (path.indexOf("manager") > -1 || path.indexOf(SuperEndpoints.DefaultEndPointName) > -1 || path.indexOf("openapi") > -1) ? body : InvokerResult.notNull(body);
        }
    }
}
