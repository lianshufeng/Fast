package com.fast.dev.openapi.client.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(OpenApiController.OpenApiUrl)
public abstract class OpenApiController {

    public final static String OpenApiUrl = "openapi";


    @Autowired
    private HttpServletRequest request;


    /**
     * 企业id
     *
     * @return
     */
    public EnterpriseInfo getEnterpriseId() {
        String ak = request.getHeader("ak");
        String epId = request.getHeader("epId");
        return new EnterpriseInfo(ak, epId);
    }


    /**
     * 企业信息
     */
    public static class EnterpriseInfo {

        //应用key
        @Getter
        private String ak;
        //企业id
        @Getter
        private String epId;

        protected EnterpriseInfo(String ak, String epId) {
            this.ak = ak;
            this.epId = epId;
        }
    }


}
