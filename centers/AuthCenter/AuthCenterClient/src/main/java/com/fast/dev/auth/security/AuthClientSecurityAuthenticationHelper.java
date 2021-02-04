package com.fast.dev.auth.security;

import com.fast.dev.auth.client.bean.AuthUser;
import com.fast.dev.auth.client.model.FamilyAuthUser;
import com.fast.dev.auth.client.model.FamilyModel;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.service.AuthService;
import com.fast.dev.auth.client.service.FamilyService;
import com.fast.dev.auth.client.service.UserService;
import com.fast.dev.auth.client.type.ResultState;
import com.fast.dev.auth.security.cache.AuthClientUserTokenCache;
import com.fast.dev.auth.security.model.UserAutTokenCacheItem;
import com.fast.dev.auth.security.model.UserParmModel;
import com.fast.dev.core.util.net.IPUtil;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.security.helper.SecurityAuthenticationHelper;
import com.fast.dev.ucenter.security.model.UserAuthenticationToken;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户安全助手
 */
@Slf4j
@Component
public class AuthClientSecurityAuthenticationHelper extends SecurityAuthenticationHelper {

    //令牌的关键词
    private static final String[] UserTokenName = new String[]{"_uToken", "uToken"};

    //企业id
    private static final String[] EnterPriseName = new String[]{"enterPriseId", "epId", "enterpriseId"};


    @Autowired
    private RemoteUserCenterService remoteUserCenterService;

    @Autowired
    private AuthClientUserTokenCache authClientUserTokenCache;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private FamilyService familyService;

    /**
     * 权限拦截
     *
     * @param httpServletRequest
     */
    public void authenticate(final HttpServletRequest httpServletRequest) {
        //当前线程初始化
        this.release();

        //获取用户令牌
        String uToken = getUserToken(httpServletRequest);

        //获取企业令牌
        String epId = getEnterPriseId(httpServletRequest);

        log.debug("[uToken] : {}  -> url : {} -> UA : {} -> ip : {}", uToken, httpServletRequest.getRequestURL(), httpServletRequest.getHeader("user-agent"), IPUtil.getRemoteIp(httpServletRequest));


        //设置登录用户
        setCurrentUserParm(httpServletRequest, uToken, epId);

    }


    /**
     * 设置当前用户
     *
     * @param uToken
     * @param epId
     */
    private void setCurrentUserParm(HttpServletRequest httpServletRequest, String uToken, String epId) {
        //缓存用户信息
        UserAutTokenCacheItem userAutTokenCacheItem = cacheUserToken(uToken);
        if (userAutTokenCacheItem == null) {
            return;
        }

        //缓存企业信息
        this.cacheEnterPrise(userAutTokenCacheItem, epId);


        //设置spring的权限
        this.setUserAuthentication(httpServletRequest, this.toCurrentUserParm(userAutTokenCacheItem, epId));

    }

    /**
     * 缓存企业信息
     */
    private void cacheEnterPrise(UserAutTokenCacheItem item, String epId) {
        //没有企业id
        if (!StringUtils.hasText(epId)) {
            return;
        }

        //有缓存则直接不缓存企业信息
        if (item.containsEnterPrise(epId)) {
            return;
        }

        //访问这个用户在企业中的权限
        this.authService.queryUserEnterPrise(item.getUid(), epId).optionalContent().ifPresent((authUser) -> {
            //拷贝基础权限
            FamilyAuthUser familyAuthUser = new FamilyAuthUser();
            BeanUtils.copyProperties(authUser, familyAuthUser);
            //设置家庭信息
            familyAuthUser.setFamily(this.getFamily(epId, item.getUid()));
            //缓存数据
            item.putEnterPrise(epId, familyAuthUser);
        });
    }


    /**
     * 读取并缓存
     *
     * @param uToken
     * @return
     */
    private UserAutTokenCacheItem cacheUserToken(String uToken) {
        if (!StringUtils.hasText(uToken)) {
            return null;
        }
        //通过缓存读取
        UserAutTokenCacheItem userAutTokenCacheItem = this.authClientUserTokenCache.get(uToken);
        if (userAutTokenCacheItem != null) {
            return userAutTokenCacheItem;
        }
        //网络查询
        UserTokenModel userTokenModel = this.remoteUserCenterService.queryByUserToken(uToken);
        if (userTokenModel == null || userTokenModel.getUid() == null) {
            return null;
        }

        //查询用户基础信息
        BaseUserModel baseUserModel = this.remoteUserCenterService.queryUserId(userTokenModel.getUid());
        if (baseUserModel == null || baseUserModel.getId() == null) {
            return null;
        }


        //实例化并入库
        final UserAutTokenCacheItem newUserAutTokenCacheItem = new UserAutTokenCacheItem();
        this.authClientUserTokenCache.put(uToken, newUserAutTokenCacheItem);

        //用户令牌
        BeanUtils.copyProperties(userTokenModel, newUserAutTokenCacheItem);

        //用户基本信息
        BeanUtils.copyProperties(baseUserModel, newUserAutTokenCacheItem);

        //设置用户的所有企业
        userService.getAffiliatedEnterprises(userTokenModel.getUid()).optionalContent().ifPresent((it) -> {
            newUserAutTokenCacheItem.setAffiliatedEnterprises(it);
        });

        //设置用户的基础权限
        this.authService.queryUserAuth(userTokenModel.getUid()).optionalContent().ifPresent((userAuths) -> {
            newUserAutTokenCacheItem.setUserAuths(userAuths);
        });

        return newUserAutTokenCacheItem;
    }


