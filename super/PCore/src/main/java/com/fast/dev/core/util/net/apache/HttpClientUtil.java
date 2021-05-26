package com.fast.dev.core.util.net.apache;

import com.fast.dev.core.util.JsonUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.util.StreamUtils;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j
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
        @Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Integer code = request(httpModel, byteArrayOutputStream, headers);
        byte[] content = byteArrayOutputStream.toByteArray();
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
    @SneakyThrows
    public static Integer request(HttpModel httpModel, OutputStream outputStream, Map<String, Set<Object>> headers) throws IOException {
        //构建超时参数
        RequestConfig.Builder builder = RequestConfig.custom();
        if (httpModel.getTimeOut() != null) {
            builder.setSocketTimeout(httpModel.getTimeOut());
        }
        RequestConfig requestConfig = builder.build();

        //忽略ssl
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();

//        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpRequestBase requestBase = null;
        String[] urls = httpModel.getUrl().split("://");

        String url = urls[0] + "://" + UrlEncodeUtil.encode(urls[1]);

        //请求类型的判断
        if (httpModel.getMethod() == MethodType.Get || httpModel.getMethod() == null) {
            requestBase = new HttpGet(url);
        } else if (httpModel.getMethod() == MethodType.Post || httpModel.getMethod() == MethodType.Json) {
            HttpPost httpPost = new HttpPost(url);
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
        @Cleanup InputStream inputStream = response.getEntity().getContent();
        int size = StreamUtils.copy(inputStream, outputStream);
        log.debug("requestUrl : " + httpModel.getUrl() + " , responseSize : " + size);
        return response.getStatusLine().getStatusCode();
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


    /**
     * 创建post信息
     *
     * @param m
     * @return
     */
    public static String buildPostInfo(Map<String, Object> m) {
        final StringBuilder sb = new StringBuilder();
        m.entrySet().forEach((it) -> {
            Object val = it.getValue();
            List<String> values;
            if (val instanceof Collection) {
                values = new ArrayList<String>((Collection<String>) val);
            } else if (val.getClass().isArray()) {
                values = Arrays.asList((String[]) val);
            } else {
                values = new ArrayList<String>() {{
                    add(String.valueOf(it.getValue()));
                }};
            }
            appendFormInfo(sb, it.getKey(), values);
        });
        return sb.toString();
    }

    /**
     * 构建表单信息
     *
     * @param sb
     * @param key
     * @param values
     */
    private static void appendFormInfo(StringBuilder sb, String key, List<String> values) {
        for (String value : values) {
            sb.append(key + "=" + value + "&");
        }
    }


}
