package com.fast.dev.auth.center.server.core.dao.extend;

import com.fast.dev.auth.center.server.core.domain.Family;
import com.fast.dev.auth.client.model.FamilyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FamilyDaoExtend {


    /**
     * 通过成员id查询所有家庭组
     *
     * @return
     */
    List<Family> findByMember(String epId, String[] uid);


    /**
     * 分页查询家庭组列表
     *
     * @param epId
     * @param pageable
     * @return
     */
    Page<Family> list(String epId, Pageable pageable);


    /**
     * 更新家庭
     *
     * @param model
     * @return
     */
    String modifyFamily(FamilyModel model);


    /**
     * 插入家庭
     *
     * @param model
     * @return
     */
    String insertFamily(FamilyModel model);


}
