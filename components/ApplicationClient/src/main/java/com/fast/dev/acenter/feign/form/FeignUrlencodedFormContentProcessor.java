package com.fast.dev.acenter.feign.form;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.form.UrlencodedFormContentProcessor;
import lombok.SneakyThrows;
import lombok.val;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class FeignUrlencodedFormContentProcessor extends UrlencodedFormContentProcessor {

    private static final char QUERY_DELIMITER = '&';

    private static final char EQUAL_SIGN = '=';

    @Override
    public void process(RequestTemplate template, Charset charset, Map<String, Object> data) throws EncodeException {
        val bodyData = new StringBuilder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry == null || entry.getKey() == null) {
                continue;
            }
            if (bodyData.length() > 0) {
                bodyData.append(QUERY_DELIMITER);
            }
            bodyData.append(createKeyValuePair(entry, charset));
        }

        val contentTypeValue = new StringBuilder()
                .append(getSupportedContentType().getHeader())
                .append("; charset=").append(charset.name())
                .toString();

        val bytes = bodyData.toString().getBytes(charset);
//        val body = Request.Body.encoded(bytes, charset);

        template.header(CONTENT_TYPE_HEADER, Collections.<String>emptyList()); // reset header
        template.header(CONTENT_TYPE_HEADER, contentTypeValue);
        template.body(bytes, charset);
    }

    private String createKeyValuePair(Map.Entry<String, Object> entry, Charset charset) {
        String encodedKey = encode(entry.getKey(), charset);
        Object value = entry.getValue();

        if (value == null) {
            return encodedKey;
        } else if (value.getClass().isArray()) {
            return createKeyValuePairFromArray(encodedKey, value, charset);
        } else if (value instanceof Collection) {
            return createKeyValuePairFromCollection(encodedKey, value, charset);
        } else if (value instanceof Map) {
            return createKeyValuePairFromMap(encodedKey, value, charset);
        }
        return new StringBuilder()
                .append(encodedKey)
                .append(EQUAL_SIGN)
                .append(encode(value, charset))
                .toString();
    }


    /**
     * 类型为Map
     *
     * @param key
     * @param values
     * @param charset
     * @return
     */
    private String createKeyValuePairFromMap(String key, Object values, Charset charset) {
        val map = (Map<String, Object>) values;

        StringBuilder result = new StringBuilder();
        map.entrySet().forEach((it) -> {
            result
                    .append(key)
                    .append("['" + it.getKey() + "']")
                    .append(EQUAL_SIGN)
                    .append(encode(it.getValue(), charset))
                    .append(QUERY_DELIMITER);

        });


        return result.toString();
    }


    private String createKeyValuePairFromCollection(String key, Object values, Charset charset) {
        val collection = (Collection) values;
        val array = collection.toArray(new Object[0]);
        return createKeyValuePairFromArray(key, array, charset);
    }

    @SneakyThrows
    private static String encode(Object string, Charset charset) {
        return URLEncoder.encode(string.toString(), charset.name());
    }


    private String createKeyValuePairFromArray(String key, Object values, Charset charset) {
        val result = new StringBuilder();
        val array = (Object[]) values;

        for (int index = 0; index < array.length; index++) {
            val value = array[index];
            if (value == null) {
                continue;
            }

            if (index > 0) {
                result.append(QUERY_DELIMITER);
            }

            result
                    .append(key)
                    .append(EQUAL_SIGN)
                    .append(encode(value, charset));
        }
        return result.toString();
    }
}
