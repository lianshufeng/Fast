package com.fast.dev.pay.server.core.hb.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.server.core.hb.type.JournalState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * 华夏任务执行流水表
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseTaskJournal extends SuperEntity {

    //关联的合同
    @Indexed
    @DBRef(lazy = true)
    private HuaXiaEnterpriseAutoChargeContract contract;


    //关联的任务
    @Indexed
    @DBRef(lazy = true)
    private HuaXiaEnterpriseTask huaXiaEnterpriseTask;

    //执行时间
    @Indexed
    private long executeTime;

    //是否成功
    @Indexed
    private JournalState state;

    //仅失败才有失败原因
    private String failreason;

    //内容
    private Map<String, Object> items;


}
