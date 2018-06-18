package com.fast.dev.data.controller;

import com.fast.dev.data.util.SignUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class JrebelController {


    @RequestMapping("/jrebel/leases")
    public Object leases(HttpServletRequest request) {

        String clientRandomness = request.getParameter("randomness");
        String username = request.getParameter("username");
        String guid = request.getParameter("guid");

        boolean offline = Boolean.parseBoolean(request.getParameter("offline"));
        String validFrom = "null";
        String validUntil = "null";
        if (offline) {
            String clientTime = request.getParameter("clientTime");
            String offlineDays = request.getParameter("offlineDays");
            long clinetTimeUntil = Long.parseLong(clientTime) + 180L * 24 * 60 * 60 * 1000;
            validFrom = clientTime;
            validUntil = String.valueOf(clinetTimeUntil);
        }


        Map<String, Object> result = new HashMap<>();
        result.put("serverVersion", "3.2.4");
        result.put("serverProtocolVersion", "1.1");
        result.put("serverGuid", "a1b4aea8-b031-4302-b602-670a990272cb");
        result.put("groupType", "managed");
        result.put("id", 1);
        result.put("licenseType", 1);
        result.put("evaluationLicense", false);
        result.put("signature",
                "OJE9wGg2xncSb+VgnYT+9HGCFaLOk28tneMFhCbpVMKoC/Iq4LuaDKPirBjG4o394/UjCDGgTBpIrzcXNPdVxVr8PnQzpy7ZSToGO8wv/KIWZT9/ba7bDbA8/RZ4B37YkCeXhjaixpmoyz/CIZMnei4q7oWR7DYUOlOcEWDQhiY=");
        result.put("serverRandomness", "H2ulzLlh7E0=");
        result.put("seatPoolType", "standalone");
        result.put("statusCode", "SUCCESS");
        result.put("offline", offline);
        result.put("validFrom", null);
        result.put("validUntil", null);
        result.put("company", username);
        result.put("orderId", "");
        result.put("zeroIds", new String[]{});
        result.put("licenseValidFrom", 1490544001000l);
        result.put("licenseValidUntil", 1691839999000l);
        result.put("signature", signature(clientRandomness, guid, offline, validFrom, validUntil));



        return result;
    }


    private Object signature(String clientRandomness, String guid, boolean offline, String validFrom, String validUntil) {

        //String serverRandomness = ByteUtil.a(ByteUtil.a(8));
        String serverRandomness = "H2ulzLlh7E0="; //服务端随机数,如果要自己生成，务必将其写到json的serverRandomness中
        String installationGuidString = guid;
        //String value = String.valueOf("false");
        String s2 = "";
        if (offline) {
            s2 = StringUtils.join((Object[]) new String[]{clientRandomness, serverRandomness, installationGuidString, String.valueOf(offline), validFrom, validUntil}, ';');
        } else {
            s2 = StringUtils.join((Object[]) new String[]{clientRandomness, serverRandomness, installationGuidString, String.valueOf(offline)}, ';');
        }
        final byte[] a2 = SignUtil.sign(s2.getBytes());
        return Base64.getEncoder().encodeToString(a2);
    }





}
