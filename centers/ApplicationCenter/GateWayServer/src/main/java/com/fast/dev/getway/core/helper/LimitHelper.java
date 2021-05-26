package com.fast.dev.getway.core.helper;

import com.fast.dev.core.util.net.IPUtil;
import com.fast.dev.getway.core.conf.LimitUrlConf;
import com.fast.dev.getway.core.model.AllowIpPart;
import com.fast.dev.getway.core.model.LimitRole;
import com.fast.dev.getway.core.util.IpUtil;
import com.netflix.zuul.context.RequestContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 限制助手
 */

@Slf4j
@Component
public class LimitHelper {


    @Autowired
    private LimitUrlConf limitUrlConf;

    //缓存
    private List<CacheItem> _cache = new ArrayList<>();

    // 需要过滤的URL的正则表达式
    private Set<String> needLimitUri = new HashSet<>();

    @Autowired
    private void init() {
        this._cache.clear();
        this.needLimitUri.clear();

        if (limitUrlConf.getRoles() == null || limitUrlConf.getRoles().size() == 0) {
            return;
        }

        // key = urls ,varlue 为规则
        for (Map.Entry<String, LimitRole> entry : this.limitUrlConf.getRoles().entrySet()) {
            CacheItem cacheItem = new CacheItem();
            BeanUtils.copyProperties(entry.getValue(), cacheItem, "allowIp");

            //转换为正则表达式
            setRegText(cacheItem.getUrl());
            setRegText(cacheItem.getExclusion());

            //设置允许的ip
            Optional.ofNullable(entry.getValue().getAllowIp()).ifPresent((allowIp) -> {
                cacheItem.setAllowIp(Arrays.stream(allowIp).map((ip) -> {
                    AllowIpPart allowIpPart = new AllowIpPart();
                    totBothIp(ip, allowIpPart);
                    return allowIpPart;
                }).collect(Collectors.toSet()).toArray(new AllowIpPart[0]));
            });


            this.needLimitUri.addAll(List.of(cacheItem.getUrl()));
            this._cache.add(cacheItem);
        }


    }

    /**
     * 设置为正则表达式
     *
     * @param texts
     */
    private void setRegText(String[] texts) {
        if (texts == null) {
            return;
        }
        for (int i = 0; i < texts.length; i++) {
            texts[i] = toReg(texts[i]);
        }
    }


    /**
     * 过滤条件
     *
     * @return
     */
    public boolean shouldFilter(String uri) {
        for (String u : this.needLimitUri) {
            if (uri.matches(u)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 执行
     *
     * @param rtx
     * @return
     */
    public Object execute(RequestContext rtx) {

        //调用的uri
        String uri = rtx.getRequest().getRequestURI();


        //远程调用主机的ip
        long remoteIp = IpUtil.ipv4ToLong(IPUtil.getRemoteIp(rtx.getRequest()));

        //访问是否放行通过
        boolean accessPass = canAccessPass(uri, remoteIp);


        //如果不允许放行则拦截所有请求并返回403
        if (!accessPass) {
            rtx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由
            rtx.setResponseStatusCode(403);// 返回错误码
        }
        log.info("[{}]  {}  -> {}", accessPass ? "direct" : "block", rtx.getRequest().getRemoteAddr(), uri);
        return null;
    }


    /**
     * 是否放行访问的url
     *
     * @param uri
     * @param remoteIp
     * @return
     */
    private boolean canAccessPass(String uri, long remoteIp) {
        for (CacheItem item : this._cache) {
            //匹配URL + 排除URL
            if (urlMatches(item.getUrl(), uri) && !(urlMatches(item.getExclusion(), uri))) {
                //白名单为空的直接拒绝转换
                if (item.getAllowIp() == null || item.getAllowIp().length == 0) {
                    return false;
                }
                //白名单
                boolean access = false;
                for (AllowIpPart allowIpPart : item.getAllowIp()) {
                    if (canIpPass(remoteIp, allowIpPart)) {
                        access = true;
                        break;
                    }
                }
                return access;
            }
        }
        return true;
    }


    /**
     * 将一条URL在数组中进行匹配
     *
     * @return
     */
    private boolean urlMatches(String[] regular, String url) {
        if (regular == null || regular.length == 0) {
            return false;
        }
        for (String reg : regular) {
            if (url.matches(reg)) {
                return true;
            }
        }
        return false;
    }


    /**
     * ip是否可以通过
     *
     * @param ip
     * @param allowIpPart
     * @return
     */
    private boolean canIpPass(long ip, AllowIpPart allowIpPart) {
        return ip >= allowIpPart.getBeforeIp() && ip <= allowIpPart.getAfterIp();
    }


    /**
     * 获取两端的ip
     *
     * @param ip
     * @return
     */
    private static void totBothIp(String ip, AllowIpPart allowIpPart) {

        String[] ips = ip.split("\\.");
        StringBuffer beforIp = new StringBuffer();
        StringBuffer afterIp = new StringBuffer();
        for (int i = 0; i < ips.length; i++) {
            appendToIp(ips[i], beforIp, afterIp);
        }


        //设置转换后的十进制
        allowIpPart.setBeforeIp(IpUtil.ipv4ToLong(beforIp.toString()));
        allowIpPart.setAfterIp(IpUtil.ipv4ToLong(afterIp.toString()));

    }

    //添加到ip段落里
    private static void appendToIp(String b, StringBuffer beforIp, StringBuffer afterIp) {
        if ("*".equals(b)) {
            beforIp.append("0");
            afterIp.append("255");
        } else {
            beforIp.append(b);
            afterIp.append(b);
        }
        beforIp.append(".");
        afterIp.append(".");
    }

    /**
     * url 转换到正则表达式
     *
     * @param url
     */
    private static String toReg(String url) {
        String reg = url.replaceAll("\\*", ".*");
        reg = ("^(" + reg + ")$");
        return reg;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class CacheItem {

        /**
         * 需要限制的url
         */
        private String[] url;

        /**
         * 限制中的排除
         */
        private String[] exclusion;


        /**
         * 允许访问的ip
         */
        private AllowIpPart[] allowIp;


    }

}
