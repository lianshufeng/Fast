package com.fast.dev.pay.server.core.hb.model;

import com.fast.dev.pay.server.core.hb.type.ContractFreezeState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * 合同冻结
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractFreezeModel {

    //冻结时间
    @Indexed
    private long time;

    //冻结状态
    @Indexed
    private ContractFreezeState state;


}
