package com.fast.dev.ucenter.service.domain;

import com.fast.dev.component.mongodb.domain.SuperEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserLog extends SuperEntity {

    //名称
    @Indexed
    private String name;


    private long time;

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }


}
