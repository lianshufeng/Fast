package com.fast.build.helper.core.controller;

import com.fast.build.helper.core.conf.BuildTaskConf;
import com.fast.build.helper.core.helper.PathHelper;
import com.fast.dev.core.helper.ViewHelper;
import com.fast.dev.core.mvc.MVCConfiguration;
import lombok.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("res")
public class ResourceController {

    @Autowired
    private PathHelper pathHelper;

    @Autowired
    private BuildTaskConf buildTaskConf;

    @Autowired
    private ViewHelper viewHelper;


    /**
     * 更新项目
     *
     * @return
     */
    @RequestMapping({"list", "", "/"})
    public Object list(HttpServletRequest request) {
        String remoteRequestHost = this.viewHelper.getRemoteHost();
        File appResourceFile = this.pathHelper.getResourcePath(this.buildTaskConf.getAppResourceName());

        File[] files = appResourceFile.listFiles();
        if (files == null) {
            return new Object[0];
        }
        return Arrays.stream(files).map((file) -> {
            return ResourceItems.builder()
                    .url(remoteRequestHost + "/" + MVCConfiguration.StaticResources + "/" + buildTaskConf.getAppResourceName() + "/" + FilenameUtils.getName(file.getAbsolutePath()))
                    .updateTime(file.lastModified())
                    .size(file.length())
                    .build();
        })
                .sorted(Comparator.comparing(ResourceItems::getUpdateTime).reversed())
                .collect(Collectors.toList());
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ResourceItems {

        private String url;
        private long size;
        private long updateTime;

    }


    /**
     * 开始备份
     *
     * @return
     */
    @SneakyThrows
    @RequestMapping("backup/do")
    public Object backup() {
        String fileName = String.valueOf(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
        File appResourceFile = this.pathHelper.getResourcePath(this.buildTaskConf.getAppResourceName());
        File backupResourceFile = new File(this.pathHelper.getResourcePath(this.buildTaskConf.getBackupResourceName()).getAbsolutePath() + "/" + fileName);
        FileUtils.copyDirectory(appResourceFile, backupResourceFile);

        return new HashMap<>() {{
            put("time", System.currentTimeMillis());
            put("files", backupResourceFile.list());
        }};
    }

    /**
     * 查询备份列表
     *
     * @return
     */
    @RequestMapping("backup/list")
    public Object backupList() {
        File backupResourceFile = new File(this.pathHelper.getResourcePath(this.buildTaskConf.getBackupResourceName()).getAbsolutePath() + "/");
        if (!backupResourceFile.exists()) {
            return new Object[0];
        }
        return Arrays.stream(backupResourceFile.listFiles()).map((it) -> {
            return FilenameUtils.getName(it.getAbsolutePath());
        }).collect(Collectors.toList());
    }


    /**
     * 查看目录详情
     *
     * @param file
     * @return
     */
    @RequestMapping("backup/get")
    public Object backupGet(String file) {
        File backupResourceFile = new File(this.pathHelper.getResourcePath(this.buildTaskConf.getBackupResourceName()).getAbsolutePath() + "/" + file);
        if (!backupResourceFile.exists()) {
            return new Object[0];
        }
        String remoteRequestHost = this.viewHelper.getRemoteHost();
        return Arrays.stream(backupResourceFile.listFiles()).map((it) -> {
            return ResourceItems.builder()
                    .url(remoteRequestHost + "/" + MVCConfiguration.StaticResources + "/" + buildTaskConf.getBackupResourceName() + "/" + file + "/" + FilenameUtils.getName(it.getAbsolutePath()))
                    .updateTime(it.lastModified())
                    .size(it.length())
                    .build();
        }).collect(Collectors.toList());
    }


    /**
     * 删除备份
     *
     * @return
     */
    @RequestMapping("backup/delete")
    @SneakyThrows
    public Object backupDelete(String file) {
        Assert.hasText(file, "删除的备份名不能为空");

        File backupResourceFile = new File(this.pathHelper.getResourcePath(this.buildTaskConf.getBackupResourceName()).getAbsolutePath() + "/" + file);
        if (!backupResourceFile.exists()) {
            return new Object[0];
        }
        FileUtils.forceDelete(backupResourceFile);
        return new HashMap<>() {{
            put("time", System.currentTimeMillis());
        }};
    }


}
