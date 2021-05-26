package com.fast.dev.pay.server.core.general.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankDictionaryModel {

    //银行信息
    private Map<String, BankInfo> info = new HashMap<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BankInfo {

        //银行编码
        private String code;

        //银行名称
        private String name;

    }


}
