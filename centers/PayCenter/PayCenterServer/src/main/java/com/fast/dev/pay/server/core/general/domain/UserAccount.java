package com.fast.dev.pay.server.core.general.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * 用户账户
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount extends SuperEntity {


    /**
     * 企业id
     */
    @Indexed
    private String enterpriseId;


    /**
     * 用户中心的id
     */
    @Indexed
    private String uid;


    /**
     * 金额
     */
    @Indexed
    private BigDecimal balance;


}
