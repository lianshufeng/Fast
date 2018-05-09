package com.fast.dev.core.mvc;

import com.fast.dev.core.interceptors.UrlInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.nio.charset.Charset;
import java.util.*;

/**
 * 视图解析器
 */
@Configuration
@EnableWebMvc
@EnableScheduling
public class MVCConfiguration implements WebMvcConfigurer {


    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
//        允许json视图解析器
        registry.enableContentNegotiation(jsonView());
    }


    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
//        configurer
//                .favorPathExtension(true)
//                .ignoreAcceptHeader(true)
//                .parameterName("mediaType")
//                .defaultContentType(MediaType.TEXT_HTML)
//                .mediaType("html", MediaType.TEXT_HTML)
//                .mediaType("json", MediaType.APPLICATION_JSON_UTF8)
//                .favorPathExtension(false)
//
//        ;


    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // http 处理字符串
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setDefaultCharset(Charset.forName("UTF-8"));
        converters.add(stringConverter);



        // JSON 视图
        MappingJackson2HttpMessageConverter json = new MappingJackson2HttpMessageConverter();
        converters.add(json);


    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
//        registry.addResourceHandler("/templates/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/templates/");
        registry.addResourceHandler("/resources/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/resources/");
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


    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Map<String, UrlInterceptor> m = this.applicationContext.getBeansOfType(UrlInterceptor.class);
        // 存储接口实现对应的spring的bean名称
        Map<UrlInterceptor, String> interceptorCache = new HashMap<UrlInterceptor, String>();
        for (String key : m.keySet()) {
            interceptorCache.put(m.get(key), key);
        }
        // 优先级排序
        List<UrlInterceptor> urlInterceptorList = new ArrayList<UrlInterceptor>();
        urlInterceptorList = CollectionUtils.arrayToList(m.values().toArray(new UrlInterceptor[0]));
        Collections.sort(urlInterceptorList, new Comparator<UrlInterceptor>() {
            @Override
            public int compare(UrlInterceptor urlInterceptor1, UrlInterceptor urlInterceptor2) {
                return urlInterceptor2.level() > urlInterceptor1.level() ? -1 : 1;
            }
        });
        // 依次添加
        for (UrlInterceptor urlInterceptor : urlInterceptorList) {
            String[] addPathPatterns = urlInterceptor.addPathPatterns();
            String[] excludePathPatterns = urlInterceptor.excludePathPatterns();
            InterceptorRegistration interceptorRegistration = registry.addInterceptor(urlInterceptor);
            // 添加拦截列表
            if (addPathPatterns != null) {
                interceptorRegistration.addPathPatterns(addPathPatterns);
            }
            // 添加过滤列表
            if (excludePathPatterns != null) {
                interceptorRegistration.excludePathPatterns(excludePathPatterns);
            }
        }
    }

    /**
     * 异常处理
     *
     * @param resolvers
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.addAll(this.applicationContext.getBeansOfType(HandlerExceptionResolver.class).values());
    }


}
