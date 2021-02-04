package com.fast.dev.pay.server.core.general.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 企业账户
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseAccount extends SuperEntity {

    /**
     * 企业id
     */
    @Indexed(unique = true)
    private String enterpriseId;


}
