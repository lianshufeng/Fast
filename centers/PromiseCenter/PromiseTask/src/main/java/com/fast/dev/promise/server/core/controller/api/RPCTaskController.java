package com.fast.dev.promise.server.core.controller.api;

import com.fast.dev.core.util.result.InvokerResult;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("rpc/api")
public class RPCTaskController extends TaskController {


    @Override
    @RequestMapping("query")
    public Object query(String id) throws Exception {
        return InvokerResult.notNull(super.query(id));
    }

    @Override
    @RequestMapping("heartbeat")
    public Object heartbeat(String... id) {
        return InvokerResult.notNull(super.heartbeat(id));
    }

    @Override
    @RequestMapping("remove")
    public Object remove(String... id) {
        return InvokerResult.notNull(super.remove(id));
    }


    @Override
    public Object doit(String id) {
        return InvokerResult.notNull(super.doit(id));
    }

}
