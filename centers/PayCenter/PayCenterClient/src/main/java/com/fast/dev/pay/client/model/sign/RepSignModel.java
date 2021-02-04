package com.fast.dev.pay.client.model.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回签名的结果
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepSignModel {

    /**
     * 签名后的数据
     */
    private String sign;

}
