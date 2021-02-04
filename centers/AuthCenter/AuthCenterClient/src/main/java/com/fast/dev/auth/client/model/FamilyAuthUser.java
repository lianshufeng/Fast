package com.fast.dev.auth.client.model;

import com.fast.dev.auth.client.bean.AuthUser;
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
public class FamilyAuthUser extends AuthUser {

    //家庭模型
    private FamilyModel family;

}
