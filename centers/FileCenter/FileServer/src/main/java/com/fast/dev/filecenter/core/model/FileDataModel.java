package com.fast.dev.filecenter.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileDataModel {

    /**
     * 文件id
     */
    private String id;


    /**
     * 文件hash
     */
    private String hash;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文本类型
     */
    private String contentType;

    /**
     * 数据创建时间
     */
    private Long createTime;




}
