package com.fast.dev.pay.server.core.hb.event;

import org.springframework.context.ApplicationEvent;

public class CheckChargeTaskContractEvent  extends SuperContractEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public CheckChargeTaskContractEvent(Object source) {
        super(source);
    }
}
