package com.fast.dev.timercenter.server.core.factory;

import com.fast.dev.timercenter.server.core.conf.DefaultRequestParmConf;
import com.fast.dev.timercenter.service.model.RequestParmModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultRequestParm {


    @Autowired
    private DefaultRequestParmConf defaultRequestParmModelConf;


    /**
     * 获取默认的请求模型
     *
     * @return
     */
    public RequestParmModel get() {
        RequestParmModel requestParmModel = new RequestParmModel();
        BeanUtils.copyProperties(defaultRequestParmModelConf, requestParmModel);
        return requestParmModel;
//        RequestParmModel parmModel = new RequestParmModel();
//
//        // http
//        HttpModel http = new HttpModel();
//        http.setMethod(MethodType.Get);
//        http.setHeader(new HashMap<String, Object>() {{
//            put("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
//        }});
//        http.setCharset("UTF-8");
//        parmModel.setHttp(http);
//
//        //error
//        parmModel.setErrorTry(new ErrorTryModel(3, 30000l, CheckType.Code, 200));
//
//        //十分钟
//        parmModel.setSleepTime(1000 * 60 * 10l);


//        return parmModel;
    }


}
