package com.fast.dev.auth.center.server.core.dao.extend;

import com.fast.dev.auth.client.model.GeneralItemModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Set;

/**
 * 内部资源的接口
 */
public interface InternalResourceDaoExtend<T> {
    /**
     * 注册权限
     *
     * @param kv
     * @return
     */
    long put(Map<String, String> kv);


    /**
     * 删除权限
     *
     * @param names
     */
    long remove(String... names);


    /**
     * 模糊分页查询权限列表
     *
     * @param name
     * @param pageable
     * @return
     */
    Page<T> findByNameLike(String name, Pageable pageable);


    /**
     * 分页条件查询
     *
     * @param generalItemModel
     * @param pageable
     * @return
     */
    Page<T> list(GeneralItemModel generalItemModel, Set<String> ignoreValue, Pageable pageable);

}
