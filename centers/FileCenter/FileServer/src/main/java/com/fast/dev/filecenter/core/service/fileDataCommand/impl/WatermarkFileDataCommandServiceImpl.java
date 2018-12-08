package com.fast.dev.filecenter.core.service.fileDataCommand.impl;

import com.fast.dev.filecenter.core.service.fileDataCommand.FileDataCommandService;
import com.fast.dev.filecenter.core.util.image.ImageWaterMarkUtil;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class WatermarkFileDataCommandServiceImpl implements FileDataCommandService {

    final static String logoText = "艾艺在线";
    final static Integer degree = -45;
    final static File logIcon =  new File("F://image/input/log.jpg");

    @Override
    public ByteArrayOutputStream command(String param, InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(param.equals("text")){//文字水印
            ImageWaterMarkUtil.markImageByText(logoText,inputStream,byteArrayOutputStream,degree);
        }else{//图片水印
            ImageWaterMarkUtil.markImageByIcon(new FileInputStream(logIcon),inputStream,byteArrayOutputStream,null);
        }
        inputStream.close();
        return byteArrayOutputStream;
    }

    @Override
    public String cmd() {
        return "watermark";
    }
}
