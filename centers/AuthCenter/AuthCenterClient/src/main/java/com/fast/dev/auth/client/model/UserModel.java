package com.fast.dev.auth.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
public class UserModel {

    /**
     * 用户中心的id
     */
    private String uid;


    /**
     * 扩展信息
     */
    private Map<String, String> info;

}
