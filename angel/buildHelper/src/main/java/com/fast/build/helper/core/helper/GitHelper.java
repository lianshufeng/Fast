package com.fast.build.helper.core.helper;

import com.fast.build.helper.core.conf.DefaultGitConf;
import com.fast.build.helper.core.model.ApplicationGitInfo;
import com.fast.build.helper.core.util.ApplicationHomeUtil;
import lombok.Setter;
import lombok.extern.java.Log;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;


@Log
@Component
@Scope("prototype")
public class GitHelper {

    /**
     * 默认的配置
     */
    @Autowired
    private DefaultGitConf defaultGitConf;

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
            gitReset();
            gitPull();
        } else {
            gitClone();
        }
    }

    /**
     * 克隆项目
     */
    private void gitClone() {
        try {

            //克隆仓库
            CloneCommand cmd = Git.cloneRepository();


            //切换分支
            if (this.applicationGitInfo.getBranch() != null) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 重置
     */
    private void gitReset() {
        try {
            ResetCommand cmd = Git.open(this.gitWorkProjectFile).reset().setMode(ResetCommand.ResetType.HARD);
            Ref ref = cmd.call();
            log.info("rest : " + ref);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新项目
     */
    private void gitPull() {
        try {
            PullCommand cmd = Git.open(this.gitWorkProjectFile).pull();
            CredentialsProvider credentialsProvider = credentialsProvider();
            if (credentialsProvider != null) {
                cmd.setCredentialsProvider(credentialsProvider);
            }
            PullResult result = cmd.call();
            log.info("ret : " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private CredentialsProvider credentialsProvider() {
        //设置鉴权
        if (this.applicationGitInfo.getUserName() != null || this.defaultGitConf.getUserName() != null) {
            // 取出用户名与密码，优先取对应的应用配置，为空则取默认配置的
            String userName = this.applicationGitInfo.getUserName();
            if (userName == null) {
                userName = this.defaultGitConf.getUserName();
            }
            String passWord = this.applicationGitInfo.getPassWord();
            if (passWord == null) {
                passWord = this.defaultGitConf.getPassWord();
            }

            //设置git的权限
            CredentialsProvider cp = new UsernamePasswordCredentialsProvider(userName, passWord);
            return cp;
        }
        return null;
    }


}
