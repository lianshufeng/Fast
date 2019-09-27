package com.fast.dev.robot.robotserver.core.dao;

import java.io.InputStream;

/**
 * 镜像末班
 */
public interface ImageTemplateDao {


    /**
     * 随机取一个文件id
     * @return
     */
    String getRandomFileId();


    /**
     *
     * 取一个文件流
     * @param fileId
     * @return
     */
    InputStream getFile(String fileId);


}
