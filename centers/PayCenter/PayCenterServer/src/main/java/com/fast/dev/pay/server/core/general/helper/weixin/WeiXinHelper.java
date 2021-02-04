package com.fast.dev.pay.server.core.general.helper.weixin;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.core.util.bytes.PemUtil;
import com.fast.dev.core.util.net.apache.ResponseModel;
import com.fast.dev.core.util.token.TokenUtil;
import com.fast.dev.pay.client.model.RefundOrderModel;
import com.fast.dev.pay.client.model.ResponseCloseOrderModel;
import com.fast.dev.pay.client.model.ResponseRefundOrderModel;
import com.fast.dev.pay.client.model.account.WeiXinAccountModel;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.client.util.OrderUtil;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelper;
import com.fast.dev.pay.server.core.util.wxv2.WXPayUtil;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.RsaCryptoUtil;
import groovy.util.Node;
import groovy.xml.XmlUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Slf4j
@Component
@Scope("prototype")
public class WeiXinHelper extends PaySupportHelper {

    private static final String KEY_ALGORITHM = "RSA";


    //证书序列号
    private String merchantSerialNumber;

    //证书秘钥
    private PrivateKey merchantPrivateKey;

    //自动证书+httpclient构建工具
    private WechatPayHttpClientBuilder builder;

    //自动证书
    private AutoUpdateCertificatesVerifier verifier;

    //微信账户信息
    private WeiXinAccountModel weiXinAccount;


