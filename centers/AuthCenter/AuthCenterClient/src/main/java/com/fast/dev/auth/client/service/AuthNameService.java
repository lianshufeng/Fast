package com.fast.dev.auth.client.service;


import com.fast.dev.auth.client.model.AuthNameModel;
import com.fast.dev.auth.client.model.ResultContent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authcenterserver/manager/auth/name")
public interface AuthNameService {


    /**
     * 添加权限名称
     *
     * @param authName
     */
    @RequestMapping(value = "add")
    ResultContent add(@RequestBody AuthNameModel[] authName);


    /**
     * 删除权限名称
     *
     * @param authName
     */
    @RequestMapping(value = "remove")
    ResultContent remove(@RequestBody String[] authName);


    /**
     * 模糊查询
     *
     * @param pageable
     * @return
     */
    @RequestMapping(value = "list", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    Page<AuthNameModel> list(@RequestParam("name") String name, @RequestParam("remark") String remark, @PageableDefault Pageable pageable);


}