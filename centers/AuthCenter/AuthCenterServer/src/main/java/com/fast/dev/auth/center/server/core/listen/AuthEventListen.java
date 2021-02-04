package com.fast.dev.auth.center.server.core.listen;

import com.fast.dev.auth.center.server.core.annotations.AuthEvent;
import com.fast.dev.auth.center.server.core.event.AuthPublishEvent;
import com.fast.dev.auth.center.server.core.helper.AuthEventOutputStreamHelper;
import com.fast.dev.core.event.method.MethodEvent;
import com.fast.dev.core.util.spring.SpringELUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Component
public class AuthEventListen implements ApplicationListener<AuthPublishEvent> {

    @Autowired
    private AuthEventOutputStreamHelper authEventOutputStreamHelper;

    @Override
    public void onApplicationEvent(AuthPublishEvent event) {
        MethodEvent.Source source = event.getSource();
        if (source.getCallType() == MethodEvent.CallType.After) {
            publishEvent(source);
        }
    }

    /**
     * 发布事件
     *
     * @param source
     */
    private void publishEvent(MethodEvent.Source source) {
        //动态执行表达式
        AuthEvent authEvent = source.getMethod().getAnnotation(AuthEvent.class);

        //条件过滤
        Boolean filter = SpringELUtil.parseExpression(new HashMap<String, Object>() {{
            put("ret", source.getRet());
        }}, authEvent.filter());
        if (filter == false) {
            return;
        }

        //取出参数
        Map<String, Object> parmMap = new HashMap<>();
        String[] parmNames = source.getParmName();
        Object[] parmValues = source.getParm();
        //结果集与参数
        parmMap.put("ret", source.getRet());
        for (int i = 0; i < parmNames.length; i++) {
            parmMap.put(parmNames[i], parmValues[i]);
        }


        //数据
        Map<String, Object> datas = new HashMap<>();
        for (String parm : authEvent.parm()) {
            //取出数据id
            Object val = SpringELUtil.parseExpression(parmMap, parm);
            datas.put(parm.replaceAll("#", ""), val);
        }

        //发布事件
        this.authEventOutputStreamHelper.publish(authEvent.type(), authEvent.action(), datas);
    }


}
