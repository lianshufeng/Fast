package com.fast.dev.ucenter.security.helper;

import com.fast.dev.ucenter.core.service.UserManagerService;
import com.fast.dev.ucenter.security.cache.UserTokenCache;
import com.fast.dev.ucenter.security.model.UserAuthenticationModel;
import com.fast.dev.ucenter.security.model.UserAuthenticationToken;
import com.fast.dev.ucenter.security.service.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户安全助手
 */
public class SecurityAuthenticationHelper {

    private static final String UserTokenName = "_uToken";


    @Autowired
    private UserTokenCache userTokenCache;

    @Autowired
    private UserCenterService remoteUserCenterService;


    @Autowired
    private UserManagerService userManagerService;


    /**
     * 权限拦截
     *
     * @param httpServletRequest
     */
    public void authenticate(HttpServletRequest httpServletRequest) {
        //先清空缓存
        SecurityContextHolder.clearContext();
        String uToken = getToken(httpServletRequest);
        if (uToken == null) {
            return;
        }
        UserAuthenticationModel userAuthenticationModel = this.userTokenCache.get(uToken);
        //为空这远程查询
        if (userAuthenticationModel == null) {
            userAuthenticationModel = remoteUserCenterService.query(uToken);
        }
        //设置当前用户的角色
        setUserAuthentication(userAuthenticationModel);
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
    private void setUserAuthentication(UserAuthenticationModel userAuthenticationModel) {
        if (userAuthenticationModel == null) {
            return;
        }
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String roleName : userAuthenticationModel.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(roleName));
        }
        UserAuthenticationToken userAuthenticationToken = new UserAuthenticationToken(authorities);
        userAuthenticationToken.setUser(userAuthenticationModel);

        //置为当前用户的请求
        SecurityContextHolder.getContext().setAuthentication(userAuthenticationToken);
    }


    /**
     * 拿到 用户令牌
     *
     * @param httpServletRequest
     * @return
     */
    private String getToken(HttpServletRequest httpServletRequest) {

        //cookie
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (UserTokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // head
        String token = httpServletRequest.getHeader(UserTokenName);
        if (token != null) {
            return token;
        }

//        Parameter
        return httpServletRequest.getParameter(UserTokenName);
    }


}
