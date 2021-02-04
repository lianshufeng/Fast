package com.fast.dev.pay.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {


    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品备注
     */
    private String remark;


    /**
     * 商品图像
     */
    private String url;


    /**
     * 商品描述
     */
    private Map<String, Object> info;


}
