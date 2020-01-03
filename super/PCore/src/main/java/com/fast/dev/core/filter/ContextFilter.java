package com.fast.dev.core.filter;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 上下文拦截器,处理上下文与服务名相同自动去掉并转发的过滤器
 */
@Log
public class ContextFilter implements Filter {


    @Value("${spring.application.name}")
    private String applicationName;



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        String value = "/" + applicationName.toLowerCase() + "/";


        if (uri.toLowerCase().indexOf(value) != 0) {
            chain.doFilter(request, response);
            return;
        }


        String target = "/" + uri.substring(value.length(), uri.length());
        log.info(String.format("Context Redirect : %s", target));
        servletRequest.getRequestDispatcher(target).forward(request, response);

    }


}
