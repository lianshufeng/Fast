package com.fast.dev.ucenter.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 禁用用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDisableModel implements Serializable {

    //到期时间
    private long disableTime;

    //原因
    private String reason;

    //禁用时间
    private long createTime;

}
