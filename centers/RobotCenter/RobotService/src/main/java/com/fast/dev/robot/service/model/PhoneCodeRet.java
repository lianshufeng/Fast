package com.fast.dev.robot.service.model;

import com.fast.dev.robot.service.type.ValidateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneCodeRet {

    /**
     * 校验结果
     */
    private ValidateType validateType;


    //令牌
    private String token;


}
