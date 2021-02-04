package com.fast.build.helper.core.helper;

import com.fast.build.helper.core.conf.BuildGitConf;
import com.fast.build.helper.core.model.ApplicationGitInfo;
import lombok.Cleanup;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;


@Slf4j
@Component
@Scope("prototype")
public class GitHelper {

    /**
     * 默认的配置
     */
    @Autowired
    private BuildGitConf gitConf;

    /**
     * 应用的git信息
     */
    @Setter
    private ApplicationGitInfo applicationGitInfo;


    @Setter
    private String appName;


    //git的工作空间
    private File gitWorkProjectFile;

    @Autowired
    private PathHelper pathHelper;

    /**
     * git项目更新
     */
    public void gitProjectUpdate() {
        //更新git项目地址
        this.gitWorkProjectFile = pathHelper.getGitWorkProjectFile(this.appName);
        log.info(this.appName + " -> " + this.gitWorkProjectFile.getAbsolutePath());
        if (this.gitWorkProjectFile.exists()) {
            updateBranch();
            gitReset();
            gitPull();
            gitCheckOut();
        } else {
            gitClone();
        }
    }


    /**
     * 更新分支
     */
    @SneakyThrows
    private void updateBranch() {

        //配置的分支名
        final String configBranchName = StringUtils.hasText(this.applicationGitInfo.getBranch()) ? this.applicationGitInfo.getBranch() : "master";

        //缓存的分支名
        final String cacheBranchName = StringUtils.hasText(getBranch()) ? getBranch() : null;

        if (configBranchName.equals(cacheBranchName)) {
            return;
        }

        log.info("切换分支 : {}", configBranchName);


        //删除老的仓库
        FileUtils.deleteDirectory(this.gitWorkProjectFile);
        //删除目录
        this.gitWorkProjectFile.delete();
        //重新克隆项目
        gitClone();

    }

    /**
     * 获取分支
     */
    @SneakyThrows
    private String getBranch() {
        @Cleanup Git git = Git.open(this.gitWorkProjectFile);
        return git.getRepository().getBranch();
    }


    /**
     * 克隆项目
     */
    @SneakyThrows
    private void gitClone() {
        //克隆仓库
        CloneCommand cmd = Git.cloneRepository();


        //切换分支
        if (StringUtils.hasText(this.applicationGitInfo.getBranch())) {
            cmd.setBranch(this.applicationGitInfo.getBranch());
        }

        //设置权限
        CredentialsProvider credentialsProvider = credentialsProvider();
        if (credentialsProvider != null) {
            cmd.setCredentialsProvider(credentialsProvider);
        }

        cmd.setURI(this.applicationGitInfo.getUrl());
        cmd.setDirectory(this.gitWorkProjectFile);


        cmd.call();
    }


    @SneakyThrows
    private void gitCheckOut() {
        @Cleanup Git git = Git.open(this.gitWorkProjectFile);
        CheckoutCommand cmd = git.checkout().addPath(".");
        Ref ref = cmd.call();
        log.info("rest : " + ref);
    }


    /**
     * 重置
     */
    @SneakyThrows
    private void gitReset() {
        @Cleanup Git git = Git.open(this.gitWorkProjectFile);
        ResetCommand cmd = git.reset().setMode(ResetCommand.ResetType.HARD);
        Ref ref = cmd.call();
        log.info("rest : " + ref);
    }


    /**
     * 更新项目
     */
    @SneakyThrows
    private void gitPull() {
        @Cleanup Git git = Git.open(this.gitWorkProjectFile);
        PullCommand cmd = git.pull();
        CredentialsProvider credentialsProvider = credentialsProvider();
        if (credentialsProvider != null) {
            cmd.setCredentialsProvider(credentialsProvider);
        }
        PullResult result = cmd.call();
        log.info("ret : " + result);
    }


    private CredentialsProvider credentialsProvider() {
        //设置鉴权
        if (this.applicationGitInfo.getUserName() != null || this.gitConf.getUserName() != null) {
            // 取出用户名与密码，优先取对应的应用配置，为空则取默认配置的
            String userName = this.applicationGitInfo.getUserName();
            if (userName == null) {
                userName = this.gitConf.getUserName();
            }
            String passWord = this.applicationGitInfo.getPassWord();
            if (passWord == null) {
                passWord = this.gitConf.getPassWord();
            }

            //设置git的权限
            CredentialsProvider cp = new UsernamePasswordCredentialsProvider(userName, passWord);
            return cp;
        }
        return null;
    }


}
