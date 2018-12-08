package com.fast.dev.filecenter.core.controller;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.core.util.result.InvokerState;
import com.fast.dev.filecenter.core.service.UrlMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("file")
public class UrlMappingController {
    @Autowired
    private UrlMappingService urlMappingService;

    /**
     * 设置URL映射，自定义URL
     * @param userId
     * @param fileId
     * @param url
     * @return
     */
    @RequestMapping("/setUrlMapping")
    public Object setUrlMapping(String userId, String fileId, String url){
        Assert.hasText(userId,"userId不能为空");
        Assert.hasText(fileId,"fileId不能为空");
        Assert.hasText(url,"URL不能为空");
        if(!StringUtils.isEmpty(url)&&urlMappingService.existUrl(userId,url)){
            return InvokerResult.builder().state(InvokerState.Error).content("自定义url已存在").build();
        }
        return InvokerResult.builder().state(InvokerState.Success).content(this.urlMappingService.setUrlMapping(userId,fileId,url)).build();
    }


    /**
     * 查询自定义URL是否存在
     * @param userId
     * @param url
     * @return
     */
    @RequestMapping("/checkUrlMapping")
    public Object checkUrlMapping(String userId,String url){
        if(urlMappingService.existUrl(userId,url)){
            return InvokerResult.builder().state(InvokerState.Error).content("自定义url已存在").build();
        }
        return InvokerResult.builder().state(InvokerState.Success).build();
    }

    /**
     * 修改URL映射，自定义URL
     * @param userId
     * @param fileId
     * @param url
     * @return
     */
    @RequestMapping("/updateUrlMapping")
    public Object updateUrlMapping(String userId, String fileId, String url){
        Assert.hasText(userId,"userId不能为空");
        Assert.hasText(fileId,"fileId不能为空");
        Assert.hasText(url,"URL不能为空");
        if(!urlMappingService.existUrl(userId,url)){
            return InvokerResult.builder().state(InvokerState.Error).content("数据不存在").build();
        }
        this.urlMappingService.updateUrlMapping(userId,fileId,url);
        return InvokerResult.builder().state(InvokerState.Success).build();
    }


    /**
     *  删除自定义URL
     * @param userId
     * @param url
     * @return
     */
    @RequestMapping("/deleteUrlMapping")
    public Object deleteUrlMapping(String userId, String url){
        Assert.hasText(userId,"userId不能为空");
        Assert.hasText(url,"URL不能为空");
        if(!urlMappingService.existUrl(userId,url)){
            return InvokerResult.builder().state(InvokerState.Error).content("数据不存在").build();
        }
        this.urlMappingService.deleteUrlMapping(userId,url);
        return InvokerResult.builder().state(InvokerState.Success).build();
    }
}
