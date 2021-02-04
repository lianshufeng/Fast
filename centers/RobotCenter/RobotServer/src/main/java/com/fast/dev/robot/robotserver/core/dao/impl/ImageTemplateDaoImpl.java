package com.fast.dev.robot.robotserver.core.dao.impl;

import com.fast.dev.robot.robotserver.core.dao.ImageTemplateDao;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ImageTemplateDaoImpl implements ImageTemplateDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    //文件列表
    private final static String FsFiles = "fs.files";


    @Autowired
    @SneakyThrows
    @Transactional
    public void init() {
        String uuid = UUID.randomUUID().toString();
        Query query = new Query().addCriteria(Criteria.where("filename").is(uuid));
        //不存则随机写一个名字进去
        @Cleanup ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[0]);
        this.gridFsTemplate.store(byteArrayInputStream, uuid);

        //删除
        this.gridFsTemplate.delete(query);

    }


    /**
     * 获取随机的文件名
     *
     * @return
     */
    @Override
    public String getRandomFileId() {
        long count = this.mongoTemplate.count(new Query(), FsFiles);
        if (count == 0) {
            return null;
        }

        long index = RandomUtils.nextLong(0, count - 1);
        Query query = new Query();
        query.skip(index);
        query.limit(1);
        List<Map> result = this.mongoTemplate.find(query, Map.class, FsFiles);
        if (result == null || result.size() <= 0) {
            return null;
        }
        return String.valueOf(result.get(0).get("_id"));
    }

    @SneakyThrows
    @Override
    public InputStream getFile(String fileId) {
        GridFSFile file = this.gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is(fileId)));
        if (file != null) {
            return gridFsTemplate.getResource(file).getInputStream();
        }
        return null;
    }


}
