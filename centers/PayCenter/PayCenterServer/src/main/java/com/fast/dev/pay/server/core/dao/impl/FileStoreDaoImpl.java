package com.fast.dev.pay.server.core.dao.impl;

import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.pay.server.core.dao.FileStoreDao;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件系统
 */
@Component
public class FileStoreDaoImpl implements FileStoreDao {

    @Autowired
    private GridFsTemplate fsTemplate;

    @Override
    public String write(InputStream inputStream, String fileName) {
        return this.fsTemplate.store(inputStream, fileName).toHexString();
    }

    @Override
    @SneakyThrows
    public boolean read(String fileId, OutputStream outputStream) {
        GridFSFile gridFSFiles = this.fsTemplate.findOne(queryById(fileId));
        if (gridFSFiles == null) {
            return false;
        }
        @Cleanup InputStream inputStream = this.fsTemplate.getResource(gridFSFiles).getInputStream();
        StreamUtils.copy(inputStream, outputStream);
        return true;
    }

    @Override
    public boolean delete(String... fileId) {
        this.fsTemplate.delete(queryById(fileId));
        return true;
    }

    /**
     * 构建查询语句
     *
     * @param fileId
     * @return
     */
    private static Query queryById(String... fileId) {
        return Query.query(EntityObjectUtil.createQueryBatch("_id", fileId));
    }


}
