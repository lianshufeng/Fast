package com.fast.dev.pay.server.core.hb.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.pay.server.core.hb.model.AutoChargeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuperAutoCharge extends SuperEntity {

    //代扣信息
    @Indexed
    private AutoChargeInfo[] autoChargeInfos;

    //备注,用于描述商品信息
    private String remark;



}
