package com.fast.dev.promise.server.core.controller.api.manager;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.promise.model.RequestParmModel;
import lombok.extern.java.Log;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("manager/rpc/api")
public class RPCTaskManagerController  extends  TaskManagerController{


    @Override
    @RequestMapping("put")
    public Object put(@RequestBody @Validated RequestParmModel userModel) throws Exception {
        return InvokerResult.notNull(super.put(userModel));
    }


}
