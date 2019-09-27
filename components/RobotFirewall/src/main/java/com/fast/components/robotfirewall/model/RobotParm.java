package com.fast.components.robotfirewall.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机器校验码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RobotParm {


    /**
     * 宽度
     */
    private int width;

    /**
     * 高度
     */
    private int height;


}
