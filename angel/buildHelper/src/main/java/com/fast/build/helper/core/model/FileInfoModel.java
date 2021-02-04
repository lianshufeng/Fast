package com.fast.build.helper.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfoModel {
    //下载地址
    private String url;
    //尺寸
    private String size;
    //文件
    private File file;

}
