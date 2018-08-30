package com.fast.dev.ucenter.core.helper;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.conf.ValidateDataConf;
import com.fast.dev.ucenter.core.model.ValidateData;
import com.fast.dev.ucenter.core.type.ValidateCodeScope;
import com.fast.dev.ucenter.core.type.ValidateType;
import com.fast.dev.ucenter.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 作者：练书锋
 * 时间：2018/8/30
 * 配置中心助手
 */
@Component
public class ValidateDataHelper {


    @Autowired
    private ValidateDataConf validateDataConf;


    /**
     * 取出配置
     *
     * @return
     */
    public ValidateDataConf get() {
        return this.validateDataConf;
    }


    /**
     * 取出指定APP的配置,如果没有则取默认值
     *
     * @param appName
     * @return
     */
    public ValidateDataConf get(String appName) {
        //如果没有配置则取默认
        if (this.validateDataConf.getApp() == null || StringUtils.isEmpty(appName)) {
            return get();
        }
        ValidateDataConf appConf = this.validateDataConf.getApp().get(appName);
        if (appConf == null) {
            return get();
        }
        return appConf;
    }


    /**
     * 取出数据校验的取出范围
     *
     * @return
     */
    public static String[] getValidateScopeValue(ValidateData validateData) {
        Set<String> buff = new HashSet<>();
        if (validateData.getScope() == ValidateCodeScope.Number) {
            for (int i = 0; i < 10; i++) {
                buff.add(String.valueOf(i));
            }
        } else if (validateData.getScope() == ValidateCodeScope.Charset) {
            for (int i = 0; i < 10; i++) {
                buff.add(String.valueOf(i));
            }
            for (int i = 97; i < 123; i++) {
                buff.add(String.valueOf((char) i));
            }
        }
        //增加额外的
        if (validateData.getExtraCharset() != null) {
            buff.addAll(Arrays.asList(validateData.getExtraCharset()));
        }
        return buff.toArray(new String[buff.size()]);
    }


    /**
     * 获取随机值
     *
     * @param validateData
     * @return
     */
    public static String getValidateRandomValue(ValidateData validateData) {
        if (validateData == null) {
            validateData = new ValidateData();
        }
        String[] values = getValidateScopeValue(validateData);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < validateData.getLength(); i++) {
            sb.append(values[RandomUtil.randInt(0, values.length - 1)]);
        }
        return sb.toString();
    }




}
