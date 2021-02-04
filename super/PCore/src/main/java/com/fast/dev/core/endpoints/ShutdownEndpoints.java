package com.fast.dev.core.endpoints;

import com.fast.dev.core.util.result.InvokerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全下线当前模块
 */
@RestController
public class ShutdownEndpoints extends SuperEndpoints {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("shutdown")
    public InvokerResult<Map<String, Object>> shutdown() {

        //启动另外的线程进行退出
        new Thread(new Runnable() {
            @Override
            public void run() {
                callExit();
            }
        }).start();

        return InvokerResult.success(new HashMap<String, Object>() {{
            put("time", System.currentTimeMillis());
            put("info", "offline");
        }});

    }

    private void callExit() {
//        closeEureka();
        closeApplicationContent();

    }

//    @Value("${eureka.client.service-url.defaultZone}")
//    private String defaultZone;
//
//    @Value("${spring.application.name}")
//    private String appName;
//
//
//    /**
//     * 关闭注册发现服务
//     */
//    private void closeEureka() {
//        StringBuffer hostUrl = new StringBuffer(defaultZone);
//        while (hostUrl.substring(hostUrl.length() - 1, hostUrl.length()).equals("/")) {
//            hostUrl = hostUrl.delete(hostUrl.length() - 1, hostUrl.length());
//        }
//        String appurl = String.format("%s/apps/%s", hostUrl, appName);
//
//        System.out.println(appurl);
//        new RestTemplate().delete(appurl);
//    }


    /**
     * 关闭上下文
     */
    private void closeApplicationContent() {
        SpringApplication.exit(applicationContext, new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                return 0;
            }
        });

        System.exit(0);
    }


}
