package com.fast.dev.ucenter.core.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document
public class UserLog extends SuperEntity {

    //名称
    @Indexed
    private String name;

    //时间
    private BigDecimal time;


}
