package com.fast.dev.ucenter.core.domain;


import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document
public class RequestUrlLog extends SuperEntity {


    //远程ip
    @Indexed
    private String remoteIp;

    //请求的url
    @Indexed
    private String uri;

    //传递的参数
    @Indexed
    private String queryString;


    //具体的参数
    private Object parameter;


    //耗时
    private long usedTime;


}
