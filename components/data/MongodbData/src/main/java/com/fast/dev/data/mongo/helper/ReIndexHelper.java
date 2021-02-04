package com.fast.dev.data.mongo.helper;

import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 重置索引
 */
@Slf4j
@Component
public class ReIndexHelper {

    @Autowired
    private MongoTemplate mongoTemplate;

    //每个索引里最大数量
    private final static int MaxIndexCount = 24;


    /**
     * 重建索引
     */
    public void reIndexFromField(Class<? extends SuperEntity> entityClass, String fieldName, Class<?> cls) {
        //构建索引
        Set<Index> nowIndexNames = BeanUtil.readBeanType(cls).keySet()
                .stream()
                .map((it) -> {
                    final String indexName = fieldName + "." + it;
                    return new Index().named(indexName).on(indexName, Sort.Direction.ASC);
                }).collect(Collectors.toSet());

        //更新索引
        updateIndex(entityClass, fieldName, nowIndexNames.toArray(new Index[0]));
    }


    /**
     * 重置索引
     */
    public void reIndexFromMap(Class<? extends SuperEntity> entityClass, String fieldName) {
        //构建索引
        Set<Index> nowIndexNames = getIndexNamesFromMap(entityClass, fieldName)
                .stream()
                .map((it) -> {
                    final String indexName = fieldName + "." + it;
                    return new Index().named(indexName).on(indexName, Sort.Direction.ASC);
                }).collect(Collectors.toSet());

        //更新索引
        updateIndex(entityClass, fieldName, nowIndexNames.toArray(new Index[0]));
    }


    /**
     * 更新索引，独立的索引项
     *
     * @param entityClass
     * @param indexs
     */
    @SneakyThrows
    public void updateIndex(Class<? extends SuperEntity> entityClass, Index... indexs) {
        //现有索引
        Set<String> nowIndexNames = this.mongoTemplate.indexOps(entityClass)
                .getIndexInfo()
                .stream()
                .map((it) -> {
                    return it.getName();
                }).collect(Collectors.toSet());


        //索引
        IndexOperations indexOperations = this.mongoTemplate.indexOps(entityClass);

        //索引不存在建索引
        Arrays.stream(indexs).filter((it) -> {
            try {
                Field field = it.getClass().getDeclaredField("name");
                field.setAccessible(true);
                Object val = field.get(it);
                return (val instanceof String) && !(nowIndexNames.contains(String.valueOf(val)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }).forEach((it) -> {
            String ret = indexOperations.ensureIndex(it);
            log.info("create index : " + ret);
        });

    }


    /**
     * 更新索引,共用一个索引项
     *
     * @param entityClass
     */
    @SneakyThrows
    public void updateIndex(Class<? extends SuperEntity> entityClass, String indexName, Index... indexs) {
        boolean isUpdate = false;
        List<IndexInfo> indexInfos = this.mongoTemplate.indexOps(entityClass)
                .getIndexInfo()
                .stream()
                .filter((it) -> {
                    return it.getName().length() > indexName.length() + 1 && it.getName().substring(0, indexName.length()).equals(indexName);
                }).collect(Collectors.toList());
        if (indexInfos != null && indexInfos.size() > 0) {
            isUpdate = isNeedUpdateIndex(indexInfos, indexs);
        } else {
            isUpdate = true;
        }

        //进行更新索引
        if (isUpdate) {
            final IndexOperations indexOperations = this.mongoTemplate.indexOps(entityClass);

            //如果存在则删除所有的和符合索引
            indexOperations.getIndexInfo().stream().filter((it) -> {
                return it.getName().length() > indexName.length() + 1 && it.getName().substring(0, indexName.length()).equals(indexName);
            }).forEach((it) -> {
                indexOperations.dropIndex(it.getName());
            });

            int size = (int) (indexs.length / MaxIndexCount);
            for (int i = 0; i < size; i++) {
                updateIndex(indexOperations, indexName + "_" + i, i, indexs);
            }
            if (indexs.length % MaxIndexCount != 0) {
                updateIndex(indexOperations, indexName + "_" + size, size, indexs);
            }
        }
    }

    @SneakyThrows
    private void updateIndex(IndexOperations indexOperations, String indexName, int page, final Index[] indexs) {
        Index index = new Index();
        index.named(indexName);

        for (int j = 0; j < MaxIndexCount; j++) {
            //索引
            int i = page * MaxIndexCount + j;
            if (i >= indexs.length) {
                continue;
            }
            Index it = indexs[i];
            Field field = it.getClass().getDeclaredField("name");
            field.setAccessible(true);
            String fieldName = String.valueOf(field.get(it));
            index.on(fieldName, Sort.Direction.ASC);
        }
        log.info("update index : {}", indexName);
        indexOperations.ensureIndex(index);

    }


    /**
     * 需要更新的名字
     *
     * @return
     */
    private boolean isNeedUpdateIndex(List<IndexInfo> indexInfos, Index[] indexs) {
        //取出需要更新的索引名
        Set<String> newUpdateNames = Arrays.stream(indexs).map((it) -> {
            return it.getIndexOptions().get("name");
        }).filter((it) -> {
            return it != null;
        }).map((it) -> {
            return String.valueOf(it);
        }).collect(Collectors.toSet());

        //取出现有的索引名
        Set<String> nowIndexName = new HashSet<>();
        indexInfos.forEach((it) -> {
            nowIndexName.addAll(it.getIndexFields().stream().map((index) -> {
                return index.getKey();
            }).collect(Collectors.toSet()));
        });


        for (String updateName : newUpdateNames) {
            if (!nowIndexName.contains(updateName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 取出Map属性对应的所有key的集合
     *
     * @return
     */
    public Set<String> getIndexNamesFromMap(Class<? extends SuperEntity> entityClass, String fieldName) {
        String collectionName = this.mongoTemplate.getCollectionName(entityClass);
        String map = "function(){if(this." + fieldName + "==null){return}for(var key in this." + fieldName + "){emit(key,1)}};";
        String reduce = "function(key,values){return values.length};";
        Set<String> indexNames = new HashSet<>();
        MapReduceResults<Map> mapReduceResults = this.mongoTemplate.mapReduce(collectionName, map, reduce, Map.class);
        mapReduceResults.forEach((it) -> {
            indexNames.add(String.valueOf(it.get("_id")));
        });
        return indexNames;
    }


}
