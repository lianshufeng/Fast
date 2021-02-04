package com.fast.dev.auth.security.model;

import com.fast.dev.auth.client.bean.AuthUser;
import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.model.FamilyAuthUser;
import com.fast.dev.auth.client.model.UserModel;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户令牌模型
 */
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserAutTokenCacheItem extends UserTokenModel {


    /**
     * 用户名
     */
    @Getter
    @Setter
    private String userName;


    /**
     * 电话号码
     */
    @Getter
    @Setter
    private String phone;




    /**
     * 归属的企业
     */
    @Getter
    @Setter
    private List<EnterpriseModel> affiliatedEnterprises;


    /**
     * 用户的独立权限
     */
    @Getter
    @Setter
    private Set<String> userAuths;


    /**
     * 企业缓存
     */
    private Map<String, FamilyAuthUser> enterPriseCache = new ConcurrentHashMap<>();


    /**
     * 缓存时间
     */
    @Getter
    private long cacheTime = System.currentTimeMillis();


    /**
     * 获取一个企业对应的权限
     *
     * @param epId
     * @return
     */
    public FamilyAuthUser getEnterPriseAuthUser(String epId) {
        return enterPriseCache.get(epId);
    }

    /***
     * 是否存在企业
     * @param epId
     * @return
     */
    public boolean containsEnterPrise(String epId) {
        return enterPriseCache.containsKey(epId);
    }


    /**
     * 设置企业
     *
     * @param epId
     */
    public void putEnterPrise(String epId, FamilyAuthUser authUser) {
        this.enterPriseCache.put(epId, authUser);
    }


    /**
     * 删除一个企业缓存
     *
     * @param epId
     * @return
     */
    public AuthUser removeEnterPrise(String epId) {
        return this.enterPriseCache.remove(epId);
    }

}
