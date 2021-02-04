package com.fast.dev.acenter.core.config;

import com.fast.dev.acenter.feign.FeignPageableDefaultFieldQueryMapEncoder;
import com.fast.dev.acenter.feign.FeignPageableDefaultParameterProcessor;
import com.fast.dev.acenter.feign.form.FeignFormEncoder;
import com.fasterxml.jackson.databind.Module;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.optionals.OptionalDecoder;
import feign.querymap.FieldQueryMapEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.support.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
//@Import(FeignClientsConfiguration.class)
public class FeignConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;


    @Autowired(required = false)
    private SpringDataWebProperties springDataWebProperties;

    /**
     * 表单编码
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public Encoder springEncoder(ObjectProvider<AbstractFormWriter> formWriterProvider) {
        return new FeignFormEncoder(new SpringEncoder(this.messageConverters));
    }

    @Bean
    @ConditionalOnMissingBean
    public Decoder springDecoder() {
        return new SpringDecoder(this.messageConverters);
    }


    /**
     * 反序列化
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public Decoder feignDecoder() {
        return new OptionalDecoder(new ResponseEntityDecoder(springDecoder()));
    }


    @Bean
    @ConditionalOnClass(name = "org.springframework.data.domain.Page")
    public Module pageJacksonModule() {
        return new PageJacksonModule();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.data.domain.Page")
    public Module sortModule() {
        return new SortJacksonModule();
    }


    @Bean
    @ConditionalOnClass(name = "org.springframework.data.domain.Pageable")
    @ConditionalOnMissingBean
    public Encoder feignEncoderPageable(
            ObjectProvider<AbstractFormWriter> formWriterProvider) {
        PageableSpringEncoder encoder = new PageableSpringEncoder(
                springEncoder(formWriterProvider));

        if (springDataWebProperties != null) {
            encoder.setPageParameter(
                    springDataWebProperties.getPageable().getPageParameter());
            encoder.setSizeParameter(
                    springDataWebProperties.getPageable().getSizeParameter());
            encoder.setSortParameter(
                    springDataWebProperties.getSort().getSortParameter());
        }
        return encoder;
    }


    @Bean
    public AnnotatedParameterProcessor feignPageableDefaultParameterProcessor() {
        return new FeignPageableDefaultParameterProcessor();
    }


    /**
     * PageableDefault 转换参数
     *
     * @return
     */
    @Bean
    public FieldQueryMapEncoder feignPagebleDefaultFieldQueryMapEncoder() {
        return new FeignPageableDefaultFieldQueryMapEncoder();
    }


}
