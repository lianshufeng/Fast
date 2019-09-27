package com.fast.dev.robot.robotserver.core.service;

import com.fast.dev.core.helper.DiskCacheStoreHelper;
import com.fast.dev.robot.robotserver.core.dao.ImageTemplateDao;
import com.fast.dev.robot.robotserver.core.factory.RobotFirewallHelper;
import com.fast.dev.robot.robotserver.core.model.RobotParameter;
import com.fast.dev.robot.robotserver.core.model.RobotValidate;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class ImageTemplateService {

    @Autowired
    private ImageTemplateDao imageTemplateDao;


    @Resource(name = "imageDiskCache")
    private DiskCacheStoreHelper diskCacheStoreHelper;


    @Autowired
    private RobotFirewallHelper robotFirewallHelper;


    /**
     * 构建验证码
     */
    @SneakyThrows
    public RobotValidate build(RobotParameter robotParameter, OutputStream outputStream) {

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
        RobotValidate robotValidate = this.robotFirewallHelper.get(robotParameter.getRobotType()).build(inputStream, outputStream, null);


        return robotValidate;

    }


}
