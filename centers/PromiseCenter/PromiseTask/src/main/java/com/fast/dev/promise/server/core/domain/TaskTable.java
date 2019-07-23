package com.fast.dev.promise.server.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.promise.type.TaskState;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 任务模型
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
@ToString
public class TaskTable extends SuperEntity {

    @Indexed(unique = true)
    private String taskId;


    /**
     * 执行时间
     */
    @Indexed
    private Long executeTime;


    /**
     * 心跳记录时间
     */
    @Indexed
    private Long heartbeatTime;


    /**
     * http的任务
     */
    @DBRef(lazy = true)
    private HttpTable httpTable;


    /**
     * 错误模型
     */
    @DBRef(lazy = true)
    private ErrorTryTable errorTryTable;


    /**
     * 工作状态
     */
    @Indexed
    private TaskState taskState;


    /**
     * 构造方法
     *
     * @param id
     */
    public TaskTable(String id) {
        setId(id);
    }
}
