package com.fast.dev.pay.server.core.hb.service

import com.fast.dev.core.util.JsonUtil
import com.fast.dev.core.util.net.apache.HttpClientUtil
import com.fast.dev.core.util.net.apache.HttpModel
import com.fast.dev.core.util.net.apache.MethodType
import com.fast.dev.core.util.net.apache.ResponseModel
import com.fast.dev.pay.server.core.hb.conf.HuaxiaConf
import com.fast.dev.pay.server.core.hb.model.req.ConfrimOpenAcctReq
import com.fast.dev.pay.server.core.hb.model.req.FreezeAcctReq
import com.fast.dev.pay.server.core.hb.model.req.OpenAcctReq
import com.fast.dev.pay.server.core.hb.model.req.QueryOpenAcctReq
import com.fast.dev.pay.server.core.hb.model.req.QueryTransResultReq
import com.fast.dev.pay.server.core.hb.model.req.ThawPayRefundReq
import com.fast.dev.pay.server.core.hb.model.resp.ConfrimOpenAcctResp
import com.fast.dev.pay.server.core.hb.model.resp.FreezeAcctResp
import com.fast.dev.pay.server.core.hb.model.resp.OpenAcctResp
import com.fast.dev.pay.server.core.hb.model.resp.QueryOpenAcctResp
import com.fast.dev.pay.server.core.hb.model.resp.QueryTransResultResp
import com.fast.dev.pay.server.core.hb.model.resp.ThawPayRefundResp
import com.fast.dev.pay.server.core.hb.sign.EnvApplication
import com.fast.dev.pay.server.core.hb.type.ApiType
import com.fast.dev.pay.server.core.hb.type.Status
import groovy.util.logging.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Log
class HBApiService {

    @Autowired
    private HuaxiaConf conf

    private EnvApplication envApplication = new EnvApplication()


    /**
     * 开户申请
     * @param req
     * @param pk
     * @param password
     * @return
     */
    OpenAcctResp openAcct(OpenAcctReq req, byte[] pk, String password){



        OpenAcctResp resp = new OpenAcctResp()
        String json = JsonUtil.toJson(req)

        log.info("openAcct请求参数 ：" + json)
        long signTime = System.currentTimeMillis()
        //构造参数
        Map<String,Object> param = getParam(json,pk,password,req.getAppid())

        log.info("加密时间：" + (System.currentTimeMillis() - signTime))

        long now = System.currentTimeMillis()

        //调用接口
        ResponseModel responseModel = doPost(conf.host + ApiType.OpenAcct.url, param)

        log.info("调用接口时间：" + (System.currentTimeMillis() - now))
        if (responseModel != null && responseModel["body"]["body"] != null){
            String result = envApplication.openEnvelope(pk,password,responseModel["body"]["body"])
            log.info("openAcct请求结果 ：" + result)
            resp = JsonUtil.toObject(result,OpenAcctResp.class)
            resp.setOrgTradeNo(req.getTradeNo())
        } else {
            resp.setRetCode(String.valueOf(responseModel["body"]["status"]))
            resp.setRetMsg(responseModel["body"]["data"])
        }

        return resp
    }

    /**
     * 确认开户
     * @param req
     * @param pk
     * @param password
     * @return
     */
    ConfrimOpenAcctResp confrimOpenAcct(ConfrimOpenAcctReq req, byte[] pk, String password){

        ConfrimOpenAcctResp resp = new ConfrimOpenAcctResp()

        String json = JsonUtil.toJson(req)

        log.info("confrimOpenAcct请求参数 ：" + json)

        //构造参数
        Map<String,Object> param = getParam(json,pk,password,req.getAppid())

        //调用接口
        ResponseModel responseModel = doPost(conf.host + ApiType.ConfrimOpenAcct.url, param)

        if (responseModel != null && responseModel["body"]["body"] != null){
            String result = envApplication.openEnvelope(pk,password,responseModel["body"]["body"])
            log.info("confrimOpenAcct请求结果 ：" + result)
            resp = JsonUtil.toObject(result,ConfrimOpenAcctResp.class)
            resp.setOrgTradeNo(req.getTradeNo())
        } else {
            resp.setRetCode(String.valueOf(responseModel["body"]["status"]))
            resp.setRetMsg(responseModel["body"]["data"])
        }

        return resp

    }

