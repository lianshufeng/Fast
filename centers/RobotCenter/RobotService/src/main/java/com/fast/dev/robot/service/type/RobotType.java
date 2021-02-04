package com.fast.dev.robot.service.type;


import lombok.Getter;

public enum RobotType {

    /**
     * 旋转的验证码
     */
    Rotation(5 * 1000 * 60, new String[]{"code"});



    RobotType(long timeOut, String[] variableNames) {
        this.timeOut = timeOut;
        this.variableNames = variableNames;
    }

    /**
     * 超时时间
     */
    @Getter
    private long timeOut = 0;


    //变量名
    @Getter
    private String[] variableNames;


}
