package com.fast.dev.pay.server.core.general.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 流水账
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class JournalAccount extends SuperEntity {


    /**
     * 关联到钱包
     */
    @Indexed
    @DBRef(lazy = true)
    private UserAccount userAccount;

}
