package com.fast.dev.pay.server.core.hb.model.task;

import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseTaskParameter;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Set;

@Data
public class HuaXiaEnterpriseCheckFinishTaskParameter extends HuaXiaEnterpriseTaskParameter {

    /**
     * 所有的扣款任务
     */
    @Indexed
    private Set<String> taskIds;

}
