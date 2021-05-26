package com.fast.dev.gateway.core.filter;

import com.fast.dev.gateway.core.helper.LimitHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
@Order(Integer.MIN_VALUE)
public class LimitUrlFilter implements WebFilter {

    @Autowired
    private LimitHelper limitHelper;

    @Override
    public Mono<Void> filter(ServerWebExchange ctx, WebFilterChain chain) {
        return limitHelper.execute(ctx, chain);
    }
}
