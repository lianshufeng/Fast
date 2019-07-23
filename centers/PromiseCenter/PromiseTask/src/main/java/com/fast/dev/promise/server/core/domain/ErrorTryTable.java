package com.fast.dev.promise.server.core.domain;


import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.promise.type.CheckType;
import com.fast.dev.promise.type.TaskState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorTryTable extends SuperEntity {


    @Indexed
    @DBRef(lazy = true)
    private TaskTable taskTable;


    /**
     * 任务状态,冗余字段
     */
    @Indexed
    private TaskState taskState;

    /**
     * 尝试次数
     */
    @Indexed
    private Integer tryCount;

    /**
     * 延迟时间
     */
    @Indexed
    private Long sleepTime;


    /**
     * 尝试任务时间
     */
    @Indexed
    private Long tryTime;


    /**
     * 数据检查类型
     */
    private CheckType checkType;


    /**
     * 校验值
     */
    private Object checkValue;


}
