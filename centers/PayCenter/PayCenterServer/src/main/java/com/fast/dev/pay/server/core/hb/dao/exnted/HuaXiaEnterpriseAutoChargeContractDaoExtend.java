package com.fast.dev.pay.server.core.hb.dao.exnted;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeContract;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeTemplate;
import com.fast.dev.pay.server.core.hb.model.*;
import com.fast.dev.pay.server.core.hb.type.ContractState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HuaXiaEnterpriseAutoChargeContractDaoExtend {

    /**
     * 更新冻结状态
     *
     * @param id
     * @param contractFreeze
     * @return
     */
    boolean updateFreeze(String id, ContractFreezeModel contractFreeze);


    /**
     * 更新状态
     *
     * @param id
     * @param state
     * @return
     */
    boolean updateState(String id, ContractState state);


    /**
     * 更新最后一次扣款信息
     *
     * @param id
     * @param contractModel
     * @return
     */
    boolean updateLastChargeContract(String id, ChargeContractModel contractModel);

    /**
     * 更新扣款信息
     *
     * @param id
     * @param autoChargeModel
     * @return
     */
    boolean updateAutoCharge(String id, SuperAutoChargeModel autoChargeModel);

    /**
     * 查询合同列表
     *
     * @param pageable
     * @return
     */
    Page<HuaXiaEnterpriseAutoChargeContract> list(String epId, ContractRequestModel requestModel, Pageable pageable);


    /**
     * 消费者是否存在此合同
     *
     * @param consumePhone
     * @param template
     * @return
     */
    boolean existsUserChargeContract(String consumePhone, HuaXiaEnterpriseAutoChargeTemplate template);

    long countContract(Long startTime,Long endTime,String epId);


}
