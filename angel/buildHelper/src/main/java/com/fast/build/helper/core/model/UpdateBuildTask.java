package com.fast.build.helper.core.model;

import com.fast.build.helper.core.type.UpdateBuildType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBuildTask {

    /**
     * 创建时间
     */
    private long createTime;


}
