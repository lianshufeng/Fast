package com.fast.build.helper.core.helper;

import com.fast.build.helper.core.conf.MavenConf;
import com.fast.build.helper.core.model.TaskProgress;
import com.fast.build.helper.core.type.TaskStatus;
import com.fast.build.helper.core.util.JsonUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;


@Slf4j
@Component
@Scope("prototype")
public class MavenHelper {


    @Setter
    private TaskProgress taskProgress;

    /**
     * 获取当前运行环境
     */
    @Value("${spring.profiles.active:dev}")
    private String profilesActive;


    @Autowired
    private MavenConf mavenConf;


    private long startTime = System.currentTimeMillis();

    /**
     * 项目编译
     *
     * @param corePathFile
     */
    public void projectPackage(File corePathFile) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(corePathFile.getAbsoluteFile() + "/pom.xml"));
        request.setGoals(new ArrayList<String>() {
            {
                add("package");
//                add("-X"); // debug模式
                add("-P");
                add(profilesActive);
            }
        });


        try {
            Invoker invoker = new DefaultInvoker();
            //日志
            invoker.setOutputHandler((line) -> {
                logInfo(line);
            });

            //设置mavenHome
            invoker.setMavenHome(new File(mavenConf.getHome()));
            invoker.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 打印日志
     *
     * @param line
     */
    private void logInfo(String line) {
        log.info(line);
        this.taskProgress.setStatus(TaskStatus.CompileProject);
        this.taskProgress.setProcess(TaskStatus.CompileProject.getProcess() + (System.currentTimeMillis() - startTime) * 0.000001f);
        this.taskProgress.setMsg(line);
    }
}


