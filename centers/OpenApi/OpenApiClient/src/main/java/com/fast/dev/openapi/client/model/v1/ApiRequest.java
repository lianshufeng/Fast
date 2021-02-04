package com.fast.dev.openapi.client.model.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiRequest {

    /**
     * 引用key
     */
    private String ak;


    /**
     * 加密的数据
     */
    private String data;


}
