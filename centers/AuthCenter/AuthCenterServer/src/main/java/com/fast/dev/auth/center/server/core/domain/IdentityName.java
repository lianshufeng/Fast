package com.fast.dev.auth.center.server.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class IdentityName extends SuperEntity  {

    /**
     * 权限名
     */
    @Indexed(unique = true)
    private String name;


    /**
     * 备注
     */
    private String remark;
}
