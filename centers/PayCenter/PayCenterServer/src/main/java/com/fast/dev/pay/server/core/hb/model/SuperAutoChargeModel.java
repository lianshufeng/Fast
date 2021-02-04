package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuperAutoChargeModel {

    //代扣信息
    private AutoChargeInfo[] autoChargeInfos;

    //备注,用于描述商品信息
    private String remark;


}
