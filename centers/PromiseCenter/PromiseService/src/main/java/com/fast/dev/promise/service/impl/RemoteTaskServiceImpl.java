package com.fast.dev.promise.service.impl;

import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.model.ResponseStatusModel;
import com.fast.dev.promise.model.ResponseTaskModel;
import com.fast.dev.promise.service.RemoteTaskService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Log
@Component
public class RemoteTaskServiceImpl implements RemoteTaskService {

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public RequestParmModel put(RequestParmModel userModel) {
        return request("manager/api/put", userModel, RequestParmModel.class);
    }

    @Override
    public ResponseTaskModel query(String id) {
        return request("api/query", "id=" + id, ResponseTaskModel.class);
    }

    @Override
    public Map<String, ResponseStatusModel> heartbeat(String... ids) {
        return request("api/heartbeat", "id=" + joinArray(ids), Map.class);
    }

    @Override
    public Map<String, Boolean> remove(String... id) {
        return request("api/remove", "id=" + joinArray(id), Map.class);
    }

    @Override
    public Boolean doit(String id) {
        return request("api/doit", "id=" + id, Boolean.class);
    }


    /**
     * 合并数组
     *
     * @param info
     * @return
     */
    private String joinArray(String... info) {
        String ret = "";
        for (String i : info) {
            ret += i + ",";
        }
        return ret;
    }


    /**
     * 网络请求
     *
     * @param uri
     * @param o
     * @return
     */
    private <T> T request(String uri, Object o, Class retClass) {
        String url = buildUrl(uri);
        ResponseEntity responseEntity = null;
        if (o instanceof String) {
            responseEntity = this.restTemplate.getForEntity(url + "?" + String.valueOf(o), retClass);
        } else {
            responseEntity = this.restTemplate.postForEntity(url, o, retClass);
        }

        try {
            return (T) responseEntity.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 构建请求的url
     *
     * @param uri
     * @return
     */
    private String buildUrl(String uri) {
        return "http://PROMISETASK/" + uri;
    }

}
