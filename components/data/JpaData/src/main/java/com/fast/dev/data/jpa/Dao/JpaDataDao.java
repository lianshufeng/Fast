package com.fast.dev.data.jpa.Dao;

import org.springframework.data.repository.CrudRepository;

/**
 * 通用接口
 *
 * @param <T>
 */
public interface JpaDataDao<T> extends CrudRepository<T, Long> {
}
