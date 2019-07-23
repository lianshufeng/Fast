package com.fast.dev.core.util.net.apache;

import com.fast.dev.core.util.JsonUtil;
import lombok.extern.java.Log;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log
public class HttpClientUtil {


    /**
     * 网络请求
     *
     * @param httpModel
     * @return
     * @throws IOException
     */
    public static ResponseModel request(HttpModel httpModel) throws IOException {
        Map<String, Set<Object>> headers = new HashMap<>();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int code = request(httpModel, byteArrayOutputStream, headers);
        byte[] content = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        Object body = getBody(headers, content);
        return ResponseModel.builder().headers(headers).body(body).code(code).build();
    }


    /**
     * 构建响应
     *
     * @return
     */
    public static Map<String, Object> buildResponse(Map<String, Set<Object>> headers, Object body) {
        return new HashMap<String, Object>() {{
            put("headers", headers);
            put("body", body);
        }};
    }


    /**
     * 网络请求
     *
     * @param httpModel
     * @param outputStream
     * @return
     */
    public static int request(HttpModel httpModel, OutputStream outputStream, Map<String, Set<Object>> headers) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpRequestBase requestBase = null;
        //请求类型的判断
        if (httpModel.getMethod() == MethodType.Get || httpModel.getMethod() == null) {
            requestBase = new HttpGet(httpModel.getUrl());
        } else if (httpModel.getMethod() == MethodType.Post || httpModel.getMethod() == MethodType.Json) {
            HttpPost httpPost = new HttpPost(httpModel.getUrl());
            httpPost.setEntity(buildHttpEntity(httpModel));
            requestBase = httpPost;
        }

        //设置请求头
        if (httpModel.getHeader() != null) {
            for (Map.Entry<String, Object> entry : httpModel.getHeader().entrySet()) {
                requestBase.setHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }


        //开始请求
        CloseableHttpResponse response = httpclient.execute(requestBase);

        if (headers != null) {
            for (Header header : response.getAllHeaders()) {
                Set<Object> val = headers.get(header.getName());
                if (val == null) {
                    val = new HashSet<>();
                    headers.put(header.getName(), val);
                }
                val.add(header.getValue());
            }
        }

        //转发数据流
        InputStream inputStream = null;

        try {
            inputStream = response.getEntity().getContent();
            int size = StreamUtils.copy(inputStream, outputStream);
            log.info("requestUrl : " + httpModel.getUrl() + " , responseSize : " + size);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
//            e.printStackTrace();
            log.info("error : " + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }

        return -1;
    }


    /**
     * 获取body部分
     *
     * @param headers
     * @param content
     * @return
     */
    public static Object getBody(Map<String, Set<Object>> headers, byte[] content) {
        Set<Object> contentTypeHeader = headers.get("Content-Type");


        Object ret = null;

        String contentType = null;
        String charset = null;
        if (contentTypeHeader != null && contentTypeHeader.size() > 0) {
            String val = String.valueOf(contentTypeHeader.toArray(new Object[0])[0]);
            String[] tmp = val.split(";");
            if (tmp.length > 0) {
                contentType = tmp[0];
            }
            if (tmp.length > 1) {
                charset = tmp[1];
            }
        }

        //获取字符编码
        if (charset != null) {
            String[] charsetArr = charset.split("=");
            if (charset.length() > 0) {
                charset = charsetArr[1];
            }
        }


        try {
            String contentStr = new String(content, charset == null ? "UTF-8" : charset);
            if ("application/json".equals(contentType)) {
                ret = JsonUtil.toObject(contentStr, Object.class);
            } else {
                ret = contentStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return ret;
    }


    /**
     * 创建数据
     *
     * @param httpModel
     * @return
     */
    private static StringEntity buildHttpEntity(HttpModel httpModel) {
        String body = null;
        String mimeType = null;
        if (httpModel.getMethod() == MethodType.Post) {
            mimeType = "application/x-www-form-urlencoded";
            body = httpModel.getBody() == null ? "" : String.valueOf(httpModel.getBody());
        } else if (httpModel.getMethod() == MethodType.Json) {
            mimeType = "application/json";
            body = httpModel.getBody() == null ? "{}" : JsonUtil.toJson(httpModel.getBody());
        }
        return new StringEntity(body, ContentType.create(mimeType, httpModel.getCharset()));
    }


}
