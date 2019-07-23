package com.fast.build.helper.core.helper;

import com.fast.build.helper.core.util.ApplicationHomeUtil;
import com.fast.build.helper.core.util.ZipUtil;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Component
public class MavenHelper {


    /**
     * 更换版本只需要更换此文件名与zip包即可
     */
    private final static String FileName = "apache-maven-3.6.1-bin.zip";

    private final static File mavenPath = ApplicationHomeUtil.getResource(FileName.replaceAll("-bin.zip", ""));

    /**
     * 获取当前运行环境
     */
    @Value("${spring.profiles.active}")
    private String profilesActive;


    @Autowired
    private void init() throws IOException {
        if (!mavenPath.exists() || mavenPath.listFiles().length == 0) {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("tools/" + FileName);
            ZipUtil.unZipFile(inputStream, ApplicationHomeUtil.getHomeFile());
            inputStream.close();
        }
    }


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
                add("-P");
                add(profilesActive);
            }
        });


        try {
            Invoker invoker = new DefaultInvoker();
            //设置mavenHome
            invoker.setMavenHome(mavenPath);
            invoker.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
