package com.fast.dev.getway.core.helper;

import com.fast.dev.getway.core.conf.LimitUrlConf;
import com.fast.dev.getway.core.model.AllowIpPart;
import com.fast.dev.getway.core.model.LimitRole;
import com.fast.dev.getway.core.util.IpUtil;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 限制助手
 */

@Log
@Component
public class LimitHelper {


    @Autowired
    private LimitUrlConf limitUrlConf;

    //缓存
//    private Map<Set<String>, AllowIpPart[]> _cache = new ConcurrentHashMap<>();
    private Map<Set<String>, Set<AllowIpPart>> _cache = new HashMap<>();

    // 需要过滤的URL的正则表达式
//    private Set<String> needLimitUri = Collections.synchronizedSet(new HashSet());
    private Set<String> needLimitUri = new HashSet();

    @Autowired
    private void init() {
        this._cache.clear();
        this.needLimitUri.clear();

        if (limitUrlConf.getRoles()==null || limitUrlConf.getRoles().size()==0){
            return;
        }

        for (Map.Entry<String, LimitRole> entry : limitUrlConf.getRoles().entrySet()) {
            LimitRole limitRole = entry.getValue();

            //正则表达式
            Set<String> regUriList = new HashSet();
            Set<AllowIpPart> allowIpParts = new HashSet();

            for (String url : limitRole.getUrl()) {
                //转换为需要匹配的正则表达式
                String urlReg = toReg(url);
                this.needLimitUri.add(urlReg);

                //允许的ip
                for (String ip : limitRole.getAllowIp()) {
                    AllowIpPart allowIpPart = new AllowIpPart();
                    totBothIp(ip, allowIpPart);
                    allowIpParts.add(allowIpPart);
                }


                //记录每一个正则表达式
                regUriList.add(urlReg);
                this.needLimitUri.add(urlReg);
            }
            this._cache.put(regUriList, allowIpParts);

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
        long remoteIp = IpUtil.ipv4ToLong(rtx.getRequest().getRemoteAddr());

        //访问是否放行通过
        boolean accessPass = canAccessPass(uri, remoteIp);

        //如果不允许放行则拦截所有请求并返回403
        if (!accessPass) {
            rtx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由
            rtx.setResponseStatusCode(403);// 返回错误码
        }

        log.info("[pass] " + rtx.getRequest().getRemoteAddr() + " -> " + uri);

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
        for (Map.Entry<Set<String>, Set<AllowIpPart>> entry : this._cache.entrySet()) {
            //多条正则
            for (String reg : entry.getKey()) {
                //单条正则匹配成功
                if (uri.matches(reg)) {
                    //多条ip规则判断
                    for (AllowIpPart allowIpPart : entry.getValue()) {
                        if (canIpPass(remoteIp, allowIpPart)) {
                            return true;
                        }
                    }
                }
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

}
