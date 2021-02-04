package com.fast.dev.ucenter.security.helper;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.net.IPUtil;
import com.fast.dev.ucenter.security.cache.UserTokenCache;
import com.fast.dev.ucenter.security.model.UserAuth;
import com.fast.dev.ucenter.security.model.UserAuthenticationToken;
import com.fast.dev.ucenter.security.resauth.ResourcesAuthHelper;
import com.fast.dev.ucenter.security.service.UserCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户安全助手
 */
@Slf4j
public class SecurityAuthenticationHelper {

    //令牌的关键词
    private static final String[] UserTokenName = new String[]{"_uToken", "uToken"};


    //令牌的长度
    private static final int UserTokenlength = 32;


    @Autowired
    private UserTokenCache userTokenCache;

    @Autowired
    private UserCenterService remoteUserCenterService;


    @Autowired
    private ResourcesAuthHelper resourcesAuthHelper;


    /**
     * 权限拦截
     *
     * @param httpServletRequest
     */
    public void authenticate(final HttpServletRequest httpServletRequest) {
        //先清空缓存
        release();
        String uToken = getToken(httpServletRequest);
        log.debug("[uToken] : {}  -> url : {} -> UA : {} -> ip : {}", uToken, httpServletRequest.getRequestURL(), httpServletRequest.getHeader("user-agent"), IPUtil.getRemoteIp(httpServletRequest));
        if (StringUtils.isEmpty(uToken)) {
            return;
        }

        boolean needUpdateCache = false;
        //缓存或远程查询
        UserAuth userAuthenticationModel = this.userTokenCache.get(uToken);
        log.debug("[CacheUserAuth] : {}", JsonUtil.toJson(userAuthenticationModel));
        if (userAuthenticationModel == null) {
            userAuthenticationModel = remoteUserCenterService.query(uToken);
            log.debug("[NewUserAuth] : {}", JsonUtil.toJson(userAuthenticationModel));
            needUpdateCache = true;
        }
        //设置当前用户的角色
        setUserAuthentication(httpServletRequest, userAuthenticationModel);
        //缓存数据
        if (needUpdateCache == true && userAuthenticationModel != null) {
            if (userAuthenticationModel.getAuths() != null) {
                Set<String> auths = new HashSet<>(userAuthenticationModel.getAuths());
                userAuthenticationModel.setAuths(auths);
            }
            this.userTokenCache.put(uToken, userAuthenticationModel);
        }
    }


    /**
     * 清空当前用户身份
     */
    public void release() {
        SecurityContextHolder.clearContext();
    }


    /**
     * 设置当前用户的权限
     */
    private void setUserAuthentication(HttpServletRequest httpServletRequest, UserAuth userAuth) {
        if (userAuth == null) {
            return;
        }

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String authName : userAuth.getAuths()) {
            authorities.add(new SimpleGrantedAuthority(authName));
        }


        UserAuthenticationToken userAuthenticationToken = new UserAuthenticationToken(authorities);
        userAuthenticationToken.setAuthenticated(true);
        userAuthenticationToken.setDetails(userAuth);


        //置为当前用户的请求
        SecurityContextHolder.getContext().setAuthentication(userAuthenticationToken);
    }


    /**
     * 拿到 用户令牌
     *
     * @param httpServletRequest
     * @return
     */
    public String getToken(final HttpServletRequest httpServletRequest) {

        for (String tokenName : UserTokenName) {
            StringBuffer uTokenBuffer = new StringBuffer();
            if (getTokenFromCookie(httpServletRequest, tokenName, uTokenBuffer)) {
                log.debug("[UserToken]  : cookie -> {}", uTokenBuffer);
                return uTokenBuffer.toString();
            }
            if (getTokenFromHead(httpServletRequest, tokenName, uTokenBuffer)) {
                log.debug("[UserToken]  : head -> {}", uTokenBuffer);
                return uTokenBuffer.toString();
            }
            if (getTokenFromParameter(httpServletRequest, tokenName, uTokenBuffer)) {
                log.debug("[UserToken]  : parameter -> {}", uTokenBuffer);
                return uTokenBuffer.toString();
            }
        }
        return null;
    }


    /**
     * 通过cookies 拿到 token
     *
     * @param httpServletRequest
     * @return
     */
    private static boolean getTokenFromCookie(final HttpServletRequest httpServletRequest, final String tokenName, final StringBuffer uTokenBuffer) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    return checkUserToken(cookie.getValue(), uTokenBuffer);
                }
            }
        }
        return false;
    }

    /**
     * 通过head取token
     *
     * @param httpServletRequest
     * @return
     */
    private static boolean getTokenFromHead(final HttpServletRequest httpServletRequest, final String tokenName, final StringBuffer uTokenBuffer) {
        return checkUserToken(httpServletRequest.getHeader(tokenName), uTokenBuffer);
    }


    /**
     * 通过参数获取用户令牌
     *
     * @param httpServletRequest
     * @param tokenName
     * @return
     */
    private static boolean getTokenFromParameter(final HttpServletRequest httpServletRequest, final String tokenName, final StringBuffer uTokenBuffer) {
        return checkUserToken(httpServletRequest.getParameter(tokenName), uTokenBuffer);
    }

    /**
     * 检查用户令牌,校验成功返回令牌，失败返回null
     *
     * @param uToken
     * @return
     */
    private static boolean checkUserToken(final String uToken, final StringBuffer uTokenBuffer) {
        if (uToken != null && uToken.length() == UserTokenlength) {
            uTokenBuffer.append(uToken);
            return true;
        }
        return false;
    }

}
