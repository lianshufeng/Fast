package com.fast.dev.auth.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseModelAndSK extends EnterpriseModel {

    //秘钥
    private String sk;

}
