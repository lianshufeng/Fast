package com.fast.dev.data.mongo.helper;

import com.fast.dev.data.mongo.model.QueryModel;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.codehaus.groovy.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MongoQueryLanguageHelper {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    /**
     * 通过 mongo的分页条件查询
     *
     * @param queryModel
     * @param pageable
     * @param entityClass
     * @param <T>
     * @return
     */
    @SneakyThrows
    public <T> Page<T> queryByMql(QueryModel queryModel, Pageable pageable, Class<T> entityClass) {
        Page<Document> page = this.queryByMql(queryModel, pageable, this.dbHelper.getCollectionName(entityClass));
        //转换为数据库实体
        List<T> resultSet = page.getContent().parallelStream().map((it) -> {
            return (T) mongoTemplate.getConverter().read(entityClass, it);
        }).collect(Collectors.toList());
        return new PageImpl<T>(resultSet, pageable, page.getTotalElements());
    }


    @SneakyThrows
    public Page<Document> queryByMql(QueryModel queryModel, Pageable pageable, String tableName) {
        MongoCollection<Document> mongoCollection = this.mongoTemplate.getCollection(tableName);

        final String mql = !StringUtils.hasText(queryModel.getMql()) ? "{}" : queryModel.getMql();
        final Set<String> fields = queryModel.getFields() == null ? new HashSet<>() : queryModel.getFields();

        //排序
        Bson sort = sort(pageable.getSort());


        //mql查询对象
        Document mqlBson = Document.parse(mql);

        //分页
        long total = mongoCollection.countDocuments(mqlBson);
        int skip = Integer.parseInt(String.valueOf(pageable.getOffset()));
        int limit = pageable.getPageSize();


        //构建查询语句
        FindIterable<Document> findIterable = mongoCollection.find(mqlBson);

        //过滤需要显示的字段
        if (fields.size() > 0) {
            findIterable = findIterable.projection(projection(fields));
        }

        //分页条件
        findIterable = findIterable.skip(skip).limit(limit).sort(sort);


        //结果集
        List<Document> resultSet = new ArrayList<>();

        //查询
        for (Document document : findIterable) {
            resultSet.add(document);
        }

        return new PageImpl<Document>(resultSet, pageable, total);
    }

    /**
     * 转换为sort
     *
     * @return
     */
    private static Document sort(Sort sort) {
        Document bson = new Document();
        if (sort.isUnsorted()) {
            return bson;
        }
        sort.stream().forEach((order) -> {
                    bson.put(order.getProperty(), order.getDirection() == Sort.Direction.DESC ? -1 : 1);
                }
        );
        return bson;
    }

    /**
     * 转换为过滤器
     *
     * @param fields
     * @return
     */
    private static Document projection(Set<String> fields) {
        Document bson = new Document();
        fields.forEach((it) -> {
            bson.put(it, 1);
        });
        return bson;
    }

}
