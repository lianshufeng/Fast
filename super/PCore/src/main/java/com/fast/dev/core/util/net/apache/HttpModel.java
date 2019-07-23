package com.fast.dev.core.util.net.apache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class HttpModel {


    /**
     * Url地址
     */
    private String url;


    /**
     * 网络请求方式
     */
    private MethodType method;


    /**
     * 请求头
     */
    private Map<String, Object> header;


    /**
     * 请求体，仅为post生效
     */
    private Object body;


    /**
     * 请求编码
     */
    private String charset;

}
