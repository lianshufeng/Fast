package com.fast.dev.user.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentityModel {

    //身份名称
    private String name;

    //身份的描述
    private String remark;

}
