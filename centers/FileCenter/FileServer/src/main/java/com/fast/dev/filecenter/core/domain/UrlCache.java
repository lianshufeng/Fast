package com.fast.dev.filecenter.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class UrlCache extends SuperEntity {
    private String url;
    private String fileId;
    private String userId;
    //源文件
    private String urlMappingId;
}
