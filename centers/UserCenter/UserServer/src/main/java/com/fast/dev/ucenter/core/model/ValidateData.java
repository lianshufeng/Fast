package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.ServiceType;
import com.fast.dev.ucenter.core.type.ValidateCodeScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：练书锋
 * 时间：2018/8/30
 * 校验数据对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateData {


    /**
     * 数据值的范围
     */
    private ValidateCodeScope scope = ValidateCodeScope.Charset;


    /**
     * 长度
     */
    private int length = 6;


    /**
     * 额外的
     */
    private String[] extraCharset = new String[]{};


    /**
     * 具体校验的业务模版
     */
    private Map<ServiceType, String> template;


    /**
     * 是否需要加强验证
     */
    private boolean strongValidate = false;


}
