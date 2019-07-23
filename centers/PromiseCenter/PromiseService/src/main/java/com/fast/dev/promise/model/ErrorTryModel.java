package com.fast.dev.promise.model;

import com.fast.dev.promise.type.CheckType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class ErrorTryModel {

    /**
     * 尝试次数
     */
    private Integer tryCount;

    /**
     * 延迟时间
     */
    private Long sleepTime;


    /**
     * 数据检查类型
     */
    private CheckType checkType;


    /**
     * 校验值
     */
    private Object checkValue;


}
