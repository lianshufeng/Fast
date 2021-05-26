package com.fast.dev.pay.client.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Vector;

public class BankCardUtil {

    private static Vector<BankModel> vector = new Vector<>();
    static {

        vector.add(new BankModel().setName("邮储银行").setCode(100));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Accessors(chain = true)
    public static class  BankModel{

        private String name;

        private int code;


    }

}

