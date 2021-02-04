package com.fast.dev.data.base.data;

import com.fast.dev.data.base.data.model.UpdateSyncDetails;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.Set;

/**
 * 数据同步助手
 */
public interface DataSyncHelper {


    /**
     * 数据同步助手
     *
     * @param entityClasses
     * @param id
     * @return
     */
    Set<UpdateSyncDetails> update(Class<? extends AbstractPersistable> entityClasses, Object id);


    /**
     * 数据同步助手
     *
     * @param entityClasses
     * @param id
     * @param maxNoticeCount
     * @return
     */
    Set<UpdateSyncDetails> update(Class<? extends AbstractPersistable> entityClasses, Object id, int maxNoticeCount);


}
