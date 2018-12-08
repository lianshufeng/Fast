package com.fast.dev.filecenter.core.dao.impl;

import com.fast.dev.filecenter.core.dao.FileDataDao;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@Slf4j
@Repository
public class FileDataDaoImpl implements FileDataDao {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Override
    public GridFSFile findByHash(String hash) {
        Query query = new Query();
        Criteria criteria = Criteria.where("md5").is(hash);
        query.addCriteria(criteria);
        return gridFsTemplate.findOne(query);
    }

    @Override
    public GridFSFile findByFileId(String fileId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(fileId);
        query.addCriteria(criteria);
        return gridFsTemplate.findOne(query);
    }

    @Override
    public void getFileStream(String fileId, OutputStream out) throws IOException {
        InputStream inputStream = getInputStream(fileId);
        StreamUtils.copy(inputStream, out);
        inputStream.close();
    }

    @Override
    public InputStream getInputStream(String fileId) {
        return this.gridFSBucket.openDownloadStream(new ObjectId(fileId));
    }

    @Override
    public GridFSFile uploadFile(InputStream in, String fileName, String contentType) throws IOException {
        ObjectId id = gridFsTemplate.store(in, fileName, contentType);
        in.close();
        return findByFileId(id.toString());
    }

    /**
     * 根据hash删除冗余的文件，同一份文件只能保留一份，保留第一次上传的
     * @param hash
     * @return
     */
    public void delRedundantFile(String hash) {
        Query query = new Query().addCriteria(Criteria.where("md5").is(hash));
        query.with(new Sort(Sort.Direction.ASC, "uploadDate"));
        String firstUploadFileId = gridFsTemplate.findOne(query).getObjectId().toString();
        System.out.println("firstUploadFileId="+firstUploadFileId);
        //删除之前上传的该文件
        Query delQuery = new Query().addCriteria(Criteria.where("_id").ne(firstUploadFileId).and("md5").is(hash));
        gridFsTemplate.delete(delQuery);
    }
}
