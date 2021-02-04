package com.fast.dev.core.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class JsonHelper {


    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 转换到json字符串
     *
     * @param object
     * @return
     * @throws Exception
     */
    public String toJson(Object object, boolean format) {
        try {
            if (format) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } else {
                return objectMapper.writeValueAsString(object);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 转换到json字符串
     *
     * @param object
     * @return
     */
    public String toJson(Object object) {
        return toJson(object, false);
    }


    /**
     * 转换为对象
     *
     * @param json
     * @param cls
     * @return
     * @throws Exception
     */
    public <T> T toObject(String json, Class<T> cls) throws Exception {
        return objectMapper.readValue(json, cls);
    }
}
