package com.fast.dev.filecenter.core.dao;

import com.mongodb.client.gridfs.model.GridFSFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件接口
 */
public interface FileDataDao {

    /**
     * 上传文件
     * @param in
     * @param fileName
     * @param contentType
     * @return
     * @throws IOException
     */
    GridFSFile uploadFile(InputStream in,String fileName,String contentType) throws IOException;


    /**
     *  查询
     * @param hash
     */
    GridFSFile findByHash(String hash);

    /**
     *  查询
     * @param fileId
     */
    GridFSFile findByFileId(String fileId);



    void getFileStream(String fileId,OutputStream out) throws IOException;


    /**
     * 读取数据库流
     * @param fileId
     * @return
     */
    InputStream getInputStream(String fileId) throws IOException;


    /**
     * 根据hash删除重复文件，同一份文件只能保留一份（保留第一上传的）
     * @param hash
     */
    void delRedundantFile(String hash);
}
