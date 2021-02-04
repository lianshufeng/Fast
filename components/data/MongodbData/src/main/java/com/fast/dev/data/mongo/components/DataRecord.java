package com.fast.dev.data.mongo.components;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.*;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 数据记录集,可用于还原,适合完成业务补偿部分，建议配合事务使用
 */
@Component
@NoArgsConstructor
public class DataRecord {

    @Getter
    @Setter
    @Autowired
    private MongoTemplate mongoTemplate;


    private interface ProxyMethod {
        boolean add(RevertData data);

        boolean addAll(Collection<? extends RevertData> datas);

        void clear();

        int size();
    }


    @Delegate(types = ProxyMethod.class)
    private Collection<RevertData> list = new ArrayList<>();


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevertData {
        private Class<? extends SuperEntity> entityClass;
        private Query query;
        private Update update;
    }

    public Collection<RevertData> list() {
        return this.list;
    }


    /**
     * 恢复数据
     */
    public void revert() {
        for (RevertData data : this.list) {
            this.mongoTemplate.updateFirst(data.query, data.update, data.entityClass);
        }
    }

}
