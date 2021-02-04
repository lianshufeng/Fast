package com.fast.dev.data.base.data;

import com.fast.dev.data.base.data.model.UpdateDataDetails;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * 数据助手
 */
@Deprecated
public interface DataHelper {


    /**
     * 更细数据
     *
     * @param entityClasses
     * @param id
     * @return
     */
    @Deprecated
    UpdateDataDetails[] update(Class<? extends AbstractPersistable> entityClasses, Object id);


}
