package com.fast.dev.data.mongo.data;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.base.data.DataHelper;
import com.fast.dev.data.base.data.annotations.DataRule;
import com.fast.dev.data.base.data.impl.DataHelperImpl;
import com.fast.dev.data.base.data.type.UpdateType;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.mongodb.DBRef;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DataHelperMongo extends DataHelperImpl implements DataHelper {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    /**
     * 通过id查询该数据
     *
     * @param entityClasses
     * @param id
     * @return
     */
    @Override
    public Object getSource(Class<? extends AbstractPersistable> entityClasses, Object id) {
        return this.mongoTemplate.findById(id, entityClasses);
    }

    @Override
    public String[] getTargerDataRuleIds(DataRule dataRule, Map<String, Object> varMap) {
        List<String> ids = new ArrayList<>();
        String collectionName = this.dbHelper.getCollectionName(dataRule.targetEntity());
        //过滤条件
        String filter = dataRule.targetQuery();
        for (Map.Entry<String, Object> entry : varMap.entrySet()) {
            String key = ":" + entry.getKey();
            //需要替换
            if (filter.indexOf(key) > -1) {
                //需要更新的值
                String value = entry.getValue() instanceof String ? String.valueOf(entry.getValue()) : JsonUtil.toJson(entry.getValue());

                //进行数据替换
                filter = filter.replaceAll(key, String.valueOf(entry.getValue()));
            }
        }

        //数据
        for (Document document : this.mongoTemplate.getCollection(collectionName).find(Document.parse(filter))) {
            ids.add(String.valueOf(document.get("_id")));
        }

        return ids.toArray(new String[ids.size()]);
    }

    @Override
    public void updateData(Object fieldObject, String[] targetEntityIds, DataRule dataRule) {
        if (targetEntityIds == null || targetEntityIds.length == 0) {
            return;
        }
        //构建查询条件
        Query query = new Query(EntityObjectUtil.createQueryBatch("_id", targetEntityIds));

        //更新数据
        Update update = new Update();
        if (dataRule.updateType() == UpdateType.Replace) {
            update.set(dataRule.targetField(), fieldObject);
        } else if (dataRule.updateType() == UpdateType.Inc) {
            update.inc(dataRule.targetField(), new BigDecimal(String.valueOf(fieldObject)));
        }

        //时间戳
        this.dbHelper.updateTime(update);

        //进行批量修改
        this.mongoTemplate.updateMulti(query, update, dataRule.targetEntity());
    }

}
