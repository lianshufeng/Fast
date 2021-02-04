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
public class FileInfo {
    //文件名
    private String name;
    //更新时间
    private long updateTime;
    //文件长度
    private long length;
    //文件
    private File file;

}
