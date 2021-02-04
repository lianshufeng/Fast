package com.fast.dev.promise.util;

import com.fast.dev.core.util.net.apache.HttpModel;
import com.fast.dev.core.util.net.apache.MethodType;
import com.fast.dev.promise.model.ErrorTryModel;
import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.type.CheckType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromiseTaskFactory {

    /**
     * 尝试次数
     */
    private Integer tryCount;

    /**
     * 延迟时间
     */
    private Long sleepTime;


    /**
     * 数据检查类型
     */
    private CheckType checkType;


    /**
     * 校验值
     */
    private Object checkValue;


    /**
     * Url地址
     */
    private String url;


    /**
     * 网络请求方式
     */
    private MethodType method;


    /**
     * 请求头
     */
    private Map<String, Object> header;


    /**
     * 请求体，仅为post生效
     */
    private Object body;


    /**
     * 请求编码
     */
    private String charset;


    /**
     * 任务id
     */
    private String id;


    /**
     * 执行时间
     */
    private Long executeTime;


    /**
     * 构建对象
     *
     * @return
     */
    public RequestParmModel build() {
        HttpModel httpModel = new HttpModel();
        BeanUtils.copyProperties(this, httpModel);

        ErrorTryModel errorTryModel = new ErrorTryModel();
        BeanUtils.copyProperties(this, errorTryModel);

        RequestParmModel requestParmModel = new RequestParmModel();
        BeanUtils.copyProperties(this, requestParmModel);

        requestParmModel.setHttp(httpModel);
        requestParmModel.setErrorTry(errorTryModel);
        return requestParmModel;
    }


}
