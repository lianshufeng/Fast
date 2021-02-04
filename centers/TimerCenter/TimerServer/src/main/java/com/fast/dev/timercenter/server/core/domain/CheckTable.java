package com.fast.dev.timercenter.server.core.domain;


import com.fast.dev.timercenter.service.type.CheckType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckTable {


    /**
     * 数据检查类型
     */
    private CheckType checkType;


    /**
     * 校验值
     */
    private Object checkValue;


}
