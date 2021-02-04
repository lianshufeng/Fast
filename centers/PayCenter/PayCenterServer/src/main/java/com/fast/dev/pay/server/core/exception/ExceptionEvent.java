package com.fast.dev.pay.server.core.exception;

import org.springframework.context.ApplicationEvent;

public class ExceptionEvent extends ApplicationEvent {

    public ExceptionEvent(PostException.ExceptionInfo source) {
        super(source);
    }
}
