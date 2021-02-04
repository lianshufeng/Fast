package com.fast.dev.filecenter.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * URL的映射查询
 */
@Data
@Document
@NoArgsConstructor
public class UrlMapping extends SuperEntity {
    //自定义URL
    @Indexed
    private String url;

    //用户Id
    private String userId;

    //文件id
    private String fileId;
}
