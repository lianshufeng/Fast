package com.fast.dev.promise.server.core.factory;

import com.fast.dev.core.util.net.apache.HttpModel;
import com.fast.dev.core.util.net.apache.MethodType;
import com.fast.dev.promise.model.ErrorTryModel;
import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.type.CheckType;

import java.util.HashMap;

public class DefaultRequestParm {


    /**
     * 获取默认的请求模型
     *
     * @return
     */
    public static RequestParmModel get() {
        RequestParmModel parmModel = new RequestParmModel();

        // http
        HttpModel http = new HttpModel();
        http.setMethod(MethodType.Get);
        http.setHeader(new HashMap<String, Object>() {{
            put("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        }});
        http.setCharset("UTF-8");
        parmModel.setHttp(http);

        //error
        parmModel.setErrorTry(new ErrorTryModel(3, 30000l, CheckType.Code, 200));

        //十分钟
        parmModel.setExecuteTime(1000 * 60 * 10l);


        return parmModel;
    }


}
