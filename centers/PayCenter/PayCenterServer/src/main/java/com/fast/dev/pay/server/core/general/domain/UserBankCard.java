package com.fast.dev.pay.server.core.general.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "uid_account_payid", def = "{'uid' : 1, 'accountNumber': 1 , 'payAccount': 1}", unique = true)
})
public class UserBankCard extends SuperEntity {

    //用户中心的id
    @Indexed
    private String uid;

    //账户号码
    @Indexed
    private String accountNumber;

    //账户名称
    @Indexed
    private String accountName;

    //身份证
    @Indexed
    private String identificationNumber;

    //手机号码
    @Indexed
    private String phoneNumber;

    //绑定的企业id
    @Indexed
    private String payAccountId;

    //是否删除
    @Indexed
    private boolean delete;

    //中金银行绑定流水号
    private String txSNBinding;

}
