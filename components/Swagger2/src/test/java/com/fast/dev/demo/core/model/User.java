package com.fast.dev.demo.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户名
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    //用户名
    private String userName;

}
