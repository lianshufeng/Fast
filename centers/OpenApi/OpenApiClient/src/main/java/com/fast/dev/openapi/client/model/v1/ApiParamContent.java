package com.fast.dev.openapi.client.model.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiParamContent {

    /**
     * 时间
     */
    private Long time;

    /**
     * 源数据
     */
    private Object body;


}
