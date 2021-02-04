package com.fast.dev.auth.client.register;

import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.model.RoleUserModel;

import java.util.Collection;

/**
 * 企业注册事件,实现这个接口请加入注解 @Component , @Scope("prototype")
 */
public interface EnterpriseInitEvent {

    /**
     * 条件，注意：仅满足条件的才可以进行企业初始化
     *
     * @param enterpriseModel
     * @return
     */
    boolean condition(EnterpriseModel enterpriseModel);


    /**
     * 企业创建
     *
     * @param enterpriseModel
     * @return
     */
    Collection<RoleUserModel> onCreate(EnterpriseModel enterpriseModel, String ownerUid);


}
