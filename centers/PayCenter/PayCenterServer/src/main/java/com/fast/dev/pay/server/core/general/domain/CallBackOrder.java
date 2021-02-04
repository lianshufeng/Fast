package com.fast.dev.pay.server.core.general.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * 回调订单
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class CallBackOrder extends SuperEntity {

    //支付订单
    @Indexed
    @DBRef(lazy = true)
    private PayOrder payOrder;


    //回调数据
    private Map<String, Object> item;


}