    /**
     * 初始化
     */
    @Override
    public void after() {
        //读取微信账户信息
        this.weiXinAccount = super.getEnterprisePayAccount().readAccount();

        log.info("创建微信支付证书对象 : {}", weiXinAccount.getMchid());
        //证书序列号
        this.merchantSerialNumber = readCertSerialNo(weiXinAccount.getApiCertData());
        //证书秘钥
        this.merchantPrivateKey = readCertPrivateKey(weiXinAccount.getApiCertPrivateKey());
        //自动更新证书
        this.verifier = new AutoUpdateCertificatesVerifier(
                new WechatPay2Credentials(
                        this.weiXinAccount.getMchid(),
                        new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)
                ),
                this.weiXinAccount.getApiV3Key().getBytes());
        //证书构建工具
        this.builder = WechatPayHttpClientBuilder.create()
                .withMerchant(this.weiXinAccount.getMchid(), merchantSerialNumber, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier));
    }


    @Override
    @SneakyThrows
    public <T> T readObjectFromRequest(HttpServletRequest request, Class<? extends T> cls) {
        StringBuilder sb = new StringBuilder();
        if (!this.requestSignature(request, sb)) {
            log.info("error", "回调请求的签名异常");
            throw new RuntimeException("回调请求的签名异常");
        }
        return JsonUtil.toObject(sb.toString(), cls);
    }

    @Override
    public Map<String, Object> queryOrder(PayOrder payOrder) {
        String url = String.format("https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s?mchid=%s", payOrder.getOrderId(), this.weiXinAccount.getMchid());
        ResponseModel responseModel = request(url, false);
        return BeanUtil.toMap(responseModel.getBody());
    }

    @Override
    public ResponseCloseOrderModel closeOrder(PayOrder payOrder) {
        String url = String.format("https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s/close", payOrder.getOrderId());
        ResponseModel responseModel = request(url, true, new HashMap<String, Object>() {{
            put("mchid", weiXinAccount.getMchid());
        }});

        ResponseCloseOrderModel model = new ResponseCloseOrderModel();
        model.setTradeNo(payOrder.getOrderId());
        model.setSuccess(responseModel.getCode() == 204);
        if (responseModel.getBody() != null) {
            model.setOther(BeanUtil.toMap(responseModel.getBody()));
        }

        return model;
    }


    /**
     * V3 没有支持的接口
     *
     * @param payOrder
     * @param refundOrderModel
     * @return
     */
    private ResponseRefundOrderModel refundv3(PayOrder payOrder, RefundOrderModel refundOrderModel) {


        String url = "https://api.mch.weixin.qq.com/secapi/pay/refund";

        Map<String, Object> parm = new HashMap<>();
        //微信支付分配的商户号
        parm.put("mchid", this.weiXinAccount.getMchid());
        parm.put("appid", this.weiXinAccount.getAppid());

        //订单号
        parm.put("out_trade_no", refundOrderModel.getOrderId());
        //原因
        parm.put("reason", refundOrderModel.getReason());

        //退款订单号
        parm.put("out_refund_no", TokenUtil.create());

        parm.put("amount", new HashMap<>() {{
            //退款金额
            put("refund", refundOrderModel.getAmount());
            //总金额
            put("total", payOrder.getPrice());
            //货币
            put("currency", "CNY");
        }});

        //网络请求
        ResponseModel response = request(url, true, parm);

        System.out.println(JsonUtil.toJson(response.getBody()));

//        return ResultContent.builder().build();
        return null;
    }


    /**
     * 构建 v2 的客户端
     *
     * @return
     */
    @SneakyThrows
    public HttpClient buildV2HttpClient() {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        //这里自行实现我是使用数据库配置将证书上传到了服务器可以使用 FileInputStream读取本地文件
        @Cleanup InputStream inputStream = new ByteArrayInputStream(Base64Utils.decodeFromString(this.weiXinAccount.getApiCertP12()));

        //这里写密码..默认是你的MCHID
        keyStore.load(inputStream, this.weiXinAccount.getMchid().toCharArray());

        SSLContext sslcontext = SSLContexts.custom()
                //这里也是写密码的
                .loadKeyMaterial(keyStore, this.weiXinAccount.getMchid().toCharArray())
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        return HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
    }


    @Override
    @SneakyThrows
    public ResponseRefundOrderModel refund(PayOrder payOrder, RefundOrderModel refundOrderModel) {
        //使用v2的退款接口
        final String url = "https://api.mch.weixin.qq.com/secapi/pay/refund";

        Map<String, String> parm = new HashMap<>();
        //微信支付分配的商户号
        parm.put("mch_id", this.weiXinAccount.getMchid());
        parm.put("appid", this.weiXinAccount.getAppid());
        //随机字符串
        parm.put("nonce_str", TokenUtil.create());


        //订单号
        parm.put("out_trade_no", refundOrderModel.getOrderId());

        //商户退款单号
        parm.put("out_refund_no", refundOrderModel.getRefundNo());

        //总金额
        parm.put("total_fee", String.valueOf(payOrder.getPrice()));

        //退款金额
        parm.put("refund_fee", String.valueOf(refundOrderModel.getAmount()));

        //退款原因
        parm.put("refund_desc", refundOrderModel.getReason());

        String sign = WXPayUtil.generateSignature(parm, this.weiXinAccount.getMchSecret());
        parm.put("sign", sign);

        String xmlText = WXPayUtil.mapToXml(parm);


        HttpClient httpClient = buildV2HttpClient();
        HttpPost httpPost = new HttpPost(url);

        //请求网络
        StringEntity jsonStr = new StringEntity(xmlText, Charset.forName("UTF-8"));
        jsonStr.setContentEncoding("UTF-8");
        jsonStr.setContentType("application/xml");//发送json数据需要设置contentType
        httpPost.setEntity(jsonStr);

        //执行请求
        HttpResponse httpResponse = httpClient.execute(httpPost);
        String retText = StreamUtils.copyToString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
        Map<String, String> ret = WXPayUtil.xmlToMap(retText);

        //接口调用反馈
        String result_code = ret.get("result_code");
        //退款流水号
        String out_refund_no = ret.get("out_refund_no");
        //退款流程的判断
        boolean success = (result_code != null && "SUCCESS".equals(result_code) && StringUtils.hasText(out_refund_no));
        return ResponseRefundOrderModel.builder().success(success).tradeNo(out_refund_no).other(new HashMap<>(ret)).build();
    }

    @Override
    @SneakyThrows
    public String sign(String[] parm) {
        //构造签名数据
        String message = buildSignMessage(parm);
        //使用商户的秘钥进行签名
        return signByPrivateKey(message.getBytes("UTF-8"), this.merchantPrivateKey);
    }

    /**
     * Map转换为Xml
     *
     * @param map
     * @return
     */
    private static String mapToXml(Map<String, String> map) {
        Node parent = new Node(null, "xml", Map.of());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            new Node(parent, entry.getKey(), Map.of(), entry.getValue());
        }
        String text = XmlUtil.serialize(parent);
        //删除 <?xml version="1.0" encoding="UTF-8"?>
        int at = text.indexOf("?>");
        return text.substring(at + 2, text.length());
    }


    /**
     * 请求网络
     *
     * @param url
     * @param isPost
     * @return
     */
    public ResponseModel request(String url, boolean isPost) {
        return request(url, isPost, null);
    }

    /**
     * 请求网络
     *
     * @param parm
     */
    @SneakyThrows
    public ResponseModel request(String url, boolean isPost, Object parm) {

        HttpClient httpClient = this.httpClient();
        HttpRequestBase httpRequestBase = isPost ? new HttpPost(url) : new HttpGet(url);

        //设置数据格式
        httpRequestBase.setHeader("Accept", "application/json");

        if (isPost) {
            HttpPost httpPost = (HttpPost) httpRequestBase;

            //转换为json对象，UTF-8
            if (parm != null) {
                StringEntity jsonStr = new StringEntity(JsonUtil.toJson(parm), Charset.forName("UTF-8"));
                jsonStr.setContentEncoding("UTF-8");
                jsonStr.setContentType("application/json");//发送json数据需要设置contentType
                httpPost.setEntity(jsonStr);
            }
        }


        //执行请求
        HttpResponse httpResponse = httpClient.execute(httpRequestBase);
        String info = null;
        if (httpResponse.getEntity() != null) {
            info = StreamUtils.copyToString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
        }


        //响应结果集
        ResponseModel responseModel = new ResponseModel();
        //编码
        responseModel.setCode(httpResponse.getStatusLine().getStatusCode());
        //结果集
        if (info != null) {
            responseModel.setBody(JsonUtil.toObject(info, Object.class));
        }
        //响应头
        Map<String, Set<Object>> headers = new HashMap<>();
        responseModel.setHeaders(headers);
        for (Header header : httpResponse.getAllHeaders()) {
            Set<Object> val = headers.get(header.getName());
            if (val == null) {
                val = new HashSet<>();
                headers.put(header.getName(), val);
            }
            val.add(header.getValue());
        }


        return responseModel;
    }


    /**
     * 创建一个httpclient
     *
     * @return
     */
    public HttpClient httpClient() {
        return this.builder.build();
    }


    /**
     * 响应数据签名验证
     */
    public boolean requestSignature(HttpServletRequest request, StringBuilder sb) {
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String serialNo = request.getHeader("Wechatpay-Serial");
        String signature = request.getHeader("Wechatpay-Signature");
        String body = readRequestData(request);
        log.info("timestamp:{} nonce:{} serialNo:{} signature:{}", timestamp, nonce, serialNo, signature);

        //获取证书序列号
        String serialNumber = this.verifier.getValidCertificate().getSerialNumber().toString(16).toUpperCase();

        // 验证证书序列号
        if (!serialNumber.equals(serialNo)) {
            log.error("证书序列号错误: {}", signature);
            return false;
        }

        //签名验证
        String message = buildSignMessage(timestamp, nonce, body);
        boolean ret = checkByPublicKey(message, signature, this.verifier.getValidCertificate().getPublicKey());
        if (!ret) {
            log.error("签名验证失败: {}", body);
            return false;
        }
        //清空
        if (sb.length() > 0) {
            sb.delete(0, sb.length());
        }
        sb.append(body);
        return true;
    }


    /**
     * 解密数据
     *
     * @return
     */
    @SneakyThrows
    public String decryptToText(String associatedData, String nonce, String cipherText) {
        AesUtil aesUtil = new AesUtil(this.weiXinAccount.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        return aesUtil.decryptToString(
                associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                cipherText
        );
    }


    /**
     * 构建签名的消息
     *
     * @param texts
     * @return
     */
    public static String buildSignMessage(String... texts) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(texts).forEach((String text) -> {
            sb.append(text + "\n");
        });
        return sb.toString();
    }


    /**
     * 解密非对称加密填充
     *
     * @param ciphertext
     * @return
     */
    @SneakyThrows
    public String decryptOAEP(String ciphertext) {
        return RsaCryptoUtil.decryptOAEP(ciphertext, merchantPrivateKey);
    }


    /**
     * 加密非对称加密填充
     *
     * @return
     */
    @SneakyThrows
    public String encryptOAEP(String text) {
        return RsaCryptoUtil.encryptOAEP(text, verifier.getValidCertificate());
    }


    /**
     * 使用公钥签名
     *
     * @param data
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    @SneakyThrows
    public static boolean checkByPublicKey(String data, String sign, PublicKey publicKey) {
        java.security.Signature signature = java.security.Signature.getInstance("SHA256WithRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64Utils.decode(sign.getBytes(StandardCharsets.UTF_8)));
    }


    /**
     * 使用私钥签名
     *
     * @return
     */
    @SneakyThrows
    public static String signByPrivateKey(byte[] message, PrivateKey privateKey) {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(message);
        return Base64Utils.encodeToString(sign.sign());
    }

    /**
     * 读取证书序列号
     *
     * @return
     */
    @SneakyThrows
    public static String readCertSerialNo(String certData) {
        @Cleanup ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Utils.decodeFromString(certData));
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(inputStream);
        cert.checkValidity();
        return cert.getSerialNumber().toString(16).toUpperCase();
    }


    /**
     * 获取证书秘钥
     *
     * @return
     * @throws Exception
     */
    @SneakyThrows
    public static PrivateKey readCertPrivateKey(String certData) {
        //转换证书base64编码为私钥文本（无注释头尾)
        @Cleanup ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64Utils.decodeFromString(certData));
        String privateKeyText = PemUtil.read(byteArrayInputStream);
        //解码base64秘钥
        byte[] buffer = Base64Utils.decodeFromString(privateKeyText);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }


    /**
     * 读取请求的数据
     *
     * @param request
     * @return
     */
    @SneakyThrows
    private static String readRequestData(HttpServletRequest request) {
        @Cleanup InputStream inputStream = request.getInputStream();
        return StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));
    }


}
