package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoChargeInfo {

    //扣款时间,单位是天,如：第1天，第2天
    @Indexed
    private int time;


    //金额,单位精确到分,如：500 = 5块
    private long amount;


    //扣款任务的id,可为null
    private String taskId;

}
