package com.fast.dev.open.api.server.core.helper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OpenApiRequetHelper {

    @Autowired
    private RestTemplate restTemplate;

    //Json 格式的定界符
    private List<String> JsonKeyWord = List.of("{", "[", "\"");

    /**
     * 请求
     *
     * @param url
     * @param body
     * @return
     */
    public <T> T request(final Class<T> responseType, final String url, final Map<String, String> header, final String body) {
        //构建header
        HttpHeaders headers = new HttpHeaders();
        header.entrySet().forEach((it) -> {
            headers.set(it.getKey(), it.getValue());
        });

        //格式化地址
        String requestUrl = url;
        while (requestUrl.substring(0, 1).equals("/")) {
            requestUrl = requestUrl.substring(1, requestUrl.length());
        }
        requestUrl = "http://" + requestUrl;
        log.info("request : {}", requestUrl);


        //get
        if (!StringUtils.hasText(body)) {
            return get(responseType, requestUrl, headers);
        }
        //json
        if (JsonKeyWord.contains(body.substring(0, 1))) {
            return json(responseType, requestUrl, headers, body);
        }

        //post
        return post(responseType, requestUrl, headers, body);
    }


    /**
     * Get 请求
     *
     * @param responseType
     * @param url
     * @param <T>
     * @return
     */
    private <T> T get(Class<T> responseType, String url, HttpHeaders headers) {
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType).getBody();
    }


    /**
     * json
     *
     * @param responseType
     * @param url
     * @param body
     * @param <T>
     * @return
     */
    @SneakyThrows
    private <T> T json(Class<T> responseType, String url, HttpHeaders headers, String body) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, request, responseType);
    }

    /**
     * POST 请求
     *
     * @param responseType
     * @param url
     * @param body
     * @param <T>
     * @return
     */
    @SneakyThrows
    private <T> T post(Class<T> responseType, String url, HttpHeaders headers, String body) {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(url, request, responseType);
    }


}
