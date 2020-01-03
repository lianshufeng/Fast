package com.fast.dev.robot.robotserver.core.factory;

import com.fast.dev.core.helper.DiskCacheStoreHelper;
import com.fast.dev.robot.robotserver.core.factory.model.InputSource;
import com.fast.dev.robot.robotserver.core.model.RobotParm;
import com.fast.dev.robot.robotserver.core.model.RobotValidate;
import com.fast.dev.robot.service.conf.RobotFirewallConfig;
import com.fast.dev.robot.service.type.RobotType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.io.OutputStream;


/**
 * 校验码生成接口
 */
public abstract class RobotFactory {


    @Resource(name = "robotDiskCache")
    protected DiskCacheStoreHelper robotDiskCache;


    protected RobotFirewallConfig robotFirewallConfig;


    /**
     * 校验类型
     *
     * @return
     */
    public abstract RobotType robotType();


    /**
     * 生成验证码
     *
     * @param dataOutputStream
     * @param robotParm
     * @return
     */
    public abstract RobotValidate build(InputSource inputSource, OutputStream dataOutputStream, RobotParm robotParm);


    /**
     * 获取默认的参数
     *
     * @return
     */
    public abstract RobotParm getDefaultBuildInfo();


    /**
     * 获取robot的参数
     *
     * @param robotParm
     * @return
     */
    public RobotParm getRobotParm(RobotParm robotParm) {
        RobotParm ret = getDefaultBuildInfo();
        if (robotParm != null) {
            BeanUtils.copyProperties(robotParm, ret);
        }
        return ret;
    }

    @Autowired
    private void readRobotConfig(ApplicationContext applicationContext) {
        this.robotFirewallConfig = applicationContext.getBean(RobotFirewallConfig.class).get(robotType());
    }


}
