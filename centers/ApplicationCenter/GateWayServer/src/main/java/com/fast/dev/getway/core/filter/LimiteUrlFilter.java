package com.fast.dev.getway.core.filter;


import com.fast.dev.getway.core.helper.LimitHelper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 限制URL的拦截器
 */
@Component
public class LimiteUrlFilter extends ZuulFilter {

    @Autowired
    private LimitHelper limitHelper;


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return this.limitHelper.shouldFilter(RequestContext.getCurrentContext().getRequest().getRequestURI());
    }

    @Override
    public Object run() throws ZuulException {
        return this.limitHelper.execute(RequestContext.getCurrentContext());
    }
}
