package com.fast.dev.core.boot.info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class ActuatorInfoController {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    @Value("${spring.application.name}")
    private String applicationName;


    @RequestMapping("actuator/info")
    public Object info() {
        Map<String, Object> m = new HashMap<>();
        m.put("time", simpleDateFormat.format(new Date(System.currentTimeMillis())));
        m.put("ApplicationName", applicationName);
        return m;
    }


}
