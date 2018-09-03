package com.fast.dev.pushcenter.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/9/3
 * 平台消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformMessage extends BaseMessage {


    /**
     * 平台号码
     */
    private String[] number;


}
