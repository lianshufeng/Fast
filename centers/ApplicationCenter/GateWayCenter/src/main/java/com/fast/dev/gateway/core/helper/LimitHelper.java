package com.fast.dev.gateway.core.helper;

import com.fast.dev.core.util.net.IPUtil;
import com.fast.dev.gateway.core.conf.LimitUrlConf;
import com.fast.dev.gateway.core.domain.AccessRecord;
import com.fast.dev.gateway.core.model.AllowIpPart;
import com.fast.dev.gateway.core.model.LimitRole;
import com.fast.dev.gateway.core.model.Policy;
import com.fast.dev.gateway.core.service.AccessService;
import com.fast.dev.gateway.core.service.BlacklistService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 限制助手
 */

@Slf4j
@Component
@RefreshScope
public class LimitHelper {

    @Autowired
    private LimitUrlConf limitUrlConf;

    @Autowired
    private AccessService accessService;

    @Autowired
    private BlacklistService blacklistService;

    //缓存,保证数组顺序
    private List<CacheItem> _cache = new ArrayList<>();

    @Autowired
    private void init(ApplicationContext applicationContext) {
        this._cache.clear();

        if (limitUrlConf.getRoles() == null || limitUrlConf.getRoles().size() == 0) {
            return;
        }

        for (Map.Entry<String, LimitRole> entry : this.limitUrlConf.getRoles().entrySet()) {
            final CacheItem cacheItem = new CacheItem();
            //角色名
            cacheItem.setRoleName(entry.getKey());
            final LimitRole limitRole = entry.getValue();

            //策略
            cacheItem.setPolicy(limitRole.getPolicy());

            //转换为正则表达式
            cacheItem.setUrl(toRegText(limitRole.getUrl()));
            cacheItem.setExclusion(toRegText(limitRole.getExclusion()));

            //设置允许的ip
            Optional.ofNullable(entry.getValue().getAllowIp()).ifPresent((allowIp) -> {
                cacheItem.setAllowIp(Arrays.stream(allowIp).map((ip) -> {
                    AllowIpPart allowIpPart = new AllowIpPart();
                    totBothIp(ip, allowIpPart);
                    return allowIpPart;
                }).collect(Collectors.toSet()).toArray(new AllowIpPart[0]));
            });

            _cache.add(cacheItem);
        }


    }

    /**
     * 设置为正则表达式
     *
     * @param texts
     */
    private String[] toRegText(String[] texts) {
        if (texts == null) {
            return null;
        }
        String[] ret = new String[texts.length];
        for (int i = 0; i < texts.length; i++) {
            ret[i] = toReg(texts[i]);
        }
        return ret;
    }


    /**
     * 取限制访问的规则，按照顺序取匹配到的第一条规则
     *
     * @return
     */
    private CacheItem getLimitItem(String uri) {
        for (CacheItem item : this._cache) {
            if (urlMatches(item.getUrl(), uri) && !(urlMatches(item.getExclusion(), uri))) {
                return item;
            }
        }
        return null;
    }


    /**
     * 执行
     *
     * @return
     */
    public Mono<Void> execute(ServerWebExchange ctx, WebFilterChain chain) {
        final ServerHttpRequest request = ctx.getRequest();
        final ServerHttpResponse response = ctx.getResponse();

        //调用的uri
        final String uri = request.getPath().value();
        List<String> uaList = request.getHeaders().get(HttpHeaders.USER_AGENT);
        final String ua = uaList == null || uaList.size() == 0 ? "" : uaList.get(0);

        //URL过滤,如果为寻到匹配URL的规则，则放行
        CacheItem cacheItem = getLimitItem(uri);
        if (cacheItem == null) {
            return chain.filter(ctx);
        }

        //取局域网ip
        final String lanIpAddress = request.getRemoteAddress().getAddress().getHostAddress();
        long lanIpAddressValue = IPUtil.ipv4ToLong(lanIpAddress);

        //白名单
        boolean whitePass = whiteListPass(cacheItem, lanIpAddressValue);
        if (whitePass) {
            log.debug("[whitelist] - {} -> {}", lanIpAddress, uri);
            return chain.filter(ctx);
        }

        //策略访问,没有策略访问则直接拒绝
        if (cacheItem.getPolicy() == null) {
            log.debug("[block] - {} -> {}", lanIpAddressValue, uri);
            return responseBody(response, HttpStatus.FORBIDDEN, "禁止访问:拒绝");
        }


        //取出远程ip
        //转换为普通的header
        Map<String, String> headers = new HashMap<>();
        Arrays.stream(IPUtil.headNames).forEach((name) -> {
            Optional.ofNullable(request.getHeaders().get(name)).ifPresent((it) -> {
                if (it.size() > 0) {
                    headers.put(name, it.get(0));
                }
            });
        });

        String remoteIp = IPUtil.getRemoteIp(headers, lanIpAddress);


        //策略访问
        boolean access = accessPolicy(cacheItem, remoteIp, ua, uri);
        if (!access) {
            log.debug("[blacklist] - {} -> {}", remoteIp, uri);
            return responseBody(response, HttpStatus.FORBIDDEN, "禁止访问:策略限制");
        }

        log.debug("[direct] - {} -> {}", remoteIp, uri);
        return chain.filter(ctx);
    }


    /**
     * 设置放回内容
     *
     * @param response
     * @return
     */
    private Mono<Void> responseBody(ServerHttpResponse response, HttpStatus code, String text) {
        response.setStatusCode(code);
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        response.getHeaders().add("Content-Type", "text/plain; charset=utf-8");
        return response.writeWith(Flux.just(buffer));
    }


    /**
     * 白名单可否通行
     *
     * @param item
     * @param accessIp
     * @return
     */
    private boolean whiteListPass(CacheItem item, final long accessIp) {
        //白名单
        if (item.getAllowIp() != null) {
            for (AllowIpPart allowIpPart : item.getAllowIp()) {
                if (canIpPass(accessIp, allowIpPart)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 策略访问
     *
     * @param item
     * @param remoteIp
     * @return
     */
    private boolean accessPolicy(CacheItem item, final String remoteIp, String ua, final String url) {
        //查询黑名单是否存在
        if (this.blacklistService.exitsBlacklist(remoteIp, item.getRoleName())) {
            return false;
        }

        //构建对象
        final AccessRecord accessRecord = AccessRecord.builder().ip(remoteIp).ua(ua).url(url).roleName(item.getRoleName()).build();

        //记录访问
        this.accessService.record(item.getPolicy(), accessRecord);
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
        allowIpPart.setBeforeIp(IPUtil.ipv4ToLong(beforIp.toString()));
        allowIpPart.setAfterIp(IPUtil.ipv4ToLong(afterIp.toString()));

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

        //角色名
        private String roleName;

        //需要限制的url
        private String[] url;

        //限制中的排除
        private String[] exclusion;

        //允许访问的ip
        private AllowIpPart[] allowIp;

        //策略
        private Policy policy;

    }

}
