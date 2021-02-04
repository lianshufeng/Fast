//package com.fast.dev.timercenter.server.core.controller.test;
//
//import com.fast.dev.core.util.net.apache.HttpClientUtil;
//import com.fast.dev.core.util.net.apache.ResponseModel;
//import com.fast.dev.core.util.result.InvokerResult;
//import com.fast.dev.timercenter.service.model.RequestParmModel;
//import com.fast.dev.timercenter.server.core.util.RequestParmUtil;
//import com.fast.dev.timercenter.server.core.util.TaskUtil;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.HashMap;
//
//
//@RestController
//@RequestMapping("api")
//public class TestController {
//
//
//    @RequestMapping("manager/test")
//    public InvokerResult<Object> test(HttpServletRequest request, HttpServletResponse response, @RequestBody @Validated RequestParmModel userModel) throws Exception {
//        //转换为系统任务
//        RequestParmModel systemParm = RequestParmUtil.build(userModel);
//
//        //请求网络的响应结果
//        ResponseModel responseModel = HttpClientUtil.request(systemParm.getHttp());
//
//
//        //任务执行结果
//        boolean ret = TaskUtil.validate(responseModel, systemParm.getErrorTry());
//
//
//        return InvokerResult.notNull(new HashMap<String, Object>() {{
//            put("ret", ret);
//            put("response", responseModel);
//        }});
//    }
//
//
//}
