package com.fast.dev.gateway.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Configuration
public class CorsWebFilterConfig {


    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedOriginPattern("*");
        config.setMaxAge(3600l);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }


    /**
     * 过滤多个重复的跨域标识
     */

    @Component
    public class CorsResponseHeaderFilter implements GlobalFilter, Ordered {

        private static final String ANY = "*";

        @Override
        public int getOrder() {
            // 指定此过滤器位于NettyWriteResponseFilter之后
            // 即待处理完响应体后接着处理响应头
            return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
        }

        @Override
        @SuppressWarnings("serial")
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                exchange.getResponse().getHeaders().entrySet().stream()
                        .filter(kv -> (kv.getValue() != null && kv.getValue().size() > 1))
                        .filter(kv -> (kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
                                || kv.getKey().equals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)
                                || kv.getKey().equals(HttpHeaders.VARY)))
                        .forEach(kv ->
                        {
                            // Vary只需要去重即可
                            if (kv.getKey().equals(HttpHeaders.VARY))
                                kv.setValue(kv.getValue().stream().distinct().collect(Collectors.toList()));
                            else {
                                List<String> value = new ArrayList<>();
                                if (kv.getValue().contains(ANY)) {  //如果包含*，则取*
                                    value.add(ANY);
                                    kv.setValue(value);
                                } else {
                                    value.add(kv.getValue().get(0)); // 否则默认取第一个
                                    kv.setValue(value);
                                }
                            }
                        });
            }));
        }
    }

}
