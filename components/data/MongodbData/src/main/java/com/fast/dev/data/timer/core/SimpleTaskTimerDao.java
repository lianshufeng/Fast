package com.fast.dev.data.timer.core;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.data.mongo.helper.MongoQueryLanguageHelper;
import com.fast.dev.data.mongo.model.QueryModel;
import com.fast.dev.data.timer.conf.TaskTimerConf;
import com.fast.dev.data.timer.domain.SimpleTaskTimerTable;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务调度器的实现
 */

@Component
@Scope("prototype")
public class SimpleTaskTimerDao {

    @Setter
    @Getter
    private Class<? extends SuperEntity> taskTimerTableCls;

    @Autowired
    private TaskTimerConf taskTimerConf;


    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private MongoQueryLanguageHelper mongoQueryLanguageHelper;


    /**
     * 查询所有的任务
     *
     * @return
     */
    public List<SimpleTaskTimerTable> list() {
        //查询条件
        QueryModel queryModel = new QueryModel()
                .setMql("{}")
                .setFields(new HashSet<>() {{
                    add("_id");
                    add("cron");
                }});
        //分页条件
        Pageable pageable = PageRequest.of(0, this.taskTimerConf.getMaxLoadDBCount(), Sort.by(Sort.Direction.ASC, "updateTime"));
        Page<Document> entities = this.mongoQueryLanguageHelper.queryByMql(queryModel, pageable, this.mongoTemplate.getCollectionName(taskTimerTableCls));
        return entities.getContent().parallelStream().map((it) -> {
            SimpleTaskTimerTable simpleTaskTimerTable = new SimpleTaskTimerTable();
            simpleTaskTimerTable.setId(it.getObjectId("_id").toString());
            simpleTaskTimerTable.setCron(it.getString("cron"));
            return simpleTaskTimerTable;
        }).collect(Collectors.toList());
    }


    /**
     * 通过id找到这条数据
     *
     * @param id
     * @return
     */
    public <T> T findById(String id) {
        return (T) this.mongoTemplate.findById(id, this.taskTimerTableCls);
    }

    /**
     * 查询并转换为任务调度器对象
     *
     * @param id
     * @return
     */
    public SimpleTaskTimerTable findByIdToSimpleTaskTimerTable(String id) {
        Map map = this.mongoTemplate.findById(id, Map.class, this.mongoTemplate.getCollectionName(this.taskTimerTableCls));
        if (map == null) {
            return null;
        }
        SimpleTaskTimerTable simpleTaskTimerTable = new SimpleTaskTimerTable();
        simpleTaskTimerTable.setId(id);
        Optional.ofNullable(map.get("cron")).ifPresent((it) -> {
            simpleTaskTimerTable.setCron(String.valueOf(it));
        });
        return simpleTaskTimerTable;
    }

}
