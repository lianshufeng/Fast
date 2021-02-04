package com.fast.dev.auth.center.server.core.model;

import com.fast.dev.auth.client.type.FamilyIdentity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMemberModel {

    //家庭成员的用户id
    @Indexed
    private String uid;

    //昵称
    @Indexed
    private String nickName;

    //手机号码
    @Indexed
    private String phone;

    //家庭身份
    @Indexed
    private FamilyIdentity identity;

}
