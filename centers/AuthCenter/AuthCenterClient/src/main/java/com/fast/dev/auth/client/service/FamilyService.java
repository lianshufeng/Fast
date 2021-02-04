package com.fast.dev.auth.client.service;


import com.fast.dev.auth.client.model.FamilyModel;
import com.fast.dev.auth.client.model.ResultContent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "authcenterserver/manager/family")
public interface FamilyService {


    /**
     * 更新/新增家庭
     *
     * @return 返回家庭id
     */
    @RequestMapping(value = "update")
    ResultContent<String> update(@RequestBody FamilyModel familyModel);


    /**
     * 清空家庭成员
     *
     * @return 返回家庭id
     */
    @RequestMapping(value = "clean", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<Boolean> clean(@RequestParam("id") String id);


    /**
     * 获取家庭
     *
     * @param
     * @return
     */
    @RequestMapping(value = "get", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<FamilyModel> get(@RequestParam("id") String id);


    /**
     * 通过成员的用户id查询家庭模型
     *
     * @param uid
     * @return
     */
    @RequestMapping(value = "findByMemberUid", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<List<FamilyModel>> findByMemberUid(@RequestParam("epId") String epId, @RequestParam("uid") String[] uid);


    /**
     * 通过企业id查询分页查询家庭组
     * <p>
     * 如果epId为空或者空字符串则查询全部的家庭组
     *
     * @return
     */
    @RequestMapping(value = "list", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    Page<FamilyModel> list(@RequestParam("epId") String epId, @PageableDefault Pageable pageable);


    /**
     * 删除家庭组
     *
     * @return
     */
    @RequestMapping(value = "remove", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<Boolean> remove(@RequestParam("id") String... id);

}