    /**
     * 开户信息查询
     * @param req
     * @param pk
     * @param password
     * @return
     */
    def queryOpenAcct(QueryOpenAcctReq req,byte[] pk, String password){
        QueryOpenAcctResp resp = new QueryOpenAcctResp()

        String json = JsonUtil.toJson(req)

        log.info("queryOpenAcct请求参数 ：" + json)

        //构造参数
        Map<String,Object> param = getParam(json,pk,password,req.getAppid())

        log.info("queryOpenAcct加密参数 ：" + JsonUtil.toJson(param))
        //调用接口
        ResponseModel responseModel = doPost(conf.host + ApiType.QueryOpenAcct.url, param)

        if (responseModel != null && responseModel["body"]["body"] != null){
            String result = envApplication.openEnvelope(pk,password,responseModel["body"]["body"])
            log.info("queryOpenAcct请求结果 ：" + result)
            resp = JsonUtil.toObject(result,QueryOpenAcctResp.class)

        } else {
            resp.setRetCode(String.valueOf(responseModel["body"]["status"]))
            resp.setRetMsg(responseModel["body"]["data"])
        }

        return resp
    }

    /**
     * 冻结/解冻
     * @param req
     * @param pk
     * @param password
     * @return
     */
    FreezeAcctResp freezeAcct(FreezeAcctReq req,byte[] pk, String password){
        FreezeAcctResp resp = new FreezeAcctResp()

        String json = JsonUtil.toJson(req)

        log.info("freezeAcct请求参数 ：" + json)

        //构造参数
        Map<String,Object> param = getParam(json,pk,password,req.getAppid())

        //调用接口
        ResponseModel responseModel = doPost(conf.host + ApiType.FreezeAcct.url, param)

        if (responseModel != null && responseModel["body"]["body"] != null){
            String result = envApplication.openEnvelope(pk,password,responseModel["body"]["body"])
            log.info("freezeAcct请求结果 ：" + result)
            resp = JsonUtil.toObject(result,FreezeAcctResp.class)
            resp.setOrgTradeNo(req.getTradeNo())
        } else {
            resp.setRetCode(String.valueOf(responseModel["body"]["status"]))
            resp.setRetMsg(responseModel["body"]["data"])
        }

        return resp
    }


    /**
     * 解冻支付/退款
     * @param
     *
     * @param pk
     * @param password
     * @return
     */
    ThawPayRefundResp thawPayRefund(ThawPayRefundReq req, byte[] pk, String password){
        ThawPayRefundResp resp = new ThawPayRefundResp()

        String json = JsonUtil.toJson(req)

        log.info("thawPayRefund请求参数 ：" + json)

        //构造参数
        Map<String,Object> param = getParam(json,pk,password,req.getAppid())

        //调用接口
        ResponseModel responseModel = doPost(conf.host + ApiType.ThawPayRefund.url, param)

        if (responseModel != null && responseModel["body"]["body"] != null){
            String result = envApplication.openEnvelope(pk,password,responseModel["body"]["body"])
            log.info("thawPayRefund请求结果 ：" + result)
            resp = JsonUtil.toObject(result,ThawPayRefundResp.class)
            resp.setOrgTradeNo(req.getTradeNo())
            if (resp.getRetCode().equals("9990")){
                resp.setStatus(Status.SUCCESS)
            }
        } else {
            resp.setRetCode(String.valueOf(responseModel["body"]["status"]))
            resp.setRetMsg(responseModel["body"]["data"])
        }

        return resp
    }

    /**
     * 交易查询
     * @param req
     * @param pk
     * @param password
     * @return
     */
    QueryTransResultResp queryTransResult(QueryTransResultReq req, byte[] pk, String password){

        QueryTransResultResp resp = new QueryTransResultResp()

        String json = JsonUtil.toJson(req)

        log.info("queryTransResult请求参数 ：" + json)

        //构造参数
        Map<String,Object> param = getParam(json,pk,password,req.getAppid())

        //调用接口
        ResponseModel responseModel = doPost(conf.host + ApiType.QueryTransResult.url, param)

        if (responseModel != null && responseModel["body"]["body"] != null){
            String result = envApplication.openEnvelope(pk,password,responseModel["body"]["body"])
            log.info("queryTransResult请求结果 ：" + result)
            resp = JsonUtil.toObject(result,QueryTransResultResp.class)
            resp.setOrgTradeNo(req.getTradeNo())
        } else {
            resp.setRetCode(String.valueOf(responseModel["body"]["status"]))
            resp.setRetMsg(responseModel["body"]["data"])
        }

        return resp
    }



    Map getParam(String json,byte[] pk, String password,String appid){

        //参数签名
        String signature = envApplication.signJson(json, pk, password)

        //参数加密
        String body = envApplication.makeEnvelope(conf.cert, json.getBytes("UTF-8"))

        Map<String,Object> param = ["version":"1.0", "appid":appid,"signature":signature,"body":body]

        return param
    }


    ResponseModel doPost(String url, Object param){
        HttpModel httpModel = new HttpModel()
        httpModel.setMethod(MethodType.Json)
        httpModel.setUrl(url)
        httpModel.setBody(param)
        ResponseModel responseModel  =  HttpClientUtil.request(httpModel)
        return responseModel
    }
}
