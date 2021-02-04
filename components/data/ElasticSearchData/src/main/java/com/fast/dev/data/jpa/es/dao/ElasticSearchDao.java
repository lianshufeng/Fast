package com.fast.dev.data.jpa.es.dao;

import com.fast.dev.data.jpa.es.domain.SuperEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ES的dao
 *
 * @param <T>
 */
public interface ElasticSearchDao<T extends SuperEntity> extends ElasticsearchRepository<T, String> {

}
