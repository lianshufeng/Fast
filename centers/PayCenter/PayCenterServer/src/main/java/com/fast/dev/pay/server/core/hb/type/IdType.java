package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

@Getter
public enum IdType {

    IdCard("身份证", "01");


    private String remark;

    private String code;


    IdType(String remark, String code) {
        this.remark = remark;
        this.code = code;
    }
}
