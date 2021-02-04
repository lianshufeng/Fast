package com.fast.dev.auth.client.type;

import lombok.Getter;

public enum ResultState {

    Success("成功"),

    Fail("失败"),

    Exception("服务器异常"),

    EnterpriseNotExist("企业不存在"),

    EnterpriseExist("企业已存在"),

    UserNotExist("用户不存在"),

    RoleNameNotNull("角色名不能为空"),

    AuthNameNotAllow("不允许的权限名"),

    UserInRole("用户在角色中"),

    RoleNameExist("角色名已存在"),

    NotExist("不存在"),

    Exist("已存在"),

    RoleNotExist("角色不存在"),

    ParmNotExist("参数不能为空"),


    FamilyNotExist("家庭不存在"),

    UserExistOtherFamily("用户存在其他家庭组"),

    UserExistFamily("用户存在家庭"),

    UserNotExistUCenter("用户不存在用户中心"),

    ;

    ResultState(String remark) {
        this.remark = remark;
    }

    @Getter
    private String remark;

}
