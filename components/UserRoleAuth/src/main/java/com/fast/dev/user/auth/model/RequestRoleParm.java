package com.fast.dev.user.auth.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class RequestRoleParm {

    @NotNull(message = "角色名不能为空")
    private String roleName;


    //备注,描述
    private String remark;


    //权限名称
    private String[] auth;


    //角色的父类名称
    private String parentRoleName;


}
