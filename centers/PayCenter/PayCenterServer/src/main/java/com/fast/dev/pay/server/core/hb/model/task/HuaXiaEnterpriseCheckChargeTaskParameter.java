package com.fast.dev.pay.server.core.hb.model.task;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseTaskParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseCheckChargeTaskParameter extends HuaXiaEnterpriseTaskParameter {

    //扣款的任务
    @Indexed
    private String chargeTaskId;

    //扣款的流水
    @Indexed
    private String taskJournalId;


}
