package com.fast.dev.pay.server.core.cpcn.service

import com.fast.dev.core.util.JsonUtil
import com.fast.dev.core.util.net.apache.HttpClientUtil
import com.fast.dev.core.util.net.apache.HttpModel
import com.fast.dev.core.util.net.apache.MethodType
import com.fast.dev.core.util.net.apache.ResponseModel
import com.fast.dev.core.util.script.GroovyUtil
import com.fast.dev.pay.server.core.cpcn.conf.CPCNConf
import com.fast.dev.pay.server.core.cpcn.param.Key
import com.fast.dev.pay.server.core.cpcn.param.request.CancelBindCardRequest
import com.fast.dev.pay.server.core.cpcn.param.request.ConfirmBindCardRequest
import com.fast.dev.pay.server.core.cpcn.param.request.DirectBindCardRequest
import com.fast.dev.pay.server.core.cpcn.param.request.PaymentRequest
import com.fast.dev.pay.server.core.cpcn.param.request.PreBindCardRequest
import com.fast.dev.pay.server.core.cpcn.param.request.QueryBindCardRequest
import com.fast.dev.pay.server.core.cpcn.param.request.QueryPaymentResultRequest
import com.fast.dev.pay.server.core.cpcn.param.request.QueryRedirectPaymentRequest
import com.fast.dev.pay.server.core.cpcn.param.request.RedirectCloseOrderRequest
import com.fast.dev.pay.server.core.cpcn.param.request.RedirectPaymentRequest
import com.fast.dev.pay.server.core.cpcn.param.request.RedirectRefundRequest
import com.fast.dev.pay.server.core.cpcn.param.request.RefundRequest
import com.fast.dev.pay.server.core.cpcn.param.response.CancelBindCardResponse
import com.fast.dev.pay.server.core.cpcn.param.response.ConfirmBindCardResponse
import com.fast.dev.pay.server.core.cpcn.param.response.DirectBindCardResponse
import com.fast.dev.pay.server.core.cpcn.param.response.PaymentResponse
import com.fast.dev.pay.server.core.cpcn.param.response.PreBindCardResponse
import com.fast.dev.pay.server.core.cpcn.param.response.QueryBindCardResponse
import com.fast.dev.pay.server.core.cpcn.param.response.QueryPaymentResultResponse
import com.fast.dev.pay.server.core.cpcn.param.response.QueryRedirectPaymentResponse
import com.fast.dev.pay.server.core.cpcn.param.response.RedirectCloseOrderResponse
import com.fast.dev.pay.server.core.cpcn.param.response.RedirectPaymentResponse
import com.fast.dev.pay.server.core.cpcn.param.response.RedirectRefundResponse
import com.fast.dev.pay.server.core.cpcn.param.response.RefundResponse
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil
import com.fast.dev.pay.server.core.general.conf.BankDictionaryModel
import com.fast.dev.pay.server.core.general.conf.PayConf
import groovy.util.logging.Slf4j
import org.bouncycastle.util.encoders.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

@Service
@Slf4j
class CPCNApiService {

    @Autowired
    private CPCNConf conf

    @Autowired
    private PayConf payConf

