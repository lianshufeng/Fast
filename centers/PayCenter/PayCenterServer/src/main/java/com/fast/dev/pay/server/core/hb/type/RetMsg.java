package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

/**
 * 返回信息
 */
public enum RetMsg {


    Finish("处理完成", 0000),

    Fail("交易失败", 2000),

    Process("交易处理中", 2001),

    ParmError("请求参数错误", 9003),

    ;

    @Getter
    private String remark;

    @Getter
    private int code;

    RetMsg(String remark, int code) {
        this.remark = remark;
        this.code = code;
    }
}
