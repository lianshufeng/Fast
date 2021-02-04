package com.fast.dev.auth.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FamilyModel {

    private String id;

    //企业id
    private String epId;

    //家庭身份
    private Set<FamilyMember> member;

    //拥有者
    private String owner;


    /**
     * 获取成员的uid
     *
     * @return
     */
    public Set<String> getMemberUid() {
        if (this.member == null) {
            return null;
        }
        return this.member.stream().map((it) -> {
            return it.getUid();
        }).collect(Collectors.toSet());
    }

}
