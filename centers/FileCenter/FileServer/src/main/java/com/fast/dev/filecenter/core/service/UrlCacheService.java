package com.fast.dev.filecenter.core.service;

import com.fast.dev.filecenter.core.dao.FileDataDao;
import com.fast.dev.filecenter.core.dao.UrlCacheDao;
import com.fast.dev.filecenter.core.dao.UrlMappingDao;
import com.fast.dev.filecenter.core.domain.UrlCache;
import com.fast.dev.filecenter.core.domain.UrlMapping;
import com.fast.dev.filecenter.core.helper.FileDataCommandHelper;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@Slf4j
@Service
public class UrlCacheService {
    @Autowired
    private UrlCacheDao urlCacheDao;

    @Autowired
    private FileDataDao fileDataDao;

    @Autowired
    private UrlMappingDao urlMappingDao;
    @Autowired
    private FileDataCommandHelper fileDataCommandHelper;


    public void setUrlCache(String userId, String fileId, String url, String urlMappingId) {
        UrlCache urlCache = new UrlCache();
        urlCache.setUrl(url);
        urlCache.setFileId(fileId);
        urlCache.setUserId(userId);
        urlCache.setUrlMappingId(urlMappingId);
        urlCacheDao.save(urlCache);
    }

    public String findFileByUrl(String func, String userId, String url) throws IOException {
        String cacheUrl = func + userId + "/" + url;
        System.out.println(cacheUrl);
        UrlCache urlCache = urlCacheDao.findByUserIdAndUrl(userId, cacheUrl);
        if (null != urlCache) {
            return urlCache.getFileId();
        }
        //源文件
        UrlMapping urlMapping = urlMappingDao.findByUserIdAndUrl(userId, url);
        Assert.notNull(urlMapping, "文件不存在，请重试或联系管理员");

        //处理和生成文件
        String fileId = makeFile(urlMapping.getFileId(), func);

        //保存URL缓存
        setUrlCache(userId,fileId,cacheUrl,urlMapping.getId());
        return fileId;
    }

    /**
     * 处理和制作文件
     */
    private String makeFile(String sourceFileId, String func) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        String[] strArr = func.split("/");
        for(int i=0;i<strArr.length;i++){
            String[] s = strArr[i].split("_", 2);
            map.put(s[0],s[1]);
        }
        //源文件流
        InputStream inputStream = fileDataDao.getInputStream(sourceFileId);
        for (String cmd : map.keySet()) {
            ByteArrayOutputStream byteArrayOutputStream = fileDataCommandHelper.execute(cmd,map.get(cmd),inputStream);
            inputStream.close();
            //输出流转输入流
            byte[] bin = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            inputStream = new ByteArrayInputStream(bin);
        }
        // 保存处理后的文件
        GridFSFile fsFile = fileDataDao.uploadFile(inputStream, "", "image/jpeg");
        inputStream.close();
        String fileId = fsFile.getObjectId().toString();
        return fileId;
    }
}
