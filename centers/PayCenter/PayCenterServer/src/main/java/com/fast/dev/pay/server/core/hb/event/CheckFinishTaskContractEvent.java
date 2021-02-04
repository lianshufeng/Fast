package com.fast.dev.pay.server.core.hb.event;

/**
 * 合同事件
 */
public class CheckFinishTaskContractEvent extends SuperContractEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public CheckFinishTaskContractEvent(Object source) {
        super(source);
    }


}
