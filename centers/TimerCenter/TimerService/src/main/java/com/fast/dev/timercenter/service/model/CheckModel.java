package com.fast.dev.timercenter.service.model;

import com.fast.dev.timercenter.service.type.CheckType;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class CheckModel {


    /**
     * 数据检查类型
     */
    private CheckType checkType;


    /**
     * 校验值
     */
    private Object checkValue;


}
