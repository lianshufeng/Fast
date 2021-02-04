package com.fast.dev.auth.client.log.listen;

import com.fast.dev.auth.client.log.event.UserLogAnnotationEvent;
import com.fast.dev.auth.client.log.helper.impl.UserLogHelperImpl;
import com.fast.dev.core.event.method.MethodEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserLogListen implements ApplicationListener<UserLogAnnotationEvent> {


    @Autowired
    private UserLogHelperImpl userLogHelper;


    @Override
    public void onApplicationEvent(UserLogAnnotationEvent userLogAnnotationEvent) {
        MethodEvent.Source source = userLogAnnotationEvent.getSource();
        if (source.getCallType() == MethodEvent.CallType.Before) {
            before(source);
        } else if (source.getCallType() == MethodEvent.CallType.After) {
            after(source);
        }
    }

    /**
     * 执行之前
     */
    private void before(MethodEvent.Source source) {
        this.userLogHelper.start(source);
    }

    /**
     * 执行之后
     */
    private void after(MethodEvent.Source source) {
        this.userLogHelper.end(source);
    }


}