    /**
     * 查询绑卡
     */
    def queryBindCard(QueryBindCardRequest request,Key key) {

        log.debug("queryBindCard 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        QueryBindCardResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new QueryBindCardResponse(respMessage,respSignature,key)
            log.debug("queryBindCard 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new QueryBindCardResponse()
            response.message("请求接口失败")
        }
        return response
    }

    /**
     *  绑卡
     */
    def preBindCard(PreBindCardRequest request,Key key) {
        log.debug("bindCard 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        PreBindCardResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new PreBindCardResponse(respMessage,respSignature,key)
            log.debug("bindCard 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new PreBindCardResponse()
            response.message("请求接口失败")
        }
        return response
    }

    /**
     * 确认绑卡
     */
    def confirmBindCard(ConfirmBindCardRequest request,Key key) {

        log.debug("confirmBindCard 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        ConfirmBindCardResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new ConfirmBindCardResponse(respMessage,respSignature,key)
            log.debug("confirmBindCard 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new ConfirmBindCardResponse()
            response.message("请求接口失败")
        }
        return response

    }

    /**
     * 直接绑卡（无短信验证）
     */
    def directBindCard(DirectBindCardRequest request,Key key){

        log.debug("directBindCard 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        DirectBindCardResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new DirectBindCardResponse(respMessage,respSignature,key)
            log.debug("directBindCard 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new DirectBindCardResponse()
            response.message("请求接口失败")
        }
        return response

    }

    /**
     * 取消绑卡
     */
    def cancelBindCard(CancelBindCardRequest request,Key key) {
        log.debug("cancelBindCard 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        CancelBindCardResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new CancelBindCardResponse(respMessage,respSignature,key)
            log.debug("cancelBindCard 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new CancelBindCardResponse()
            response.message("请求接口失败")
        }
        return response
    }

    /**
     * 快捷支付
     */
    def payment(PaymentRequest request,Key key) {
        log.debug("payment 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        PaymentResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new PaymentResponse(respMessage,respSignature,key)
            log.debug("payment 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new PaymentResponse()
            response.message("请求接口失败")
        }
        return response
    }

    /**
     * 查询支付结果
     */
    def queryPaymentResult(QueryPaymentResultRequest request,Key key) {
        log.debug("queryPaymentResult 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        QueryPaymentResultResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String[] ret = body.split(",");
            String respMessage = ret[0].replaceAll("\r|\n", "")
            String respSignature = ret[1].replaceAll("\r|\n", "")
            response = new QueryPaymentResultResponse(respMessage,respSignature,key)
            log.debug("queryPaymentResult 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new QueryPaymentResultResponse()
            response.message("请求接口失败")
        }
        return response
    }

    /**
     * 退款
     */
    def refund(RefundRequest request,Key key) {
        log.debug("refund 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        RefundResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new RefundResponse(respMessage,respSignature,key)
            log.debug("refund 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new RefundResponse()
            response.message("请求接口失败")
        }
        return response
    }

    /**
     * 跳转支付
     */
    def redirectPayment(RedirectPaymentRequest request, Key key) {
        log.debug("redirectPayment 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        RedirectPaymentResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new RedirectPaymentResponse(respMessage,respSignature,key)
            log.debug("redirectPayment 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new RedirectPaymentResponse()
            response.message("请求接口失败")
        }
        return response
    }

    /**
     * 跳转支付查询
     */
    def queryRedirectPayment(QueryRedirectPaymentRequest request, Key key) {
        log.debug("queryRedirectPayment 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        QueryRedirectPaymentResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new QueryRedirectPaymentResponse(respMessage,respSignature,key)
            log.debug("queryRedirectPayment 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new QueryRedirectPaymentResponse()
            response.message("请求接口失败")
        }
        return response
    }

    /**
     * 跳转支付退款
     */
    def redirectRefund(RedirectRefundRequest request, Key key) {
        log.debug("redirectRefund 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        RedirectRefundResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new RedirectRefundResponse(respMessage,respSignature,key)
            log.debug("redirectRefund 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new RedirectRefundResponse()
            response.message("请求接口失败")
        }
        return response
    }

    /**
     * 跳转支付退款
     */
    def redirectCloseOrder(RedirectCloseOrderRequest request, Key key) {
        log.debug("redirectCloseOrder 请求参数 ：" + JsonUtil.toJson(request))
        //调用接口
        ResponseModel responseModel = doPost(conf.host, structureParam(request.getRequestMessage(),request.getRequestSignature()))
        String body = String.valueOf(responseModel['body'])
        RedirectCloseOrderResponse response = null
        if(body != null && body.indexOf(",") > 0){
            String respMessage = body.split(",")[0].replaceAll("\r|\n", "")
            String respSignature = body.split(",")[1].replaceAll("\r|\n", "")
            response = new RedirectCloseOrderResponse(respMessage,respSignature,key)
            log.debug("redirectCloseOrder 返回 ：" + response.responsePlainText)
        }
        if (response == null){
            response = new RedirectCloseOrderResponse()
            response.message("请求接口失败")
        }
        return response
    }


    def doPost(String url, Object param) {
        HttpModel httpModel = new HttpModel()
        httpModel.setHeader(["Content-Type": "application/x-www-form-urlencoded"])
        httpModel.setMethod(MethodType.Post)
        httpModel.setUrl(url)
        httpModel.setBody(param)
        ResponseModel responseModel = HttpClientUtil.request(httpModel)
        return responseModel
    }

    def getBankCode(String accountNumber){
        HttpModel httpModel = new HttpModel()
        httpModel.setHeader(["Content-Type": "application/x-www-form-urlencoded"])
        httpModel.setMethod(MethodType.Get)
        httpModel.setUrl("https://api.dzurl.top/api/bankcard?cardNo=" + accountNumber)
        ResponseModel result = HttpClientUtil.request(httpModel)
        if (result['body']['state'] != 'Success'){
            throw new RuntimeException("获取银行编码失败")
        }
        String code = result['body']['content']['bank']['code']
        BankDictionaryModel bank = payConf.getBank()
        if (!bank.getInfo().containsKey(code)){
            throw new RuntimeException("卡号："+ accountNumber +"，不支持该银行，请联系管理员")
        }
        return bank.getInfo().get(code).getCode()
    }

    def structureParam(String message,String signature){
        return GroovyUtil.textTemplate(["message": URLEncoder.encode(message,"UTF-8" ),
                                        "signature": URLEncoder.encode(signature,"UTF-8" )],
                'message=${message}&signature=${signature}')
    }

    String getCPCNResponse(){
        //构造返回中金的参数
        /*DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element Response = document.createElement("Response");
        Element Head = document.createElement("Head");
        Element Body = document.createElement("Body");
        Element Code = document.createElement("Code");
        Element Message = document.createElement("Message");
        document.appendChild(Response);
        Response.appendChild(Head);
        Response.appendChild(Body);
        Head.appendChild(Code);
        Code.setTextContent("2000");
        Head.appendChild(Message);
        Message.setTextContent("OK.");
        String responsePlainText = XmlUtil.createPrettyFormat(document).trim();
        byte[] data = responsePlainText.getBytes("UTF-8");
        return new String(Base64.encode(data))*/
        return "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48UmVzcG9" +
                "uc2UgdmVyc2lvbj0iMi4wIj48SGVhZD48Q29kZT4yMDAwPC9Db2RlPjxNZ" +
                "XNzYWdlPk9LLjwvTWVzc2FnZT48L0hlYWQ+PC9SZXNwb25zZT4="
    }

}
