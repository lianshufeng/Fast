package com.fast.dev.mq.mqserver.core.service;

import com.fast.dev.mq.mqserver.core.client.ActiveMQClient;
import com.fast.dev.mq.mqserver.core.conf.MQConf;
import com.fast.dev.mq.mqserver.core.constant.MQConstant;
import com.fast.dev.mq.mqserver.core.model.RequestModel;
import com.fast.dev.mq.mqserver.core.model.ResponseModel;
import com.fast.dev.mq.mqserver.core.util.FormUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
public class RemoteCallService {

    @Autowired
    private MQConf mqConf;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ActiveMQClient activeMQClient;

    /**
     * 调用远程业务
     *
     * @param requestModel
     */
    public void call(RequestModel requestModel) {
        Object parameter = requestModel.getParameter();
        RequestType requestType = checkRequstType(requestModel);
        String url = "http://" + formatUrl(mqConf.getGatewayServiceName() + "/" + requestModel.getUrl());
        ResponseEntity<?> ret = null;

        switch (requestType) {
            case Get:
                ret = getRequest(url, requestModel);
                break;
            case Post:
                ret = postRequest(url, requestModel);
                break;
            case Json:
                ret = jsonRequest(url, requestModel);
                break;
        }

        //响应的topic
        String responseTopic = MQConstant.ResponseTopic + requestModel.getToken();


        //发送到响应的topic里
        this.activeMQClient.sendData(responseTopic, toResponseModel(requestModel, ret));

    }


    /**
     * 转换为输出的model
     *
     * @param entity
     * @return
     */
    private ResponseModel toResponseModel(RequestModel requestModel, ResponseEntity<?> entity) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setMsgId(requestModel.getMsgId());
        responseModel.setCode(entity.getStatusCodeValue());
        responseModel.setBody(entity.getBody());
        return responseModel;
    }


    /**
     * GET 请求
     *
     * @param url
     * @param requestModel
     * @return
     */
    private ResponseEntity<?> getRequest(String url, RequestModel requestModel) {
        HttpEntity requestEntity = new HttpEntity(null, header(requestModel));
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);
    }


    /**
     * Post请求
     *
     * @param url
     * @param requestModel
     * @return
     */
    private ResponseEntity<?> postRequest(String url, RequestModel requestModel) {
        HttpHeaders httpHeaders = header(requestModel);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<Object> requestEntity = new HttpEntity<>(FormUtil.toText(requestModel.getParameter()), httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
    }


    /**
     * JSON 请求
     *
     * @param url
     * @param requestModel
     * @return
     */
    private ResponseEntity<?> jsonRequest(String url, RequestModel requestModel) {
        HttpHeaders httpHeaders = header(requestModel);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestModel.getParameter(), httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
    }

    /**
     * 构建请求的header
     *
     * @param requestModel
     * @return
     */
    private HttpHeaders header(RequestModel requestModel) {
        HttpHeaders requestHeaders = new HttpHeaders();
        if (requestModel.getHeader() != null) {
            requestModel.getHeader().entrySet().forEach((it) -> {
                requestHeaders.add(it.getKey(), it.getValue());
            });
        }
        return requestHeaders;
    }


    /**
     * 格式化URL
     *
     * @param url
     * @return
     */
    private static String formatUrl(String url) {
        return url.replaceAll("\\\\", "/").replaceAll("//", "/");
    }


    /**
     * 根据参数检查请求的类型
     *
     * @return
     */
    private static RequestType checkRequstType(RequestModel model) {
        Object parameter = model.getParameter();
        if (parameter == null && model.isJson() == false) {
            return RequestType.Get;
        } else if (model.isJson()) {
            return RequestType.Json;
        } else {
            return RequestType.Post;
        }
    }


    /**
     * 请求类型
     */
    public enum RequestType {
        Get,
        Post,
        Json
    }

}
