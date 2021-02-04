package com.fast.dev.auth.center.server.core.controller.manager.data;

import com.fast.dev.data.base.data.DataCleanManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/manager/dataclean")
public class DataCleanController {

    @Autowired
    private DataCleanManager dataCleanManager;


    @RequestMapping("task")
    public Object task(String name) {
        return new HashMap<>() {{
            put("time", System.currentTimeMillis());
            put("state", dataCleanManager.execute(name, true));
        }};
    }


}
