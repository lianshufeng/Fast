package com.fast.dev.pay.client.support.weixin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeiXinH5Info {

    //场景类型 (必填) , iOS, Android, Wap
    private String type;

    //应用名称
    private String app_name;

    //网站URL
    private String app_url;

    //iOS平台BundleID
    private String bundle_id;

    //Android平台PackageName
    private String package_name;


}
