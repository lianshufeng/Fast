package com.fast.dev.robot.robotserver.core.service;

import com.fast.dev.core.helper.DiskCacheStoreHelper;
import com.fast.dev.core.util.spring.SpringELUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.robot.robotserver.core.dao.ImageTemplateDao;
import com.fast.dev.robot.robotserver.core.dao.RobotRecordDao;
import com.fast.dev.robot.robotserver.core.domain.RobotRecord;
import com.fast.dev.robot.robotserver.core.factory.RobotFirewallHelper;
import com.fast.dev.robot.robotserver.core.factory.model.InputSource;
import com.fast.dev.robot.robotserver.core.model.RobotBuild;
import com.fast.dev.robot.robotserver.core.model.RobotGetParameter;
import com.fast.dev.robot.robotserver.core.model.RobotParm;
import com.fast.dev.robot.robotserver.core.model.RobotValidate;
import com.fast.dev.robot.service.conf.RobotFirewallConfig;
import com.fast.dev.robot.service.model.RobotValidateParameter;
import com.fast.dev.robot.service.model.RobotValidateRet;
import com.fast.dev.robot.service.type.ValidateType;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageTemplateService {

    @Autowired
    private ImageTemplateDao imageTemplateDao;

    @Autowired
    private RobotRecordDao robotRecordDao;


    @Resource(name = "dbDiskCache")
    private DiskCacheStoreHelper diskCacheStoreHelper;


    @Autowired
    private RobotFirewallHelper robotFirewallHelper;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private RobotFirewallConfig robotFirewallConfig;


    /**
     * 构建验证码
     */
    @SneakyThrows
    public RobotBuild build(RobotGetParameter robotParameter, OutputStream outputStream) {

        String fileId = this.imageTemplateDao.getRandomFileId();
        Assert.hasText(fileId, "文件id不能为空");

        //判断是否本地有缓存,没有缓存则存到本地
        if (!this.diskCacheStoreHelper.exists(fileId)) {
            @Cleanup InputStream inputStream = this.imageTemplateDao.getFile(fileId);
            Assert.notNull(inputStream, "文件流为空");
            this.diskCacheStoreHelper.store(fileId, inputStream);
        }


        //从本地拿出流
        @Cleanup InputStream inputStream = this.diskCacheStoreHelper.get(fileId);


        //生成验证码
        RobotParm robotParm = new RobotParm(robotParameter.getWidth(), robotParameter.getHeight());

        //验证码
        RobotValidate robotValidate = this.robotFirewallHelper.get(robotParameter.getRobotType()).build(new InputSource(fileId, inputStream), outputStream, robotParm);

        //配置信息
        RobotFirewallConfig robotFirewallConfig = this.robotFirewallConfig.get(robotParameter.getRobotType());


        //UUID
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        RobotRecord robotRecord = new RobotRecord();
        BeanUtils.copyProperties(robotValidate, robotRecord);

        //验证码令牌
        robotRecord.setToken(token);
        //业务名
        robotRecord.setServiceName(robotParameter.getServiceName());
        //令牌过期时间
        robotRecord.setExpireTime(this.dbHelper.getTime() + robotFirewallConfig.getTokenTimeOut());

        this.robotRecordDao.save(robotRecord);

        RobotBuild robotBuild = new RobotBuild();
        BeanUtils.copyProperties(robotRecord, robotBuild);

        return robotBuild;

    }


    @SneakyThrows
    public RobotValidateRet validate(HttpServletRequest request, RobotValidateParameter robotValidateParameter) {

        RobotValidateRet robotValidateRet = new RobotValidateRet();

        //查询db
        RobotRecord robotRecord = this.robotRecordDao.getRobotRecord(robotValidateParameter.getToken());
        if (robotRecord == null) {
            robotValidateRet.setValidateType(ValidateType.NotExist);
            return robotValidateRet;
        }

        //构建表达式map
        Map<String, Object> val = new HashMap<>();
        //动态变量
        for (String it : robotRecord.getRobotType().getVariableNames()) {
            val.put(it, request.getParameter(it));
        }


        //拷贝数据库中查询的参数，用于返回
        BeanUtils.copyProperties(robotRecord, robotValidateRet, "expression");

        //进行校验
        boolean success = SpringELUtil.parseExpression(val, robotRecord.getExpression());
        if (success) {
            robotValidateRet.setValidateType(ValidateType.Done);
        } else {
            robotValidateRet.setValidateType(ValidateType.Error);
        }
        return robotValidateRet;
    }


}
