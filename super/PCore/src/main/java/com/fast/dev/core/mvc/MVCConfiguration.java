package com.fast.dev.core.mvc;

import com.fast.dev.core.helper.JsonHelper;
import com.fast.dev.core.helper.SpringBeanHelper;
import com.fast.dev.core.helper.ViewHelper;
import com.fast.dev.core.interceptors.UrlInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 视图解析器
 */
@Log
@Configuration
@EnableWebMvc
@EnableScheduling
public class MVCConfiguration implements WebMvcConfigurer {


    @Autowired
    private ApplicationContext applicationContext;


    /**
     * 静态资源
     */
    public final static String StaticResources = "resources";


    /**
     * 跨域
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String diskPath = ResourceUtils.FILE_URL_PREFIX + "/" + new ApplicationHome().getDir().getAbsolutePath() + "/" + StaticResources + "/";
        while (diskPath.indexOf("//") > -1) {
            diskPath = diskPath.replaceAll("//", "/");
        }
        log.info("disk : " + diskPath);
        registry.addResourceHandler("/" + StaticResources + "/**")
                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/" + StaticResources + "/")
                .addResourceLocations(diskPath)
        ;
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
        List<UrlInterceptor> urlInterceptorList = new ArrayList<>(m.values());
        Collections.sort(urlInterceptorList, new Comparator<UrlInterceptor>() {
            @Override
            public int compare(UrlInterceptor urlInterceptor1, UrlInterceptor urlInterceptor2) {
                return urlInterceptor2.order() > urlInterceptor1.order() ? -1 : 1;
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

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }


    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void setObjectMapper() {
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        return mappingJackson2HttpMessageConverter;
    }


    @Bean
    public JsonHelper jsonHelper() {
        return new JsonHelper();
    }


    @Bean
    public ViewHelper viewHelper() {
        return new ViewHelper();
    }


    @Bean
    public SpringBeanHelper springBeanHelper() {
        return new SpringBeanHelper();
    }

}
