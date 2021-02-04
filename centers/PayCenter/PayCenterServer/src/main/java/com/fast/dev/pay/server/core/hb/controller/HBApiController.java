package com.fast.dev.pay.server.core.hb.controller;

import com.fast.dev.pay.server.core.hb.model.req.ConfrimOpenAcctReq;
import com.fast.dev.pay.server.core.hb.model.req.FreezeAcctReq;
import com.fast.dev.pay.server.core.hb.model.req.OpenAcctReq;
import com.fast.dev.pay.server.core.hb.model.req.ThawPayRefundReq;
import com.fast.dev.pay.server.core.hb.service.HBApiService;
import com.fast.dev.ucenter.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 华夏apr
 */
@RestController
@RequestMapping("hb/api")
public class HBApiController {

    @Autowired
    private HBApiService hbApiService;

    @RequestMapping("openAcct")
    private Object openAcct(String userMobile) throws Exception {

        ZonedDateTime zdt = ZonedDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        OpenAcctReq req = new OpenAcctReq();
        req.setUserMobile(userMobile);
        req.setPcsDate(formatter.format(zdt));
        req.setTradeNo(formatter.format(zdt) + RandomUtil.randInt(10000,99999));
        FileInputStream jks = new FileInputStream("D:/hb/kayak.jks");
        byte[] byt = new byte[jks.available()];
        jks.read(byt);

        return hbApiService.openAcct(req,byt,"hmjy1234");
    }

    @RequestMapping("confrimOpenAcct")
    private Object confrimOpenAcct(ConfrimOpenAcctReq req) throws Exception {
        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        req.setPcsDate(formatter.format(zdt));
        req.setTradeNo(formatter.format(zdt) + RandomUtil.randInt(10000,99999));
        FileInputStream jks = new FileInputStream("D:/hb/ayhm.jks");
        byte[] byt = new byte[jks.available()];
        jks.read(byt);
        return hbApiService.confrimOpenAcct(req,byt,"433471");
    }

    @RequestMapping("freezeAcct")
    private Object freezeAcct(FreezeAcctReq req) throws Exception{
        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        req.setPcsDate(formatter.format(zdt));
        req.setTradeNo(formatter.format(zdt) + RandomUtil.randInt(10000,99999));
        FileInputStream jks = new FileInputStream("D:/hb/ayhm.jks");
        byte[] byt = new byte[jks.available()];
        jks.read(byt);
        return hbApiService.freezeAcct(req,byt,"433471");
    }

    @RequestMapping("thawPayRefund")
    private Object thawPayRefund(ThawPayRefundReq req) throws Exception{
        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        req.setPcsDate(formatter.format(zdt));
        req.setTradeNo(formatter.format(zdt) + RandomUtil.randInt(10000,99999));
        FileInputStream jks = new FileInputStream("D:/hb/ayhm.jks");
        byte[] byt = new byte[jks.available()];
        jks.read(byt);
        return hbApiService.thawPayRefund(req,byt,"433471");
    }



}
