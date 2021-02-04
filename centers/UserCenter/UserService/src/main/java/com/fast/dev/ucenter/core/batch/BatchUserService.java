package com.fast.dev.ucenter.core.batch;

import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.model.batch.BatchQueryLoginNameModel;
import com.fast.dev.ucenter.core.model.batch.BatchQueryValue;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * 批量的用户用户接口
 */

@FeignClient(name = "userserver/ucenter/manager/batch")
public interface BatchUserService {


    /**
     * 通过用户id查询
     *
     * @return
     */
    @RequestMapping(value = "queryUserId")
    Map<String, BaseUserModel> queryUserId(@RequestBody BatchQueryValue value);


    /**
     * 通过登陆方式查询用户
     *
     * @return
     */
    @RequestMapping(value = "queryByLoginName")
    List<BaseUserModel> queryByLoginName(@RequestBody BatchQueryLoginNameModel queryLoginNameModel);


}
