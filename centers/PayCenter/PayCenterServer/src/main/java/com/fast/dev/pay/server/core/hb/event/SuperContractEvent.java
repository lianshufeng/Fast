package com.fast.dev.pay.server.core.hb.event;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public abstract class SuperContractEvent extends ApplicationEvent {

    @Getter
    private boolean success;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SuperContractEvent(Object source) {
        super(source);
    }


    public SuperContractEvent success(boolean success) {
        this.success = success;
        return this;
    }


    /**
     * 取出任务
     *
     * @return
     */
    public HuaXiaEnterpriseTask getTask() {
        Object source = super.getSource();
        if (source == null) {
            return null;
        }
        return (HuaXiaEnterpriseTask) source;
    }


}
