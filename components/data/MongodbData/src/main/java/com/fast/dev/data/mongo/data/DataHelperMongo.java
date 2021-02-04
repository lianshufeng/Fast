package com.fast.dev.data.mongo.data;

import com.fast.dev.core.util.script.GroovyUtil;
import com.fast.dev.data.base.data.DataHelper;
import com.fast.dev.data.base.data.annotations.DataRule;
import com.fast.dev.data.base.data.impl.DataHelperImpl;
import com.fast.dev.data.base.data.model.UpdateDataDetails;
import com.fast.dev.data.base.data.type.UpdateType;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Deprecated
@Component
public class DataHelperMongo extends DataHelperImpl implements DataHelper {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    //用于存入当前线程中，提高同一张表的操作效率
    private ThreadLocal<Map<Class<? extends AbstractPersistable>, BulkOperations>> threadLocal = new ThreadLocal<>();


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


        //模板引擎构建查询语句
        String filter = GroovyUtil.textTemplate(varMap, dataRule.targetQuery());

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

        //同一张表的批量操作
        BulkOperations bulkOperations = this.threadLocal.get().get(dataRule.targetEntity());
        if (bulkOperations == null) {
            bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, dataRule.targetEntity());
            this.threadLocal.get().put(dataRule.targetEntity(), bulkOperations);
        }
        bulkOperations.updateMulti(query, update);


        //进行批量修改
//        this.mongoTemplate.updateMulti(query, update, dataRule.targetEntity());
    }


    @Override
    public UpdateDataDetails[] update(Class<? extends AbstractPersistable> entityClasses, Object id) {
        //声明并发操作到当前线程中
        this.threadLocal.set(new HashMap<>());

        //执行批量更新的操作
        UpdateDataDetails[] dataDetails = super.update(entityClasses, id);

        //执行批量更新
        for (BulkOperations operations : this.threadLocal.get().values()) {
            operations.execute();
        }

        //从当前线程中删除
        threadLocal.remove();
        return dataDetails;
    }
}
