package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

/***
 * 工作模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkTaskModel {

    //会话
    @Indexed
    private String session;

    //工作日,工具类取时间
    @Indexed
    private String workDay;


}
