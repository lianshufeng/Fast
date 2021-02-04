package com.fast.dev.open.api.server.core.service.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.open.api.server.core.service.BodyWriteService;
import com.fast.dev.openapi.client.model.v1.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BodyWriteServiceV1Impl extends BodyWriteService {

    @Autowired
    private DBHelper dbHelper;

    @Override
    public String version() {
        return "/v1";
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        if (!(body instanceof ApiResponse)) {
            return body;
        }

        ApiResponse apiResponse = (ApiResponse) body;
        //设置系统时间
        apiResponse.setTime(dbHelper.getTime());
        //设置状态的描述
        if (apiResponse.getState() != null && !StringUtils.hasText(apiResponse.getMsg())) {
            apiResponse.setMsg(apiResponse.getState().getRemark());
        }
        return apiResponse;
    }
}
