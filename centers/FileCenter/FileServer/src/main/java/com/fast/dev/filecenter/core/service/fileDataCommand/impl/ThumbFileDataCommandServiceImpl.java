package com.fast.dev.filecenter.core.service.fileDataCommand.impl;

import com.fast.dev.filecenter.core.dao.FileDataDao;
import com.fast.dev.filecenter.core.dao.UrlCacheDao;
import com.fast.dev.filecenter.core.dao.UrlMappingDao;
import com.fast.dev.filecenter.core.service.UrlCacheService;
import com.fast.dev.filecenter.core.service.fileDataCommand.FileDataCommandService;
import com.fast.dev.filecenter.core.util.image.ImageScaleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
public class ThumbFileDataCommandServiceImpl implements FileDataCommandService {
    private String func = "thumb";

    @Autowired
    private UrlMappingDao urlMappingDao;

    @Autowired
    private UrlCacheService urlCacheService;

    @Autowired
    private UrlCacheDao urlCacheDao;

    @Autowired
    private FileDataDao fileDataDao;

    @Override
    public ByteArrayOutputStream command(String param,InputStream inputStream) throws IOException {
        //处理文件(缩放)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int width = Integer.valueOf(param.split("_")[0]);
        int height = Integer.valueOf(param.split("_")[1]);
        ImageScaleUtil.scale(inputStream, byteArrayOutputStream, width, height,"");
        inputStream.close();
        return byteArrayOutputStream;
    }

    @Override
    public String cmd() {
        return func;
    }


}
