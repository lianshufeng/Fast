package com.fast.dev.pay.server.core.general.controller.callback.cpcn;

import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.bytes.BytesUtil;
import com.fast.dev.pay.server.core.cpcn.conf.CPCNConf;
import com.fast.dev.pay.server.core.cpcn.service.CPCNApiService;
import com.fast.dev.pay.server.core.cpcn.util.PFXUtil;
import com.fast.dev.pay.server.core.cpcn.util.SignUtil;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import com.fast.dev.pay.server.core.general.controller.callback.SuperCallBackController;
import com.fast.dev.pay.server.core.general.dao.PayOrderDao;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelper;
import com.fast.dev.pay.server.core.general.helper.cpcn.CpcnOrderHelper;
import com.fast.dev.pay.server.core.general.service.callback.cncp.CNCPOrderCallBackService;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("callback/cpcn")
public class CPCNOrderPayCallBackController extends SuperCallBackController {

    @Autowired
    private CNCPOrderCallBackService cncpOrderCallBackService;

    @Autowired
    private CPCNApiService cpcnApiService;

    @Autowired
    private CPCNConf cpcnConf;

    @Autowired
    private PayOrderDao payOrderDao;

    @Autowired
    private UserLogHelper userLogHelper;


    /**
     * 微信的回调
     *
     * @return
     */
    //注意：此接防止被全局异常捕获返回 200
    @SneakyThrows
    @RequestMapping("order")
    @UserLog(action = "CPCNOrderPayCallBack")
    public void order(HttpServletRequest request, HttpServletResponse response) {
        log.info("callback : {} ", "CPCNOrderPay");
        log.info("param : {}", JsonUtil.toJson(request.getParameterMap(), true));
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(200);
        String message = request.getParameter("message");
        String signature = request.getParameter("signature");
        byte[] data = Base64.decode(message);
        String responsePlainText = new String(data, "UTF-8");
        byte[] signatureByte = BytesUtil.hexToBin(signature);
        byte[] pubBin = java.util.Base64.getDecoder().decode(cpcnConf.getPubKey());
        PublicKey publicKey = PFXUtil.getPublicKey(new ByteArrayInputStream(pubBin));
        boolean verify = SignUtil.verify(publicKey, data, signatureByte);
        if (!verify) {
            throw new Exception("验签失败");
        }
        Document document = XmlUtil.createDocument(responsePlainText);
        String paymentNo = XmlUtil.getNodeText(document, "PaymentNo");
        PayOrder payOrder = payOrderDao.findByOrderId(paymentNo);
        Map<String, Object> item = new HashMap<>();
        item.put("status", XmlUtil.getNodeText(document, "Status"));
        if (payOrder == null || payOrder.getOrderId() == null) {
            userLogHelper.log("payOrder", "订单不存在：" + paymentNo);
        } else {
            item.put("orderId", payOrder.getOrderId());
            cncpOrderCallBackService.callback(payOrder, item);
        }
        @Cleanup PrintWriter printWriter = response.getWriter();
        printWriter.write(cpcnApiService.getCPCNResponse());

    }


    @Override
    public Class<? extends PaySupportHelper> getPaySupportHelper() {
        return CpcnOrderHelper.class;
    }
}
