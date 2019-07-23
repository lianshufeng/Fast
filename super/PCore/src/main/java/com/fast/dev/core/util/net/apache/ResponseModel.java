package com.fast.dev.core.util.net.apache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel {

    /**
     * 响应编码
     */
    private int code;


    /**
     * header
     */
    private Map<String, Set<Object>> headers;

    /**
     * body
     */
    private Object body;


}
