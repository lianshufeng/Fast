package com.fast.build.helper.core.helper;

import com.fast.build.helper.core.conf.BuildGitConf;
import com.fast.build.helper.core.conf.BuildTaskConf;
import com.fast.build.helper.core.util.ApplicationHomeUtil;
import com.fast.build.helper.core.util.PomXmlUtil;
import com.fast.build.helper.core.util.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

@Component
public class PathHelper {
    @Autowired
    private BuildGitConf gitConf;

    @Autowired
    private BuildTaskConf buildTaskConf;

    @Autowired
    private void initBuildPath() {
        File buildFile = getBuildPath();
        if (!buildFile.exists()) {
            buildFile.mkdirs();
        }
    }

    /**
     * 获取git的工作空间
     *
     * @return
     */
    public File getGitWorkProjectFile(String appName) {
        return new File(ApplicationHomeUtil.getResource(this.buildTaskConf.getProjectPath()).getAbsolutePath() + "/" + appName);
    }

    /**
     * 获取Git工作空间的位置
     *
     * @return
     */
    public File getGitWorkProjectRootFile() {
        return getGitWorkProjectFile("");
    }


    /**
     * 获取临时项目的app名称
     *
     * @param coreFile
     * @return
     */
    public File getTempAppModuleFile(File coreFile, String module) {
        return new File(coreFile.getAbsolutePath() + "/" + module);
    }


    /**
     * 获取临时的核心项目的应用目录
     *
     * @param coreProjectFile
     * @return
     */
    public File getTempCoreProjectApplicationsFile(File coreProjectFile) {
        return new File(coreProjectFile.getAbsolutePath() + "/" + this.buildTaskConf.getApplicationPath() + "/");
    }


    /**
     * 取编译目录
     *
     * @return
     */
    public File getBuildPath() {
        return ApplicationHomeUtil.getResource("resources/" + this.buildTaskConf.getBuildPath());
    }

    /**
     * 获取资源路径
     *
     * @return
     */
    public File getResourcePath(String fileName) {
        return ApplicationHomeUtil.getResource("resources/" + fileName);
    }


    /**
     * 取备份目录
     *
     * @return
     */
    public File getBackupPath(String path) {
        return ApplicationHomeUtil.getResource("resources/" + this.buildTaskConf.getBackupResourceName() + "/" + path);
    }


    /**
     * 获取项目的存放路径
     *
     * @return
     */
    public String getProjectSavePath(File pomFile) throws Exception {
        //取深度
        int depth = PomXmlUtil.getAppDepth(pomFile);
        if (depth == -1) {
            return joinAppcationSavePath(pomFile.getParentFile().getName());
        }


        //去掉默认配置的那项目录  , 默认 : application
        depth--;


        File f = pomFile.getParentFile();
        ArrayList<String> ret = new ArrayList<>();
        for (int i = 0; i < depth; i++) {
            ret.add(0, f.getName());
            f = f.getParentFile();
        }

        //需要加入默认的应用路径
        return joinAppcationSavePath(TextUtil.join(ret.toArray(new String[0]), "/"));
    }

    /**
     * 加入应用路径
     *
     * @param val
     * @return
     */
    private String joinAppcationSavePath(String val) {
        return this.buildTaskConf.getApplicationPath() + "/" + val;
    }


}
