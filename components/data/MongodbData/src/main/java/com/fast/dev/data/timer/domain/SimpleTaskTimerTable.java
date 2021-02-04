package com.fast.dev.data.timer.domain;


import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 任务定时器
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public class SimpleTaskTimerTable extends SuperEntity {

    /**
     * cron表达式，如: 0/10 * * * * * , 每10秒执行一次
     */
    private String cron;

}
