package com.fast.dev.data.jpa.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 通用接口
 *
 * @param <T>
 */
public interface JpaDataDao<T> extends JpaRepository<T, Long> {
}
