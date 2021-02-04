package com.fast.dev.acenter.feign.form;

import feign.codec.Encoder;
import feign.form.ContentProcessor;
import feign.form.ContentType;
import feign.form.FormEncoder;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Map;

public class FeignFormEncoder extends FormEncoder {


    public FeignFormEncoder() {
        setFeignUrlencodedFormContentProcessor();
    }

    public FeignFormEncoder(Encoder delegate) {
        super(delegate);
        setFeignUrlencodedFormContentProcessor();
    }


    /**
     * 设置自定义URL编码
     */
    @SneakyThrows
    private void setFeignUrlencodedFormContentProcessor() {
        Field field = FormEncoder.class.getDeclaredField("processors");
        field.setAccessible(true);
        Map<ContentType, ContentProcessor> processors = (Map<ContentType, ContentProcessor>) field.get(this);

        //自定义URL编码格式，保持和spring，controller一致
        processors.put(ContentType.URLENCODED, new FeignUrlencodedFormContentProcessor());
    }

}
