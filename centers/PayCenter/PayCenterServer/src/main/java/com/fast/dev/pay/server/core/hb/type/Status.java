package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

/**
 * 交易状态
 */
public enum Status {

    SUCCESS("成功"),

    FAIL("失败"),

    PROCESSING("处理中"),

    UNKNOWN("已受理，待确认"),
    ;

    @Getter
    private String remkar;


    Status(String remkar) {
        this.remkar = remkar;
    }
}
