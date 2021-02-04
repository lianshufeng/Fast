package com.fast.dev.timercenter.server.core.util;

import com.fast.dev.core.util.net.apache.ResponseModel;
import com.fast.dev.core.util.spring.SpringELUtil;
import com.fast.dev.timercenter.service.model.CheckModel;
import com.fast.dev.timercenter.service.type.CheckType;

public class TaskUtil {


    /**
     * 任务校验
     *
     * @param responseModel
     * @param checkModel
     * @return
     */
    public static boolean validate(ResponseModel responseModel, CheckModel checkModel) {
        // 响应代码
        if (checkModel.getCheckType() == CheckType.Code) {
            return Integer.parseInt(String.valueOf(checkModel.getCheckValue())) == responseModel.getCode();
        }

        // 响应内容
        if (checkModel.getCheckType() == CheckType.Text) {
            return responseModel.getBody().equals(checkModel.getCheckValue());
        }


        //响应json则执行表达式
        if (checkModel.getCheckType() == CheckType.Script) {
            Object ret = SpringELUtil.parseExpression(responseModel, String.valueOf(checkModel.getCheckValue()));
            if (ret != null && ret instanceof Boolean) {
                return (boolean) ret;
            }
            return false;
        }

        return false;
    }

}
