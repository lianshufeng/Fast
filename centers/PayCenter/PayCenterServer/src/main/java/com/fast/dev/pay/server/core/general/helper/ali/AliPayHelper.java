package com.fast.dev.pay.server.core.general.helper.ali;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Client;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.Context;
import com.alipay.easysdk.kernel.util.AntCertificationUtil;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.kernel.util.Signer;
import com.alipay.easysdk.payment.app.models.AlipayTradeAppPayResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeCloseResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.aliyun.tea.*;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.core.util.encode.HashUtil;
import com.fast.dev.pay.client.model.RefundOrderModel;
import com.fast.dev.pay.client.model.ResponseCloseOrderModel;
import com.fast.dev.pay.client.model.ResponseRefundOrderModel;
import com.fast.dev.pay.client.model.account.AliPayAccountModel;
import com.fast.dev.pay.server.core.general.conf.PayConf;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Scope("prototype")
public class AliPayHelper extends PaySupportHelper {

    @Autowired
    private UserLogHelper userLog;

    //支付宝的支付账号信息
    private AliPayAccountModel accountModel;

    //支付宝配置
    private Config options;


    @Autowired
    private PayConf payConf;

    //支付宝的公钥
    private String alipayPublicKey;


    @Override
    @SneakyThrows
    public void after() {
        this.accountModel = super.getEnterprisePayAccount().readAccount();
        log.info("创建ali支付证书 : {}", this.accountModel.getAppId());


        //构建参数
        this.options = buildConfig();

        //支付宝证书的公钥
        this.alipayPublicKey = AntCertificationUtil.getCertPublicKey(AntCertificationUtil.readCertContent(options.alipayCertPath));

    }


    /**
     * 构建配置
     *
     * @return
     */
    private Config buildConfig() {
        Config config = new Config();

        config.signType = "RSA2";
        config.protocol = "https";
        // 网关(注意区分，沙箱环境与正式环境)
        config.gatewayHost = this.payConf.getAli().getGatewayHost();

        // 回调通知地址
        config.notifyUrl = this.payConf.getAli().getNotifyUrl();

        //应用id
        config.appId = this.accountModel.getAppId();

        // 商户秘钥
        config.merchantPrivateKey = new String(Base64Utils.decodeFromString(this.accountModel.getMerchantPrivateKey()));


        //设置证书配置
        setCertConfig(this.accountModel, CertType.MerchantCert, config);
        setCertConfig(this.accountModel, CertType.AlipayCert, config);
        setCertConfig(this.accountModel, CertType.AlipayRootCert, config);

        return config;
    }


    /**
     * 面对面支付（生产二维码)
     *
     * @param subject
     * @param outTradeNo
     * @param totalAmount
     */
    @Synchronized
    @SneakyThrows
    public AlipayTradePrecreateResponse faceToFace(String orderId, String subject, String outTradeNo, long totalAmount) {

        //实例化SDK
        com.alipay.easysdk.payment.facetoface.Client client = new com.alipay.easysdk.payment.facetoface.Client(new Client(buildContext()));

        //回调地址
        client = client.asyncNotify(String.format(options.notifyUrl, orderId));
        AlipayTradePrecreateResponse response = client.preCreate(subject, outTradeNo, formatAmount(totalAmount));

        failAndThrowException(orderId, response);

        return response;
    }


    @Synchronized
    @SneakyThrows
    public AlipayTradeAppPayResponse appPay(String orderId, String subject, String outTradeNo, long totalAmount) {

        //实例化SDK
        com.alipay.easysdk.payment.app.Client client = new com.alipay.easysdk.payment.app.Client(new Client(buildContext()));

        //回调地址
        client = client.asyncNotify(String.format(options.notifyUrl, orderId));
        AlipayTradeAppPayResponse response = client.pay(subject, outTradeNo, formatAmount(totalAmount));

        failAndThrowException(orderId, response);
        return response;
    }


