package com.fast.dev.pay.server.core.hb.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 华夏Api流水
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaApiJournal extends SuperEntity {


    //交易流水号
    @Indexed(unique = true)
    private String tradeNo;

    //交易时间
    @Indexed
    private String pcsDate;

    //方法名
    @Indexed
    private String method;

    //请求参数
    private String request;

    //响应参数
    private String response;

    //过期时间
    @Indexed(expireAfterSeconds = 0)
    private Date TTL;


}
