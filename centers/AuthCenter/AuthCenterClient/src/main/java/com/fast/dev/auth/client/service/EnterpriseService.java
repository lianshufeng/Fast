package com.fast.dev.auth.client.service;


import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.model.EnterpriseModelAndSK;
import com.fast.dev.auth.client.model.EnterpriseQueryModel;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.type.ResultState;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authcenterserver/manager/enterprise")
public interface EnterpriseService {


    /**
     * 添加企业
     *
     * @param model
     * @param ownerUid
     * @return
     */
    @RequestMapping(value = "add", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<String> add(@ModelAttribute EnterpriseModel model, @RequestParam("ownerUid") String ownerUid);


    /**
     * 重置企业的通信秘钥
     *
     * @return 返回重置后的SK
     */
    @RequestMapping(value = "resetSK", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<String> resetSK(@RequestParam("id") String id);


    /**
     * 获取企业的SK
     *
     * @param ak AppKey
     * @return
     */
    @RequestMapping(value = "getFromAK", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    EnterpriseModelAndSK getFromAK(@RequestParam("ak") String ak);

    /**
     * 查询
     *
     * @param pageable
     * @return
     */
    @RequestMapping(value = "list", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    Page<EnterpriseModel> list(@ModelAttribute EnterpriseModel enterpriseModel, @PageableDefault Pageable pageable);


    /**
     * 更新企业信息
     *
     * @param enterpriseModel
     * @return
     */
    @RequestMapping(value = "update", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultState update(@ModelAttribute EnterpriseModel enterpriseModel);


    /**
     * 查询企业
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "get", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    EnterpriseModel get(@RequestParam("id") String id);


    /**
     * 自定义查询语句，查询企业信息
     *
     * @param enterpriseQueryModel
     * @param pageable
     * @return
     */
    @RequestMapping(value = "queryEnterprise", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE}, method = RequestMethod.POST)
    Page<EnterpriseModel> queryEnterprise(@ModelAttribute EnterpriseQueryModel enterpriseQueryModel, @PageableDefault Pageable pageable);


}
