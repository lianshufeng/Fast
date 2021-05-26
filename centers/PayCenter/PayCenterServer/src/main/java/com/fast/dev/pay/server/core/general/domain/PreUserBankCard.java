package com.fast.dev.pay.server.core.general.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 预绑定用户银行卡
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class PreUserBankCard extends UserBankCard {





    //过期自动删除
    @Indexed(expireAfterSeconds = 0)
    private Date TTL;

}
