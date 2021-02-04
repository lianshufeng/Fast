package com.fast.dev.pay.server.core.hb.event;

public class ChargeTaskContractEvent extends SuperContractEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ChargeTaskContractEvent(Object source) {
        super(source);
    }
}