    /**
     * 读取对象,如果有验签也一并实现
     *
     * @param request
     * @param cls
     * @param <T>
     * @return
     */
    @Override
    @SneakyThrows
    public <T> T readObjectFromRequest(HttpServletRequest request, Class<? extends T> cls) {
        //取出参数map
        Map<String, String> m = new HashMap<>();
        //将参数转换为普通的map
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                m.put(entry.getKey(), values[0]);
            }
        }

        //进行数据签名校验
        if (!Signer.verifyParams(new HashMap<>(m), this.alipayPublicKey)) {
            log.info("error", "回调请求的签名异常");
            throw new RuntimeException("回调请求的签名异常");
        }

        return JsonUtil.toObject(JsonUtil.toJson(m), cls);
    }

    @Override
    @SneakyThrows
    public Map<String, Object> queryOrder(PayOrder payOrder) {
        final String orderId = payOrder.getOrderId();
        //实例化SDK
        com.alipay.easysdk.payment.common.Client client = new com.alipay.easysdk.payment.common.Client(new Client(buildContext()));
        AlipayTradeQueryResponse response = client.query(orderId);

        boolean success = isSuccess(response);
        log.info("支付宝查询订单 ： {} -> {} ", payOrder.getId(), success);

        //状态判断
        Map<String, Object> map = JsonUtil.toObject(response.getHttpBody(), Map.class);
        return (Map) map.get("alipay_trade_query_response");
    }

    @Override
    @SneakyThrows
    public ResponseCloseOrderModel closeOrder(PayOrder payOrder) {
        final String orderId = payOrder.getOrderId();
        com.alipay.easysdk.payment.common.Client client = new com.alipay.easysdk.payment.common.Client(new Client(buildContext()));
        AlipayTradeCloseResponse response = client.close(orderId);

        Map ret = JsonUtil.toObject(response.getHttpBody(), Map.class);
        ret = (Map) ret.get("alipay_trade_close_response");

        String code = String.valueOf(ret.get("code"));
        ResponseCloseOrderModel orderModel = new ResponseCloseOrderModel();
        //考虑两种情况，第一种用户已经扫描，第二种用户未扫描（交易不存在)
        orderModel.setSuccess(code.equals("10000") || code.equals("40004"));
        orderModel.setTradeNo(payOrder.getOrderId());
        orderModel.setOther(ret);

        return orderModel;
    }


    @Override
    @SneakyThrows
    public ResponseRefundOrderModel refund(PayOrder payOrder, RefundOrderModel refundOrderModel) {
        //实例化SDK
        Client client = new Client(buildContext());
        AlipayTradeRefundResponse response = refund(client, refundOrderModel.getOrderId(), refundOrderModel.getRefundNo(), formatAmount(refundOrderModel.getAmount()));

//        AlipayTradeRefundResponse response = client.refund(refundOrderModel.getOrderId(), formatAmount(refundOrderModel.getAmount()));
        log.info("支付宝订单退款 ： {} -> {} -> {}", payOrder.getId(), isSuccess(response), response.getHttpBody());

        //状态判断
        Map<String, Object> map = JsonUtil.toObject(response.getHttpBody(), Map.class);
        map = (Map) map.get("alipay_trade_refund_response");
        Object subCode = map.get("code");
        //流水号
        Object tradeNo = refundOrderModel.getRefundNo();
        String tradeNoText = (tradeNo == null ? null : String.valueOf(tradeNo));
        boolean success = (subCode != null && "10000".equals(subCode));
        return ResponseRefundOrderModel.builder().success(success).tradeNo(tradeNoText).other(map).build();
    }

    @Override
    public String sign(String[] parm) {
        throw new RuntimeException("支付宝暂时不支持签名接口");
    }


    /**
     * 退款
     *
     * @param outTradeNo   订单号
     * @param outRequestNo 退款订单号
     * @param refundAmount 退款金额
     * @return
     * @throws Exception
     */
    @SneakyThrows
    private AlipayTradeRefundResponse refund(final Client _kernel, String outTradeNo, String outRequestNo, String refundAmount) {
        java.util.Map<String, Object> runtime_ = TeaConverter.buildMap(
                new TeaPair("connectTimeout", 15000),
                new TeaPair("readTimeout", 15000),
                new TeaPair("retry", TeaConverter.buildMap(
                        new TeaPair("maxAttempts", 0)
                ))
        );

        TeaRequest _lastRequest = null;
        long _now = System.currentTimeMillis();
        int _retryTimes = 0;
        while (Tea.allowRetry((java.util.Map<String, Object>) runtime_.get("retry"), _retryTimes, _now)) {
            if (_retryTimes > 0) {
                int backoffTime = Tea.getBackoffTime(runtime_.get("backoff"), _retryTimes);
                if (backoffTime > 0) {
                    Tea.sleep(backoffTime);
                }
            }
            _retryTimes = _retryTimes + 1;
            try {
                TeaRequest request_ = new TeaRequest();
                java.util.Map<String, String> systemParams = TeaConverter.buildMap(
                        new TeaPair("method", "alipay.trade.refund"),
                        new TeaPair("app_id", _kernel.getConfig("appId")),
                        new TeaPair("timestamp", _kernel.getTimestamp()),
                        new TeaPair("format", "json"),
                        new TeaPair("version", "1.0"),
                        new TeaPair("alipay_sdk", _kernel.getSdkVersion()),
                        new TeaPair("charset", "UTF-8"),
                        new TeaPair("sign_type", _kernel.getConfig("signType")),
                        new TeaPair("app_cert_sn", _kernel.getMerchantCertSN()),
                        new TeaPair("alipay_root_cert_sn", _kernel.getAlipayRootCertSN())
                );
                java.util.Map<String, Object> bizParams = TeaConverter.buildMap(
                        new TeaPair("out_trade_no", outTradeNo),
                        new TeaPair("out_request_no", outRequestNo),
                        new TeaPair("refund_amount", refundAmount)
                );
                java.util.Map<String, String> textParams = new java.util.HashMap<>();
                request_.protocol = _kernel.getConfig("protocol");
                request_.method = "POST";
                request_.pathname = "/gateway.do";
                request_.headers = TeaConverter.buildMap(
                        new TeaPair("host", _kernel.getConfig("gatewayHost")),
                        new TeaPair("content-type", "application/x-www-form-urlencoded;charset=utf-8")
                );
                request_.query = _kernel.sortMap(TeaConverter.merge(String.class,
                        TeaConverter.buildMap(
                                new TeaPair("sign", _kernel.sign(systemParams, bizParams, textParams, _kernel.getConfig("merchantPrivateKey")))
                        ),
                        systemParams,
                        textParams
                ));
                request_.body = Tea.toReadable(_kernel.toUrlEncodedRequestBody(bizParams));
                _lastRequest = request_;
                TeaResponse response_ = Tea.doAction(request_, runtime_);

                java.util.Map<String, Object> respMap = _kernel.readAsJson(response_, "alipay.trade.refund");
                if (_kernel.isCertMode()) {
                    if (_kernel.verify(respMap, _kernel.extractAlipayPublicKey(_kernel.getAlipayCertSN(respMap)))) {
                        return TeaModel.toModel(_kernel.toRespModel(respMap), new AlipayTradeRefundResponse());
                    }

                } else {
                    if (_kernel.verify(respMap, _kernel.getConfig("alipayPublicKey"))) {
                        return TeaModel.toModel(_kernel.toRespModel(respMap), new AlipayTradeRefundResponse());
                    }

                }

                throw new TeaException(TeaConverter.buildMap(
                        new TeaPair("message", "验签失败，请检查支付宝公钥设置是否正确。")
                ));
            } catch (Exception e) {
                if (Tea.isRetryable(e)) {
                    continue;
                }
                throw e;
            }
        }

        throw new TeaUnretryableException(_lastRequest);
    }


    /**
     * 是否成功
     *
     * @param response
     * @return
     */
    private static boolean isSuccess(TeaModel response) {
        return ResponseChecker.success(response);
    }


    /**
     * 失败抛出异常
     *
     * @param orderId
     * @param response
     */
    private static void failAndThrowException(String orderId, TeaModel response) {
        if (!isSuccess(response)) {
            throw new RuntimeException("访问三方支付平台失败 : " + orderId + " e: " + response);
        }
    }


    @SneakyThrows
    private synchronized Context buildContext() {
        return new Context(this.options, Factory.SDK_VERSION);
    }


    /**
     * 设置证书配置
     *
     * @param accountModel
     * @param certType
     * @param config
     * @return
     */
    @SneakyThrows
    private static void setCertConfig(AliPayAccountModel accountModel, CertType certType, final Config config) {
        //创建临时目录
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + accountModel.getAppId());
        if (!file.exists()) {
            file.mkdirs();
        }


        //商户证书的hash+certFileName
        String fileName = HashUtil.hash(accountModel.getMerchantCert()) + "_" + certType.getName() + ".crt";

        //处理存在的文件
        File certFile = new File(file.getAbsolutePath() + "/" + fileName);

        //读取配置中的证书信息
        Map<String, Object> accountMap = BeanUtil.toMap(accountModel);
        String certBuffer = String.valueOf(accountMap.get(certType.getName()));

        //将数据写到磁盘上
        writeFile(certFile, Base64Utils.decodeFromString(certBuffer));

        //设置配置对象,利用反射设置
        config.getClass().getField(certType.getPathName()).set(config, certFile.getAbsolutePath());

    }


    @SneakyThrows
    private synchronized static void writeFile(File file, byte[] bin) {
        //目标文件与内存文件一致则不处理
        if (file.exists() && DigestUtils.md5(FileUtils.readFileToByteArray(file)).equals(DigestUtils.md5(bin))) {
            return;
        }
        FileUtils.writeByteArrayToFile(file, bin);
    }


    /**
     * 证书类型
     */
    private static enum CertType {
        MerchantCert("merchantCert", "merchantCertPath"),
        AlipayCert("alipayCert", "alipayCertPath"),
        AlipayRootCert("alipayRootCert", "alipayRootCertPath"),

        ;

        CertType(String name, String pathName) {
            this.name = name;
            this.pathName = pathName;
        }

        @Getter
        private String name;

        @Getter
        private String pathName;

    }


    /**
     * 格式化金额
     *
     * @return
     */
    private static String formatAmount(long price) {
        StringBuffer sb = new StringBuffer(String.valueOf(price));
        while (sb.length() <= 2) {
            sb.insert(0, "0");
        }
        sb.insert(sb.length() - 2, ".");
        return sb.toString();

    }

}
