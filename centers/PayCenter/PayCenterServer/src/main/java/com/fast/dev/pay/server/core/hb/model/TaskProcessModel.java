package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskProcessModel {

    //会话
    @Indexed
    private String session;

    //工作日,工具类取时间
    @Indexed
    private long workTime;

}
