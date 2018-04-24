package com.fast.dev.userserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 视图解析器
 */
@EnableWebMvc
@Configuration
public class MVCConfiguration implements WebMvcConfigurer {


    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
//        允许json视图解析器
        registry.enableContentNegotiation(jsonView());
    }


    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true)
                .ignoreAcceptHeader(true)
                .parameterName("mediaType")
                .defaultContentType(MediaType.TEXT_HTML)
                .mediaType("html", MediaType.TEXT_HTML)
                .mediaType("json", MediaType.APPLICATION_JSON);


    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // http 处理字符串
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setDefaultCharset(Charset.forName("UTF-8"));
        converters.add(stringConverter);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }


    /**
     * json 视图解析器
     *
     * @return
     */
    @Bean
    public View jsonView() {
        MappingJackson2JsonView mappingJackson2JsonView = new MappingJackson2JsonView();
        return mappingJackson2JsonView;
    }

}
