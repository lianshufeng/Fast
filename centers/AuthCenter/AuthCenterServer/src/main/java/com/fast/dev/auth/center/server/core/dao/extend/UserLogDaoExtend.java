package com.fast.dev.auth.center.server.core.dao.extend;

import com.fast.dev.auth.center.server.core.domain.UserLog;
import com.fast.dev.auth.client.log.model.UserLogModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserLogDaoExtend {

    /**
     * 增加用户日志
     *
     * @param userLogModel
     */
    boolean add(UserLogModel... userLogModel);


    /**
     * 查询日志
     *
     * @param userLogModel
     * @param pageable
     * @return
     */
    Page<UserLog> list(UserLogModel userLogModel, Pageable pageable);


}
