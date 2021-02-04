package com.fast.dev.pay.server.core.hb.model;

import com.fast.dev.pay.server.core.hb.type.ContractFreezeState;
import com.fast.dev.pay.server.core.hb.type.ContractState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractRequestModel {


    //扣款人姓名
    private String userName;

    //扣款人电话
    private String userPhone;

    //消费者电话
    private String consumePhone;

    //订单状态
    private OrderState orderState;

    //冻结状态
    private FreezeState freezeState;

    //扣款状态
    private ChargeState chargeState;

    //开始冻结时间
    private Long startFreezeTime;
    //结束冻结时间
    private Long endFreezeTime;


    //开始扣款时间
    private Long startChargeTime;
    //结束扣款的时间
    private Long endChargeTime;


    /**
     * 订单状态
     */
    public static enum OrderState {
        //进行中
        Process(new ContractState[]{ContractState.WaitFreeze, ContractState.WaitCharge}),
        //完成
        Finish(new ContractState[]{ContractState.Finish}),
        //终止
        Cancel(new ContractState[]{ContractState.Cancel});

        //合同状态
        @Getter
        private ContractState[] state;

        //构建
        public static OrderState build(ContractState state) {
            for (OrderState value : OrderState.values()) {
                for (ContractState contractState : value.getState()) {
                    if (state == contractState) {
                        return value;
                    }
                }
            }
            return null;
        }


        OrderState(ContractState[] state) {
            this.state = state;
        }
    }

    //扣款状态
    public static enum ChargeState {
        //正常
        Success(new com.fast.dev.pay.server.core.hb.type.ChargeState[]{com.fast.dev.pay.server.core.hb.type.ChargeState.Success, com.fast.dev.pay.server.core.hb.type.ChargeState.WaitCheck}),
        //失败
        Fail(new com.fast.dev.pay.server.core.hb.type.ChargeState[]{com.fast.dev.pay.server.core.hb.type.ChargeState.Fail}),

        ;


        //构建
        public static ChargeState build(com.fast.dev.pay.server.core.hb.type.ChargeState state) {
            for (ChargeState value : ChargeState.values()) {
                for (com.fast.dev.pay.server.core.hb.type.ChargeState chargeState : value.getState()) {
                    if (state == chargeState) {
                        return value;
                    }
                }
            }
            return null;
        }


        @Getter
        private com.fast.dev.pay.server.core.hb.type.ChargeState[] state;

        ChargeState(com.fast.dev.pay.server.core.hb.type.ChargeState[] state) {
            this.state = state;
        }
    }


    //冻结状态
    public static enum FreezeState {
        //正常
        Success(new ContractFreezeState[]{ContractFreezeState.Finish, ContractFreezeState.Wait}),
        //失败
        Fail(new ContractFreezeState[]{ContractFreezeState.Fail}),

        ;


        //构建
        public static FreezeState build(ContractFreezeState state) {
            for (FreezeState value : FreezeState.values()) {
                for (ContractFreezeState freezeState : value.getState()) {
                    if (state == freezeState) {
                        return value;
                    }
                }
            }
            return null;
        }

        @Getter
        private ContractFreezeState[] state;

        FreezeState(ContractFreezeState[] state) {
            this.state = state;
        }
    }


}
