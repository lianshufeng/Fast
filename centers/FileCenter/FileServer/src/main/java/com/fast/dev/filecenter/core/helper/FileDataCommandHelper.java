package com.fast.dev.filecenter.core.helper;

import com.fast.dev.filecenter.core.service.fileDataCommand.FileDataCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FileDataCommandHelper {

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String,FileDataCommandService>  _cache = new HashMap<>();

    @Autowired
    private void init(){
        for (FileDataCommandService value : this.applicationContext.getBeansOfType(FileDataCommandService.class).values()) {
            log.info(value.cmd());
            _cache.put(value.cmd(),value);
        }
    }


    public ByteArrayOutputStream execute(String cmd ,String param, InputStream inputStream) throws IOException {
        FileDataCommandService fileDataCommandService =  _cache.get(cmd);
        Assert.notNull(fileDataCommandService,"命令错误");
        return fileDataCommandService.command(param,inputStream);
    }
}
