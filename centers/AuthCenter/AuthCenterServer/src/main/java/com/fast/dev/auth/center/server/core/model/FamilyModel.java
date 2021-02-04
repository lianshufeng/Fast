package com.fast.dev.auth.center.server.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 家庭组模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyModel {


    /**
     * 成员
     */
    private Set<FamilyMemberModel> member;


}