    /**
     * 设置当前用户的权限
     */
    private void setUserAuthentication(HttpServletRequest httpServletRequest, UserParmModel parmModel) {
        if (parmModel == null) {
            return;
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        //用户的基本权限
        if (parmModel.getAuth() != null) {
            authorities.addAll(parmModel.getAuth().stream().filter((it) -> {
                return StringUtils.hasText(it);
            }).map((it) -> {
                return new SimpleGrantedAuthority(it);
            }).collect(Collectors.toSet()));
        }

        //用户令牌
        UserAuthenticationToken userAuthenticationToken = new UserAuthenticationToken(authorities);
        userAuthenticationToken.setAuthenticated(true);
        userAuthenticationToken.setDetails(parmModel);


        //置为当前用户的请求
        SecurityContextHolder.getContext().setAuthentication(userAuthenticationToken);
    }


    private UserParmModel toCurrentUserParm(UserAutTokenCacheItem item, String epId) {
        UserParmModel parm = new UserParmModel();
        BeanUtils.copyProperties(item, parm);
        //设置用户默认权限
        parm.setAuth(item.getUserAuths() == null ? new HashSet<>() : new HashSet<>(item.getUserAuths()));

        //设置企业中的权限
        if (epId == null) {
            return parm;
        }

        //取出企业信息
        FamilyAuthUser enterPriseUser = item.getEnterPriseAuthUser(epId);
        if (enterPriseUser == null) {
            return parm;
        }
        BeanUtils.copyProperties(enterPriseUser, parm, "auth");

        //设置企业id
        parm.setEnterPriseId(epId);

        //合并权限: 默认+企业
        parm.getAuth().addAll(enterPriseUser.getAuth());

        return parm;
    }


    /**
     * 拿到 用户令牌
     *
     * @param httpServletRequest
     * @return
     */
    public String getUserToken(final HttpServletRequest httpServletRequest) {
        return getParameter(httpServletRequest, UserTokenName);
    }


    /**
     * 获取企业id
     *
     * @param httpServletRequest
     * @return
     */
    public String getEnterPriseId(final HttpServletRequest httpServletRequest) {
        return getParameter(httpServletRequest, EnterPriseName);
    }


    public String getParameter(final HttpServletRequest httpServletRequest, String[] names) {
        for (String tokenName : names) {
            StringBuffer uTokenBuffer = new StringBuffer();
            if (getFromCookie(httpServletRequest, tokenName, uTokenBuffer)) {
                return uTokenBuffer.toString();
            }
            if (getFromHead(httpServletRequest, tokenName, uTokenBuffer)) {
                return uTokenBuffer.toString();
            }
            if (getFromParameter(httpServletRequest, tokenName, uTokenBuffer)) {
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
    private static boolean getFromCookie(final HttpServletRequest httpServletRequest, final String tokenName, final StringBuffer uTokenBuffer) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    return checkParameter(cookie.getValue(), uTokenBuffer);
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
    private static boolean getFromHead(final HttpServletRequest httpServletRequest, final String tokenName, final StringBuffer uTokenBuffer) {
        return checkParameter(httpServletRequest.getHeader(tokenName), uTokenBuffer);
    }


    /**
     * 通过参数获取用户令牌
     *
     * @param httpServletRequest
     * @param tokenName
     * @return
     */
    private static boolean getFromParameter(final HttpServletRequest httpServletRequest, final String tokenName, final StringBuffer uTokenBuffer) {
        return checkParameter(httpServletRequest.getParameter(tokenName), uTokenBuffer);
    }

    /**
     * 检查用户令牌,校验成功返回令牌，失败返回null
     *
     * @param parm
     * @return
     */
    private static boolean checkParameter(final String parm, final StringBuffer uTokenBuffer) {
        if (StringUtils.hasText(parm)) {
            uTokenBuffer.append(parm);
            return true;
        }
        return false;
    }


    /**
     * 取出家庭模型
     *
     * @return
     */
    public FamilyModel getFamily(String epId, String uid) {
        if (!StringUtils.hasText(epId)) {
            return null;
        }
        //查询当前用户的家庭
        ResultContent<List<FamilyModel>> familyModels = this.familyService.findByMemberUid(epId, new String[]{uid});
        if (familyModels == null || familyModels.getState() != ResultState.Success || familyModels.getContent() == null || familyModels.getContent().size() == 0) {
            return null;
        }
        return familyModels.getContent().get(0);
    }


}
