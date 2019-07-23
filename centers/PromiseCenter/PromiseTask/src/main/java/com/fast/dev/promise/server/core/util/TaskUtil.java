package com.fast.dev.promise.server.core.util;

import com.fast.dev.core.util.net.apache.ResponseModel;
import com.fast.dev.core.util.spring.SpringELUtil;
import com.fast.dev.promise.model.ErrorTryModel;
import com.fast.dev.promise.type.CheckType;

public class TaskUtil {


    /**
     * 任务校验
     *
     * @param responseModel
     * @param errorTryModel
     * @return
     */
    public static boolean validate(ResponseModel responseModel, ErrorTryModel errorTryModel) {
        // 响应代码
        if (errorTryModel.getCheckType() == CheckType.Code) {
            return Integer.parseInt(String.valueOf(errorTryModel.getCheckValue())) == responseModel.getCode();
        }

        // 响应内容
        if (errorTryModel.getCheckType() == CheckType.Text) {
            return responseModel.getBody().equals(errorTryModel.getCheckValue());
        }


        //响应json则执行表达式
        if (errorTryModel.getCheckType() == CheckType.Script) {
            Object ret = SpringELUtil.parseExpression(responseModel, String.valueOf(errorTryModel.getCheckValue()));
            if (ret != null && ret instanceof Boolean) {
                return (boolean) ret;
            }
            return false;
        }

        return false;
    }

}
