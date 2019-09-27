package com.fast.components.robotfirewall.controller;

import com.fast.components.robotfirewall.helper.RobotFirewallHelper;
import com.fast.components.robotfirewall.model.RobotValidate;
import com.fast.components.robotfirewall.type.RobotType;
import com.fast.dev.core.util.spring.SpringELUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping
public class TestController {

    @Autowired
    private RobotFirewallHelper robotFirewallHelper;


    private Map<String, String> validateMap = new ConcurrentHashMap<>();


    @SneakyThrows
    @RequestMapping("test")
    public void test(HttpServletResponse response) {
        File file = new File("C:/output");
        File[] files = file.listFiles();
        File r = files[(int) (Math.random() * files.length)];
        System.out.println("file : " + r.getName());
        @Cleanup FileInputStream fileInputStream = new FileInputStream(r);
        robotFirewallHelper.get(RobotType.Rotation).build(fileInputStream, response.getOutputStream(), null);
    }


    @SneakyThrows
    @RequestMapping("get")
    public Object get() {
        File file = new File("C:/output");
        File[] files = file.listFiles();
        File r = files[(int) (Math.random() * files.length)];
        @Cleanup FileInputStream fileInputStream = new FileInputStream(r);
        @Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        RobotValidate robotRet = robotFirewallHelper.get(RobotType.Rotation).build(fileInputStream, byteArrayOutputStream, null);
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        validateMap.put(token, robotRet.getExpression());

        return new HashMap<String, Object>() {{
            put("token", token);
            put("image", "data:image/png;base64," + Base64Utils.encodeToString(byteArrayOutputStream.toByteArray()));
        }};
    }


    @SneakyThrows
    @RequestMapping("validate")
    public Object validate(String token, @RequestParam(defaultValue = "0") Integer code, @RequestParam(defaultValue = "0") Double time) {

        Map<String, Object> ret = new HashMap<>();
        String expression = this.validateMap.remove(token);


        boolean success = false;
        if (!StringUtils.hasText(expression)) {
            log.info("token不存在");
            success = false;
        } else {
            success = SpringELUtil.parseExpression(new HashMap<String, Object>() {{
                put("code", code);
                put("time", time);
            }}, expression);
            log.info("token 校验: " + success);
        }
        ret.put("success", success);
        return ret;
    }

}
