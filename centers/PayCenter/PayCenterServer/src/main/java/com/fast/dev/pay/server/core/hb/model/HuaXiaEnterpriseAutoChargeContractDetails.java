package com.fast.dev.pay.server.core.hb.model;

import com.fast.dev.pay.server.core.hb.type.JournalState;
import com.fast.dev.pay.server.core.hb.type.TaskState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 订单详情
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseAutoChargeContractDetails {

    //扣款人姓名
    private String userName;

    //扣款人电话
    private String userPhone;

    //消费者电话
    private String consumePhone;

    //套餐名称，如果为空则为自定义套餐
    private String orderName;

    //总金额
    private long totalAmount;

    //冻结任务
    private FreezeTask freezeTask;

    //扣款信息
    private List<ChargeTask> chargeTasks;


    //备注
    private String remark;


    /**
     * 冻结任务
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FreezeTask {

        //扣款任务的id
        private String taskId;

        //扣款时间
        private Long time;

        //任务状态
        private TaskState state;

        //流水
        private List<FreezeJournal> journals;


    }

    /**
     * 冻结任务流水
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FreezeJournal {

        //扣款时间
        private Long time;


        //是否成功
        private JournalState state;


    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChargeTask {

        //扣款任务的id
        private String taskId;

        //扣款时间
        private Long time;

        //金额
        private long amount;

        //支付金额
        private long paymentAmount;

        //任务状态
        private TaskState state;

        //流水
        private List<ChargeJournal> journals;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChargeJournal {

        //扣款时间
        private Long time;

        //金额
        private long amount;

        //是否成功
        private JournalState state;

    }


}
