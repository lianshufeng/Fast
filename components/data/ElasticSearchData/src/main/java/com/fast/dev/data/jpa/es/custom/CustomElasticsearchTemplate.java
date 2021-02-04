//package com.fast.dev.data.jpa.es.custom;
//
//import org.elasticsearch.action.bulk.BulkItemResponse;
//import org.elasticsearch.action.bulk.BulkRequestBuilder;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.action.index.IndexRequestBuilder;
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.Requests;
//import org.elasticsearch.index.VersionType;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.sort.FieldSortBuilder;
//import org.elasticsearch.search.sort.ScoreSortBuilder;
//import org.elasticsearch.search.sort.SortBuilders;
//import org.elasticsearch.search.sort.SortOrder;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.elasticsearch.ElasticsearchException;
//import org.springframework.data.elasticsearch.core.*;
//import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
//import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
//import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
//import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
//import org.springframework.data.elasticsearch.core.query.*;
//import org.springframework.util.Assert;
//import org.springframework.util.StringUtils;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.springframework.util.CollectionUtils.isEmpty;
//
//public class CustomElasticsearchTemplate extends ElasticsearchTemplate {
//
//
//    private static final String FIELD_SCORE = "_score";
//
//    private Client client;
//    private ResultsMapper resultsMapper;
//
//
//    public CustomElasticsearchTemplate(Client client, MappingElasticsearchConverter mappingElasticsearchConverter, ResultsMapper resultsMapper) {
//        super(client, mappingElasticsearchConverter, resultsMapper);
//        this.client = client;
//        this.resultsMapper = resultsMapper;
//    }
//
//    public CustomElasticsearchTemplate(Client client, ResultsMapper resultsMapper) {
//        this(client, new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext()), resultsMapper);
//    }
//
//
//    private void setPersistentEntityId(Object entity, String id) {
//
//        ElasticsearchPersistentEntity<?> persistentEntity = getPersistentEntityFor(entity.getClass());
//        ElasticsearchPersistentProperty idProperty = persistentEntity.getIdProperty();
//
//        // Only deal with text because ES generated Ids are strings !
//
//        if (idProperty != null && idProperty.getType().isAssignableFrom(String.class)) {
//            persistentEntity.getPropertyAccessor(entity).setProperty(idProperty, id);
//        }
//    }
//
//
//    @Override
//    public String index(IndexQuery query) {
//        String documentId = prepareIndex(query).execute().actionGet().getId();
//        // We should call this because we are not going through a mapper.
//        if (query.getObject() != null) {
//            setPersistentEntityId(query.getObject(), documentId);
//        }
//        return documentId;
//    }
//
//
//    @Override
//    public void bulkIndex(List<IndexQuery> queries) {
//        BulkRequestBuilder bulkRequest = client.prepareBulk();
//        for (IndexQuery query : queries) {
//            bulkRequest.add(prepareIndex(query));
//        }
//        checkForBulkUpdateFailure(bulkRequest.execute().actionGet());
//    }
//
//
//    private void checkForBulkUpdateFailure(BulkResponse bulkResponse) {
//        if (bulkResponse.hasFailures()) {
//            Map<String, String> failedDocuments = new HashMap<>();
//            for (BulkItemResponse item : bulkResponse.getItems()) {
//                if (item.isFailed())
//                    failedDocuments.put(item.getId(), item.getFailureMessage());
//            }
//            throw new ElasticsearchException(
//                    "Bulk indexing has failures. Use ElasticsearchException.getFailedDocuments() for detailed messages ["
//                            + failedDocuments + "]",
//                    failedDocuments);
//        }
//    }
//
//    private String getPersistentEntityId(Object entity) {
//
//        ElasticsearchPersistentEntity<?> persistentEntity = getPersistentEntityFor(entity.getClass());
//        Object identifier = persistentEntity.getIdentifierAccessor(entity).getIdentifier();
//
//        if (identifier != null) {
//            return identifier.toString();
//        }
//
//        return null;
//    }
//
//    private VersionType retrieveVersionTypeFromPersistentEntity(Class clazz) {
//        if (clazz != null) {
//            return getPersistentEntityFor(clazz).getVersionType();
//        }
//        return VersionType.EXTERNAL;
//    }
//
//
//    private IndexRequestBuilder prepareIndex(IndexQuery query) {
//        try {
//            String indexName = StringUtils.isEmpty(query.getIndexName())
//                    ? retrieveIndexNameFromPersistentEntity(query.getObject().getClass())[0]
//                    : query.getIndexName();
//            String type = StringUtils.isEmpty(query.getType()) ? retrieveTypeFromPersistentEntity(query.getObject().getClass())[0]
//                    : query.getType();
//
//            IndexRequestBuilder indexRequestBuilder = null;
//
//            if (query.getObject() != null) {
//                String id = StringUtils.isEmpty(query.getId()) ? getPersistentEntityId(query.getObject()) : query.getId();
//                // If we have a query id and a document id, do not ask ES to generate one.
//                if (id != null) {
//                    indexRequestBuilder = client.prepareIndex(indexName, type, id);
//                } else {
//                    indexRequestBuilder = client.prepareIndex(indexName, type);
//                }
//                indexRequestBuilder.setSource(resultsMapper.getEntityMapper().mapToString(query.getObject()),
//                        Requests.INDEX_CONTENT_TYPE);
//            } else if (query.getSource() != null) {
//                indexRequestBuilder = client.prepareIndex(indexName, type, query.getId()).setSource(query.getSource(),
//                        Requests.INDEX_CONTENT_TYPE);
//            } else {
//                throw new ElasticsearchException(
//                        "object or source is null, failed to index the document [id: " + query.getId() + "]");
//            }
//            if (query.getVersion() != null) {
//                indexRequestBuilder.setVersion(query.getVersion());
//                VersionType versionType = retrieveVersionTypeFromPersistentEntity(query.getObject().getClass());
//                indexRequestBuilder.setVersionType(versionType);
//            }
//
//            if (query.getParentId() != null) {
////                indexRequestBuilder.setParent(query.getParentId());
//
//
//                throw new ElasticsearchException(
//                        "暂时不支持 query.getParentId() ");
//            }
//
//            return indexRequestBuilder;
//        } catch (IOException e) {
//            throw new ElasticsearchException("failed to index the document [id: " + query.getId() + "]", e);
//        }
//    }
//
//
//    @Override
//    public <T> long count(CriteriaQuery criteriaQuery, Class<T> clazz) {
//        QueryBuilder elasticsearchQuery = new CustomCriteriaQueryProcessor().createQueryFromCriteria(criteriaQuery.getCriteria());
//        QueryBuilder elasticsearchFilter = new CustomCriteriaFilterProcessor()
//                .createFilterFromCriteria(criteriaQuery.getCriteria());
//
//        if (elasticsearchFilter == null) {
//            return doCount(prepareCount(criteriaQuery, clazz), elasticsearchQuery);
//        } else {
//            // filter could not be set into CountRequestBuilder, convert request into search request
//            return doCount(prepareSearch(criteriaQuery, clazz), elasticsearchQuery, elasticsearchFilter);
//        }
//    }
//
//    @Override
//    public <T> long count(SearchQuery searchQuery, Class<T> clazz) {
//        QueryBuilder elasticsearchQuery = searchQuery.getQuery();
//        QueryBuilder elasticsearchFilter = searchQuery.getFilter();
//
//        if (elasticsearchFilter == null) {
//            return doCount(prepareCount(searchQuery, clazz), elasticsearchQuery);
//        } else {
//            // filter could not be set into CountRequestBuilder, convert request into search request
//            return doCount(prepareSearch(searchQuery, clazz), elasticsearchQuery, elasticsearchFilter);
//        }
//    }
//
//    @Override
//    public <T> long count(CriteriaQuery query) {
//        return count(query, null);
//    }
//
//    @Override
//    public <T> long count(SearchQuery query) {
//        return count(query, null);
//    }
//
//
//    private static String[] toArray(List<String> values) {
//        String[] valuesAsArray = new String[values.size()];
//        return values.toArray(valuesAsArray);
//    }
//
//
//    private SearchRequestBuilder prepareSearch(Query query) {
//        Assert.notNull(query.getIndices(), "No index defined for Query");
//        Assert.notNull(query.getTypes(), "No type defined for Query");
//
//        int startRecord = 0;
//        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(toArray(query.getIndices()))
//                .setSearchType(query.getSearchType())
//                .setTypes(toArray(query.getTypes()))
//                .setVersion(true)
//                .setTrackScores(query.getTrackScores());
//
//        if (query.getSourceFilter() != null) {
//            SourceFilter sourceFilter = query.getSourceFilter();
//            searchRequestBuilder.setFetchSource(sourceFilter.getIncludes(), sourceFilter.getExcludes());
//        }
//
//        if (query.getPageable().isPaged()) {
//            startRecord = query.getPageable().getPageNumber() * query.getPageable().getPageSize();
//            searchRequestBuilder.setSize(query.getPageable().getPageSize());
//        }
//        searchRequestBuilder.setFrom(startRecord);
//
//        if (!query.getFields().isEmpty()) {
//            searchRequestBuilder.setFetchSource(toArray(query.getFields()), null);
//        }
//
//        if (query.getIndicesOptions() != null) {
//            searchRequestBuilder.setIndicesOptions(query.getIndicesOptions());
//        }
//
//        if (query.getSort() != null) {
//            for (Sort.Order order : query.getSort()) {
//                SortOrder sortOrder = order.getDirection().isDescending() ? SortOrder.DESC : SortOrder.ASC;
//
//                if (FIELD_SCORE.equals(order.getProperty())) {
//                    ScoreSortBuilder sort = SortBuilders //
//                            .scoreSort() //
//                            .order(sortOrder);
//
//                    searchRequestBuilder.addSort(sort);
//                } else {
//                    FieldSortBuilder sort = SortBuilders //
//                            .fieldSort(order.getProperty()) //
//                            .order(sortOrder);
//
//                    if (order.getNullHandling() == Sort.NullHandling.NULLS_FIRST) {
//                        sort.missing("_first");
//                    } else if (order.getNullHandling() == Sort.NullHandling.NULLS_LAST) {
//                        sort.missing("_last");
//                    }
//
//                    searchRequestBuilder.addSort(sort);
//                }
//            }
//        }
//
//        if (query.getMinScore() > 0) {
//            searchRequestBuilder.setMinScore(query.getMinScore());
//        }
//        return searchRequestBuilder;
//    }
//
//    private void setPersistentEntityIndexAndType(Query query, Class clazz) {
//        if (query.getIndices().isEmpty()) {
//            query.addIndices(retrieveIndexNameFromPersistentEntity(clazz));
//        }
//        if (query.getTypes().isEmpty()) {
//            query.addTypes(retrieveTypeFromPersistentEntity(clazz));
//        }
//    }
//
//    private <T> SearchRequestBuilder prepareSearch(Query query, Class<T> clazz) {
//        setPersistentEntityIndexAndType(query, clazz);
//        return prepareSearch(query);
//    }
//
//    private String[] retrieveTypeFromPersistentEntity(Class clazz) {
//        if (clazz != null) {
//            return new String[]{getPersistentEntityFor(clazz).getIndexType()};
//        }
//        return null;
//    }
//
//
//    private String[] retrieveIndexNameFromPersistentEntity(Class clazz) {
//        if (clazz != null) {
//            return new String[]{getPersistentEntityFor(clazz).getIndexName()};
//        }
//        return null;
//    }
//
//
//    private <T> SearchRequestBuilder prepareCount(Query query, Class<T> clazz) {
//        String indexName[] = !isEmpty(query.getIndices())
//                ? query.getIndices().toArray(new String[query.getIndices().size()])
//                : retrieveIndexNameFromPersistentEntity(clazz);
//        String types[] = !isEmpty(query.getTypes()) ? query.getTypes().toArray(new String[query.getTypes().size()])
//                : retrieveTypeFromPersistentEntity(clazz);
//
//        Assert.notNull(indexName, "No index defined for Query");
//
//        SearchRequestBuilder countRequestBuilder = client.prepareSearch(indexName);
//
//        if (types != null) {
//            countRequestBuilder.setTypes(types);
//        }
//        countRequestBuilder.setSize(0);
//        return countRequestBuilder;
//    }
//
//
//    private long doCount(SearchRequestBuilder countRequestBuilder, QueryBuilder elasticsearchQuery) {
//
//        if (elasticsearchQuery != null) {
//            countRequestBuilder.setQuery(elasticsearchQuery);
//        }
//        return countRequestBuilder.execute().actionGet().getHits().getTotalHits().value;
//    }
//
//    private long doCount(SearchRequestBuilder searchRequestBuilder, QueryBuilder elasticsearchQuery,
//                         QueryBuilder elasticsearchFilter) {
//        if (elasticsearchQuery != null) {
//            searchRequestBuilder.setQuery(elasticsearchQuery);
//        } else {
//            searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
//        }
//        if (elasticsearchFilter != null) {
//            searchRequestBuilder.setPostFilter(elasticsearchFilter);
//        }
//        return searchRequestBuilder.execute().actionGet().getHits().getTotalHits().value;
//    }
//
//}
