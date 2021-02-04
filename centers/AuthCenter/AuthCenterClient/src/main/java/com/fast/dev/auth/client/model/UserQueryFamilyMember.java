package com.fast.dev.auth.client.model;

import com.fast.dev.auth.client.type.FamilyIdentity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserQueryFamilyMember {

    //家庭成员的用户id
    private String uid;

    //昵称
    private String nickName;

    //家庭身份
    private FamilyIdentity identity;

    //手机号码
    private String phone;

}
