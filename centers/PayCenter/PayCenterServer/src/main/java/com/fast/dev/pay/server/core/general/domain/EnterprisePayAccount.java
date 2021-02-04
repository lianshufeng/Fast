package com.fast.dev.pay.server.core.general.domain;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.pay.client.model.account.SuperAccount;
import com.fast.dev.pay.client.type.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class EnterprisePayAccount extends SuperEntity {

    /**
     * 企业id
     */
    @Indexed
    private String enterpriseId;


    /**
     * 禁用
     */
    @Indexed
    private boolean disable;

    /**
     * 企业账户的类型
     */
    @Indexed
    private AccountType accountType;


    /**
     * 账户字典
     */
    private Map<String, Object> account;


    /**
     * 最近的有效期证书
     */
    private long lastCertValidTime;


    /**
     * 备注
     */
    private String remark;

    /**
     * 获取账户信息
     *
     * @param <T>
     * @return
     */
    @SneakyThrows
    public <T> T readAccount() {
        return (T) JsonUtil.toObject(JsonUtil.toJson(account), this.accountType.getType());
    }

    /**
     * 写入账户信息
     *
     * @param superAccount
     * @return
     */
    @SneakyThrows
    public void writeAccount(SuperAccount superAccount) {
        this.account = JsonUtil.toObject(JsonUtil.toJson(superAccount), Map.class);
    }

}
