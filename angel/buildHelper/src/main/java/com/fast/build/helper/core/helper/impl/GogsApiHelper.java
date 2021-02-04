package com.fast.build.helper.core.helper.impl;

import com.fast.build.helper.core.conf.BuildGitConf;
import com.fast.build.helper.core.helper.GitApiHelper;
import com.fast.build.helper.core.model.GitInfo;
import com.fast.build.helper.core.type.GitType;
import com.fast.build.helper.core.util.HttpClient;
import com.fast.build.helper.core.util.JsonUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
@Component
public class GogsApiHelper extends GitApiHelper {


    @Autowired
    private BuildGitConf defaultGitConf;


    @Override
    public GitType gitType() {
        return GitType.Gogs;
    }

    /**
     * 获取仓库
     *
     * @return
     */
    @Override
    public Map<String, GitInfo> userRepos() {
        Map<String, GitInfo> ret = new HashMap<>();

        List items = (List) get(url("/user/repos?limit=9999"));
        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = (Map) items.get(i);
            String full_name = String.valueOf(item.get("full_name"));

            GitInfo info = new GitInfo();
            info.setDescription(String.valueOf(item.get("description")));
            info.setUrl(String.valueOf(item.get("clone_url")));

            ret.put(full_name, info);
        }

        return ret;
    }

    /**
     * post 请求
     *
     * @param url
     * @param info
     * @return
     */
    private Object post(String url, Map<String, Object> info) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : info.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        return net(url, true, sb.toString());
    }


    /**
     * GET 请求
     *
     * @param url
     * @return
     */
    private Object get(String url) {
        return net(url, false, null);
    }


    /**
     * 网络请求
     *
     * @param url
     * @param isPost
     * @param info
     * @return
     */
    private Object net(String url, boolean isPost, String info) {
        try {
            if (info == null) {
                info = "";
            }
            Map<String, String> head = new HashMap<>();
            head.put("Authorization", "token " + this.defaultGitConf.getToken());
            HttpClient httpClient = new HttpClient();
            HttpClient.ResultBean rb = httpClient.ReadDocuments(url, isPost, info.getBytes("UTF-8"), head);
            return JsonUtil.toObject(new String(rb.getData(), rb.getCharset()), Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取url
     *
     * @param uri
     * @return
     */
    private String url(String uri) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.defaultGitConf.getHost());
        sb.append("/api/v1");
        sb.append("/" + uri);
        String url = sb.toString();
        String[] urls = url.split("://");
        String ret = urls[1];
        while (ret.indexOf("//") > -1) {
            ret = ret.replaceAll("//", "/");
        }
        return urls[0] + "://" + ret;
    }

}
