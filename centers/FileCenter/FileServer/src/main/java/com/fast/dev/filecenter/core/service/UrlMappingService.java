package com.fast.dev.filecenter.core.service;

import com.fast.dev.filecenter.core.dao.UrlCacheDao;
import com.fast.dev.filecenter.core.dao.UrlMappingDao;
import com.fast.dev.filecenter.core.domain.UrlMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlMappingService {

    @Autowired
    private UrlMappingDao urlMappingDao;
    @Autowired
    private UrlCacheDao urlCacheDao;


    public String setUrlMapping(String userId,String fileId,String url){
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setUserId(userId);
        urlMapping.setFileId(fileId);
        urlMapping.setUrl(url);
        urlMappingDao.save(urlMapping);
        return urlMapping.getId();
    }

    public boolean existUrl(String userId,String url){
        return urlMappingDao.existsByUserIdAndUrl(userId,url);
    }

    public void updateUrlMapping(String userId,String fileId,String url){
        UrlMapping urlMapping = urlMappingDao.findByUserIdAndUrl(userId, url);
        if(!urlMapping.getFileId().equals(fileId)){
            urlMappingDao.updateUrlMapping(userId,fileId,url);
            //删除缓存表
            urlCacheDao.deleteByUserIdAndUrlMappingId(userId,urlMapping.getId());
        }
    }

    public void deleteUrlMapping(String userId,String url){
        UrlMapping urlMapping = urlMappingDao.findByUserIdAndUrl(userId, url);
        //删除URL映射
        urlMappingDao.delete(urlMapping);
        //删除缓存
        urlCacheDao.deleteByUserIdAndUrlMappingId(userId,urlMapping.getId());
    }

    public String findSourceFileId(String userId, String url){
        UrlMapping urlMapping = urlMappingDao.findByUserIdAndUrl(userId, url);
        if(null!=urlMapping){
            return urlMapping.getFileId();
        }
        return null;
    }


}
