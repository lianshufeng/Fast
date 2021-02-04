package com.fast.dev.auth.security.model;

import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.model.FamilyAuthUser;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class UserParmModel extends UserTokenModel {

    /**
     * 企业id
     */
    @Getter
    @Setter
    private String enterPriseId;

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
     * 当前用户
     */
    @Delegate(types = FamilyAuthUser.class)
    private FamilyAuthUser authUser = new FamilyAuthUser();


    /**
     * 归属的企业
     */
    @Getter
    @Setter
    private List<EnterpriseModel> affiliatedEnterprises;


    /**
     * 创建时间(缓存)
     */
    @Getter
    @Setter
    private long cacheTime;


}
