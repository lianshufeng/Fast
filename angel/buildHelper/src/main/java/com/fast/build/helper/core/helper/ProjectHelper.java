package com.fast.build.helper.core.helper;

import com.fast.build.helper.core.model.GitItem;
import com.fast.build.helper.core.model.ProjectItem;
import com.fast.build.helper.core.util.ApplicationHomeUtil;
import com.fast.dev.core.helper.JsonHelper;
import com.fast.dev.core.util.bean.BeanUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目助手，持久化内存工具
 */
@Component
public class ProjectHelper {

    private static final String DefaultConfigFileName = "ApplicationStore.json";
    private static final File ConfigFile = ApplicationHomeUtil.getResource(DefaultConfigFileName);

    @Getter
    private Map<String, ProjectItem> items = new ConcurrentHashMap<>();

    @Autowired
    private JsonHelper jsonHelper;


    @Autowired
    private void init() {
        this.reload();
    }


    @Autowired
    private GitApiHelper gitApiHelper;


    /**
     * 通过项目名获取项目
     *
     * @param projectName
     * @return
     */
    public ProjectItem getProjectItem(String projectName) {
        return this.items.get(projectName);
    }


    /**
     * 同步数据
     *
     * @param item
     */
    public void update(ProjectItem item) {
        final String name = item.getName();
        ProjectItem projectItem = this.items.get(name);
        if (projectItem == null) {
            this.items.put(name, item);
            return;
        }
        Set<String> ignoreProperties = new HashSet<>();
        BeanUtil.getNullPropertyNames(item, ignoreProperties);
        BeanUtils.copyProperties(item, projectItem, ignoreProperties.toArray(new String[0]));
    }


    /**
     * 保存到磁盘上
     */
    @PreDestroy
    @SneakyThrows
    public void save() {
        FileUtils.writeStringToFile(ConfigFile, this.jsonHelper.toJson(this.getItemsFromSort(), true), "UTF-8");
    }


    /**
     * 获取项目按照顺序
     *
     * @return
     */
    public List<ProjectItem> getItemsFromSort() {
        List<ProjectItem> ret = new ArrayList<>(this.items.values());
        //排序
        Collections.sort(ret, (it1, it2) -> {
            return it1.getName().compareTo(it2.getName());
        });
        return ret;
    }

    /**
     * 从磁盘加载
     */
    @SneakyThrows
    public void reload() {
        //清空内存
        this.items.clear();


        //加载本地
        if (ConfigFile.exists()) {
            List<Map> projectItems = this.jsonHelper.toObject(FileUtils.readFileToString(ConfigFile, "UTF-8"), List.class);
            projectItems.stream().map((item) -> {
                ProjectItem projectItem = new ProjectItem();
                BeanUtil.setBean(projectItem, item);
                return projectItem;
            }).forEach((it) -> {
                //同步到内存
                this.update(it);
            });
        }

        //加载网络
        this.gitApiHelper.userRepos().entrySet().stream().map((it) -> {
            ProjectItem item = new ProjectItem();
            item.setName(it.getKey());

            GitItem gitItem = new GitItem();
            BeanUtils.copyProperties(it.getValue(), gitItem);
            item.setGit(gitItem);

            return item;
        }).forEach((it) -> {
            //同步到内存
            this.update(it);
        });

        //保存到磁盘
        this.save();

    }


}
