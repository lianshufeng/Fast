package com.fast.dev.auth.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserQueryFamilyModel {
    //家庭身份
    private Set<UserQueryFamilyMember> member;
}
