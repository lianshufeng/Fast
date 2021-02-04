package com.fast.dev.pay.server.core.hb.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class UserMobileValidate extends SuperEntity {


    @Indexed
    private String phone;

    private String code;

    @Indexed(expireAfterSeconds = 0)
    private Date ttl;



}
