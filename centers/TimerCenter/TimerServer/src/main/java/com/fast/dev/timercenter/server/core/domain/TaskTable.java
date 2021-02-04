package com.fast.dev.timercenter.server.core.domain;

import com.fast.dev.core.util.net.apache.HttpModel;
import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 任务模型
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class TaskTable extends SuperEntity {

    /**
     * 资源令牌
     */
    @Indexed
    private String resourceToken;


    /**
     * 任务id
     */
    @Indexed(unique = true)
    private String taskId;


    /**
     * 延迟执行时间
     */
    @Indexed
    private Long delayTime;


    /**
     * 心跳记录时间
     */
    @Indexed
    private Long heartbeatTime;


    /**
     * http的任务
     */
    @Indexed
    private HttpModel httpModel;


    /**
     * 错误模型
     */
    @Indexed
    private CheckTable checkTable;


    /**
     * 尝试次数
     */
    @Indexed
    private Integer tryCount ;


}
