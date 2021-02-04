package com.fast.dev.pay.server.core.hb.model.req;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.util.RandomUtil;
import lombok.Data;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class BaseRequest {

    /**
     * appId
     */
    private String appid = "5A75494B4D6B754D6C6432357A7A437A625636647471576754776B3D";

    /**
     * 版本
     */
    private String version = "1.0";

    /**
     * 服务商编号
     */
    private String mchtId = "HX1200000000001";

    /**
     * 商户编号
     */
    private String mchtNo = "HX1200000000001";

    /**
     * 商品订单号
     */
    private String tradeNo;

    /**
     * 请求时间
     */
    private String pcsDate;

    /**
     * mac地址
     */
    private String mac = "00-15-5D-F9-0E-82";

    /**
     * 网络地址（终端IP）
     */
    private String occurAdd = "172.30.160.1";





}
