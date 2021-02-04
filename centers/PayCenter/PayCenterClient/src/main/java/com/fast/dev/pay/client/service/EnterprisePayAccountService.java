package com.fast.dev.pay.client.service;

import com.fast.dev.pay.client.model.EnterprisePayAccountModel;
import com.fast.dev.pay.client.result.ResultContent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "paycenterserver/manager/enterprise/pay")
public interface EnterprisePayAccountService {


    /**
     * 新增或者更新企业支付账号
     */
    @RequestMapping(value = "update")
    ResultContent update(@RequestBody EnterprisePayAccountModel payAccountModel);


    /**
     * 删除企业支付账号
     *
     * @return
     */
    @RequestMapping(value = "remove", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent remove(@RequestParam("id") String... id);


    /**
     * 获取该企业下的所有支付账号
     *
     * @return
     */
    @RequestMapping(value = "list", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<List<EnterprisePayAccountModel>> list(@RequestParam("enterpriseId") String enterpriseId);


    /**
     * 获取企业支付账号
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "get", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<EnterprisePayAccountModel> get(@RequestParam("id") String id);


}
