//package com.fast.dev.data.jpa.es.custom;
//
//import com.fast.dev.core.util.JsonUtil;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.common.document.DocumentField;
//import org.elasticsearch.search.SearchHit;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.ElasticsearchException;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.ScriptedField;
//import org.springframework.data.elasticsearch.core.DefaultResultMapper;
//import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
//import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
//import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
//import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
//import org.springframework.data.mapping.context.MappingContext;
//import org.springframework.util.Assert;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 自定义结果集,支持 es7
// */
//public class CustomResultMapper extends DefaultResultMapper {
//
//
//    private final MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext;
//
//
//    public CustomResultMapper(MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext) {
//        super(mappingContext);
//        this.mappingContext = mappingContext;
//    }
//
//
//    @Override
//    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
//
//        long totalHits = response.getHits().getTotalHits().value;
//        float maxScore = response.getHits().getMaxScore();
//
//        List<T> results = new ArrayList<>();
//        for (SearchHit hit : response.getHits()) {
//            if (hit != null) {
//                T result = null;
//                if (!StringUtils.isEmpty(hit.getSourceAsString())) {
//                    result = mapEntity(hit.getSourceAsString(), clazz);
//                } else {
//                    result = mapEntity(JsonUtil.toJson(hit.getFields().values()), clazz);
//                }
//
//
//                setPersistentEntityId(result, hit.getId(), clazz);
//                setPersistentEntityVersion(result, hit.getVersion(), clazz);
//                setPersistentEntityScore(result, hit.getScore(), clazz);
//
//                populateScriptFields(result, hit);
//                results.add(result);
//            }
//        }
//
//        return new AggregatedPageImpl<T>(results, pageable, totalHits, response.getAggregations(), response.getScrollId(),
//                maxScore);
//    }
//
//
//    private <T> void setPersistentEntityId(T result, String id, Class<T> clazz) {
//
//        if (clazz.isAnnotationPresent(Document.class)) {
//
//            ElasticsearchPersistentEntity<?> persistentEntity = mappingContext.getRequiredPersistentEntity(clazz);
//            ElasticsearchPersistentProperty idProperty = persistentEntity.getIdProperty();
//
//            // Only deal with String because ES generated Ids are strings !
//            if (idProperty != null && idProperty.getType().isAssignableFrom(String.class)) {
//                persistentEntity.getPropertyAccessor(result).setProperty(idProperty, id);
//            }
//        }
//    }
//
//
//    private <T> void setPersistentEntityVersion(T result, long version, Class<T> clazz) {
//
//        if (clazz.isAnnotationPresent(Document.class)) {
//
//            ElasticsearchPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(clazz);
//            ElasticsearchPersistentProperty versionProperty = persistentEntity.getVersionProperty();
//
//            // Only deal with Long because ES versions are longs !
//            if (versionProperty != null && versionProperty.getType().isAssignableFrom(Long.class)) {
//                // check that a version was actually returned in the response, -1 would indicate that
//                // a search didn't request the version ids in the response, which would be an issue
//                Assert.isTrue(version != -1, "Version in response is -1");
//                persistentEntity.getPropertyAccessor(result).setProperty(versionProperty, version);
//            }
//        }
//    }
//
//
//    private <T> void setPersistentEntityScore(T result, float score, Class<T> clazz) {
//
//        if (clazz.isAnnotationPresent(Document.class)) {
//
//            ElasticsearchPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(clazz);
//
//            if (!entity.hasScoreProperty()) {
//                return;
//            }
//
//            entity.getPropertyAccessor(result) //
//                    .setProperty(entity.getScoreProperty(), score);
//        }
//    }
//
//
//    private <T> void populateScriptFields(T result, SearchHit hit) {
//        if (hit.getFields() != null && !hit.getFields().isEmpty() && result != null) {
//            for (java.lang.reflect.Field field : result.getClass().getDeclaredFields()) {
//                ScriptedField scriptedField = field.getAnnotation(ScriptedField.class);
//                if (scriptedField != null) {
//                    String name = scriptedField.name().isEmpty() ? field.getName() : scriptedField.name();
//                    DocumentField searchHitField = hit.getFields().get(name);
//                    if (searchHitField != null) {
//                        field.setAccessible(true);
//                        try {
//                            field.set(result, searchHitField.getValue());
//                        } catch (IllegalArgumentException e) {
//                            throw new ElasticsearchException(
//                                    "failed to set scripted field: " + name + " with value: " + searchHitField.getValue(), e);
//                        } catch (IllegalAccessException e) {
//                            throw new ElasticsearchException("failed to access scripted field: " + name, e);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}
