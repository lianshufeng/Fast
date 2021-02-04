package com.fast.dev.open.api.server.core.controller.api;

import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.auth.client.model.EnterpriseModelAndSK;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.open.api.server.core.conf.OpenApiConf;
import com.fast.dev.open.api.server.core.helper.EnterpriseHelper;
import com.fast.dev.open.api.server.core.helper.OpenApiRequetHelper;
import com.fast.dev.openapi.client.controller.OpenApiController;
import com.fast.dev.openapi.client.model.v1.ApiParamContent;
import com.fast.dev.openapi.client.model.v1.ApiRequest;
import com.fast.dev.openapi.client.model.v1.ApiResponse;
import com.fast.dev.openapi.client.model.v1.ResultState;
import com.fast.dev.openapi.client.util.OpenApiUrlUtil;
import com.fast.dev.openapi.client.util.OpenApiV1Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("v1")
public class V1RequestApiController {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private EnterpriseHelper enterpriseHelper;

    @Autowired
    private OpenApiConf openApiConf;

    @Autowired
    private OpenApiRequetHelper openApiRequetHelper;

    @Autowired
    private UserLogHelper userLogHelper;

    /**
     * 请求
     *
     * @param request
     * @return
     */
    @UserLog(action = "request")
    @RequestMapping(value = "/**/*", method = RequestMethod.POST)
    public ApiResponse requestV1(HttpServletRequest request, @RequestBody ApiRequest apiRequestV1) {

        //参数校验
        ApiResponse response = check(apiRequestV1);
        if (response != null) {
            return response;
        }
        this.userLogHelper.log("ak", apiRequestV1.getAk());


        //取出企业相关信息
        EnterpriseModelAndSK sk = enterpriseHelper.query(apiRequestV1.getAk());
        if (sk == null) {
            return ApiResponse.build(ResultState.AkError);
        }
        this.userLogHelper.log("epId", sk.getId());

        //解密数据
        ApiParamContent content = decryptBody(sk, apiRequestV1);
        if (content == null) {
            return ApiResponse.build(ResultState.decryptError);
        }

        //校验时间是否已
        if (!checkTimeInvalid(content)) {
            return ApiResponse.build(ResultState.TimeInvalid);
        }


        ApiResponse apiResponse = new ApiResponse();

        //请求和处理加密
        request(OpenApiUrlUtil.getAfterText(request.getRequestURI()), sk, content, apiResponse);


        return apiResponse;

    }


    /**
     * 转发请求
     */
    private void request(String url, EnterpriseModelAndSK sk, ApiParamContent content, ApiResponse apiResponse) {
        log.info("Api  -> {} , Ak -> {}", sk.getId(), sk.getAk());
        Map<String, String> header = new HashMap<>();
        header.put("ak", sk.getAk());
        header.put("epId", sk.getId());
        String param = null;
        if (content.getBody() == null) {
            param = null;
        } else if (content.getBody() instanceof String) {
            param = String.valueOf(content.getBody());
        } else {
            param = JsonUtil.toJson(content.getBody());
        }
        this.userLogHelper.log("param", param);


        //拼接URL: appName + openapi + url;
        String requestUrl = OpenApiUrlUtil.getFirst(url) + "/" + OpenApiController.OpenApiUrl + OpenApiUrlUtil.getAfterText(url);
        this.userLogHelper.log("requestUrl", requestUrl);


        Object ret = null;
        //调用其他接口
        try {
            //开发平台的网关转发
            ret = openApiRequetHelper.request(Object.class, requestUrl, header, param);
            //加密返回的数据
            apiResponse.setData(encryptResponse(sk, ret));
            apiResponse.setState(ResultState.Success);
        } catch (HttpClientErrorException.NotFound e) {
            apiResponse.setState(ResultState.RequestNotFound);
        } catch (IllegalStateException e) {
            log.error("调用接口错误 : {}", e);
            if (e.getMessage().indexOf("No instances") > -1) {
                apiResponse.setState(ResultState.RequestNotFound);
            } else {
                apiResponse.setState(ResultState.InternalError);
            }
        } catch (Exception e) {
            log.error("调用接口错误 : {}", e);
            apiResponse.setMsg(e.getMessage());
            apiResponse.setState(ResultState.InternalError);
        }

        //状态
        Optional.ofNullable(apiResponse.getState()).ifPresent((it) -> {
            this.userLogHelper.log("state", it.name());
        });

        //结果
        Optional.ofNullable(ret).ifPresent((it) -> {
            this.userLogHelper.log("ret", it);
        });

    }


    /**
     * 检查误差时间
     *
     * @param content
     * @return
     */
    private boolean checkTimeInvalid(ApiParamContent content) {
        return Math.abs(this.dbHelper.getTime() - content.getTime()) < openApiConf.getMaxTimeInvalid();
    }

    /**
     * 检查数据完整性
     *
     * @param apiRequest
     * @return
     */
    private ApiResponse check(ApiRequest apiRequest) {
        //应用key不能为空
        if (!StringUtils.hasText(apiRequest.getAk())) {
            return ApiResponse.build(ResultState.AkNotNull);
        }

        //参数不能为空
        if (!StringUtils.hasText(apiRequest.getData())) {
            return ApiResponse.build(ResultState.DataNotNull);
        }

        return null;
    }


    /**
     * 加密响应值
     *
     * @return
     */
    private String encryptResponse(EnterpriseModelAndSK sk, Object ret) {
        return OpenApiV1Util.encrypt(sk.getSk(), ret);
    }


    /**
     * 解密数据
     *
     * @return
     */
    private ApiParamContent decryptBody(EnterpriseModelAndSK sk, ApiRequest apiRequestV1) {
        try {
            return OpenApiV1Util.decrypt(sk.getSk(), apiRequestV1.getData(), ApiParamContent.class);
        } catch (Exception e) {
            log.error("decrypt error : {} ", e);
        }
        return null;
    }


}
