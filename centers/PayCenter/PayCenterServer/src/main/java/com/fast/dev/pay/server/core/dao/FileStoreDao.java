package com.fast.dev.pay.server.core.dao;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileStoreDao {

    /**
     * 写入流文件
     *
     * @param inputStream
     * @return
     */
    String write(InputStream inputStream, String fileName);


    /**
     * 读取流文件
     *
     * @param fileId
     * @param outputStream
     */
    boolean read(String fileId, OutputStream outputStream);


    /**
     * 删除文件
     *
     * @param fileId
     */
    boolean delete(String... fileId);

}
