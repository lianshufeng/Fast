package com.fast.build.helper.core.service;

import com.fast.build.helper.core.conf.DefaultGitConf;
import com.fast.build.helper.core.helper.GitApiHelper;
import com.fast.build.helper.core.helper.GitHelper;
import com.fast.build.helper.core.helper.MavenHelper;
import com.fast.build.helper.core.helper.PathHelper;
import com.fast.build.helper.core.model.ApplicationGitInfo;
import com.fast.build.helper.core.model.ApplicationTask;
import com.fast.build.helper.core.model.GitInfo;
import com.fast.build.helper.core.util.ApplicationHomeUtil;
import com.fast.build.helper.core.util.JsonUtil;
import com.fast.build.helper.core.util.PathUtil;
import com.fast.build.helper.core.util.PomXmlUtil;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
@Service
public class CoreService {

    /**
     * 默认的配置文件名
     */
    private static final String DefaultConfigFileName = "BuildAppliaction.json";

    /**
     * 默认的配置文件路径
     */
    private static final File ConfigFile = ApplicationHomeUtil.getResource(DefaultConfigFileName);


    /**
     * 应用任务，持久化json
     */
    private ApplicationTask applicationTask;


    @Autowired
    private DefaultGitConf defaultGitConf;


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PathHelper pathHelper;

    @Autowired
    private MavenHelper mavenHelper;

    //随机的文件名
    private String rFileName = new SimpleDateFormat("yyyymmddHHmmss").format(new Date(System.currentTimeMillis()));

    /**
     * git的实现
     */
    private GitApiHelper gitApiHelper;


    @Autowired
    private void init() {
        for (GitApiHelper gitApiHelper : applicationContext.getBeansOfType(GitApiHelper.class).values()) {
            if (gitApiHelper.gitType() == this.defaultGitConf.getType()) {
                this.gitApiHelper = gitApiHelper;
                break;
            }
        }
        if (this.gitApiHelper == null) {
            throw new RuntimeException("未找到git的实现类型");
        }

    }


    /**
     * 入口
     *
     * @param args
     */
    public void execute(String[] args) {
        log.info("任务开始");
        try {
            main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("任务结束");
    }


    /**
     * 任务入口
     *
     * @param args
     */
    private void main(String[] args) throws Exception {
        //更新应用配置文件
        updateApplicationInfos();

        //获取需要编译的项目
        Map<String, ApplicationGitInfo> needbuildProjects = getNeedbuildProject();
        log.info("build : " + needbuildProjects.keySet());


        //git本地仓库的克隆或更新
        updateAppProjectFromGit(new HashSet<String>() {{
            add(defaultGitConf.getCoreProject());
            addAll(needbuildProjects.keySet());
        }});


        //拷贝项目到临时目录中
//        File corePath = copy2TempPath(needbuildProjects.keySet());
//        //修改pom模块信息
//        updateProjectPom(corePath);


        //返回核心项目的路径
        File corePath = copy2TempPathAndUpdateRootPom(needbuildProjects.keySet());


        //调用maven,编译项目
        try {
            packageProject(corePath);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //拷贝jar到打包目录
        try {
            copyJarsToBuildFile(corePath);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //删除临时目录
        try {
            removeTempFile(getRootTempFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 删除临时目录
     */
    private void removeTempFile(File corePath) {
        try {
            //清空该目录
            FileUtils.cleanDirectory(corePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //删除目录
        corePath.delete();
    }


    /**
     * 拷贝jar包
     */
    private void copyJarsToBuildFile(File corePath) {
        File applicationFile = this.pathHelper.getTempCoreProjectApplicationsFile(corePath);
        if (!applicationFile.exists()) {
            return;
        }

        //扫描有多少模块
        Collection<File> jarFiles = FileUtils.listFiles(applicationFile, new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("jar");
            }

            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        }, new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return true;
            }

            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });


        for (File jarFile : jarFiles) {
            try {
                //暂时不考虑文件名重复的情况
                File target = new File(this.pathHelper.getBuildPath().getAbsolutePath() + "/" + this.rFileName + "/" + FilenameUtils.getName(jarFile.getAbsolutePath()));
                FileUtils.copyFile(jarFile, target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        log.info("[copy] :" + jarFiles);


    }


    /**
     * 编译项目
     */
    private void packageProject(File corePath) {
        log.info("开始打包 : " + corePath.getAbsolutePath());
        this.mavenHelper.projectPackage(corePath);

    }


    /**
     * 获取任务的临时文件
     *
     * @return
     */
    private File getRootTempFile() {
        return ApplicationHomeUtil.getResource(this.defaultGitConf.getTempPath() + "/" + rFileName);
    }


    /**
     * 拷贝到核心项目里，并修改核心项目中的pom
     *
     * @param appNames
     * @return
     */
    private File copy2TempPathAndUpdateRootPom(Set<String> appNames) {
        //随机目录
        File targetFile = getRootTempFile();
        //拷贝核心项目
        File coreFile = copyDirectoryToTemp(pathHelper.getGitWorkProjectFile(this.defaultGitConf.getCoreProject()), new File(targetFile.getAbsolutePath() + "/" + this.defaultGitConf.getCoreProject()));
        for (String appName : appNames) {
            // git 的配置
            ApplicationGitInfo applicationGitInfo = this.applicationTask.getApplications().get(appName);
            if (applicationGitInfo == null) {
                continue;
            }

            //取出原项目的路径
            File gitWorkProjectFile = this.pathHelper.getGitWorkProjectFile(appName);

            //复制并更新pom配置文件
            updateProjectPom(gitWorkProjectFile, coreFile);
        }
        return coreFile;
    }

    /**
     * 复制并更新pom配置文件
     *
     * @param gitWorkProjectFile
     * @param corePath
     */
    private void updateProjectPom(File gitWorkProjectFile, File corePath) {


        //扫描有多少模块
        Collection<File> pomFiles = FileUtils.listFiles(gitWorkProjectFile, new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return FilenameUtils.getName(file.getName()).equalsIgnoreCase("pom.xml");
            }

            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        }, new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return true;
            }

            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });


        //核心项目
        File coreProjectPomXml = new File(corePath.getAbsolutePath() + "/pom.xml");
        try {
            Set<String> modules = new HashSet<>();
            for (File pomFile : pomFiles) {

                //通过读取pom的方式，获取需要加入的模块名
                String module = this.pathHelper.getProjectSavePath(pomFile);

                //根据模块名构建需要复制到的目录结构
                File appTargetFile = this.pathHelper.getTempAppModuleFile(corePath, module);

                //开始复制文件
                copyDirectoryToTemp(pomFile.getParentFile(), appTargetFile);

                modules.add(module);

                log.info("[find] " + module);
            }

            //在核心项目中追加 模块
            PomXmlUtil.append(coreProjectPomXml, modules);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 拷贝项目到临时目录里，返回核心项目
     */
//    private File copy2TempPath(Set<String> appNames) {
//        //随机目录
//        File targetFile = ApplicationHomeUtil.getResource(this.defaultGitConf.getTempPath() + "/" + rFileName);
//
//        //拷贝核心项目
//        File coreFile = copyDirectoryToTemp(pathHelper.getGitWorkProjectFile(this.defaultGitConf.getCoreProject()), new File(targetFile.getAbsolutePath() + "/" + this.defaultGitConf.getCoreProject()));
//
//        log.info("[core] " + coreFile);
//
//        for (String appName : appNames) {
//            // git 的配置
//            ApplicationGitInfo applicationGitInfo = this.applicationTask.getApplications().get(appName);
//            if (applicationGitInfo == null) {
//                continue;
//            }
//
//            //取出原项目的路径
//            File gitWorkProjectFile = this.pathHelper.getGitWorkProjectFile(appName);
//            File appTargetFile = getAppProjecTarget(coreFile, appName);
//            log.info("[project] " + gitWorkProjectFile + " -> " + coreFile);
//            copyDirectoryToTemp(gitWorkProjectFile, appTargetFile);
//        }
//
//        return coreFile;
//    }


    /**
     * 拷贝项目文件到临时目录
     *
     * @return
     */
    private File copyDirectoryToTemp(File source, File target) {
        //文件夹拷贝，过滤 git
        try {
            FileUtils.copyDirectory(source, target, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.getName().equalsIgnoreCase(".git");
                }
            });
            return target;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 克隆git
     *
     * @param appName
     */
    private void updateAppProjectFromGit(Set<String> appName) {
        if (appName == null || appName.size() == 0) {
            log.info("无处理的项目");
            return;
        }

        //利用线程池批量更新
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(this.defaultGitConf.getMaxPullGitThreadCount());

        final CountDownLatch countDownLatch = new CountDownLatch(appName.size());
        for (String name : appName) {
            ApplicationGitInfo applicationGitInfo = this.applicationTask.getApplications().get(name);
            if (applicationGitInfo == null) {
                log.info("跳过不存在的app:" + name);
                continue;
            }
            final GitHelper gitHelper = this.applicationContext.getBean(GitHelper.class);
            gitHelper.setAppName(name);
            gitHelper.setApplicationGitInfo(applicationGitInfo);

            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //clone
                        gitHelper.gitProjectUpdate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //线程执行完成
                    countDownLatch.countDown();
                }
            });


        }
        try {
            countDownLatch.await();
            fixedThreadPool.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 构建默认的配置文件
     */
    private void updateApplicationInfos() throws Exception {


        if (ConfigFile.exists()) {
            applicationTask = JsonUtil.toObject(FileUtils.readFileToString(ConfigFile, "UTF-8"), ApplicationTask.class);
        } else {
            applicationTask = new ApplicationTask();
            applicationTask.setApplications(new HashMap<>());
        }

        //更新应用信息
        updateApplicationInfoMap(applicationTask.getApplications());


        //json序列化到磁盘上
        FileUtils.writeStringToFile(ConfigFile, JsonUtil.toJson(applicationTask, true), "UTF-8");

    }


    /**
     * 获取需要编译的项目
     *
     * @return
     */
    private Map<String, ApplicationGitInfo> getNeedbuildProject() {
        Map<String, ApplicationGitInfo> items = new HashMap<>();
        for (Map.Entry<String, ApplicationGitInfo> entry : this.applicationTask.getApplications().entrySet()) {
            ApplicationGitInfo val = entry.getValue();
            if (val.isBuild()) {
                items.put(entry.getKey(), entry.getValue());
            }
        }
        return items;
    }


    /**
     * 更新应用信息
     *
     * @param applicationInfoMap
     */
    private void updateApplicationInfoMap(Map<String, ApplicationGitInfo> applicationInfoMap) {
        Map<String, GitInfo> ret = this.gitApiHelper.userRepos();
        for (Map.Entry<String, GitInfo> entry : ret.entrySet()) {
            if (applicationInfoMap.containsKey(entry.getKey())) {
                continue;
            }

            log.info("增加新项目: " + entry.getKey());
            ApplicationGitInfo applicationGitInfo = new ApplicationGitInfo();
            BeanUtils.copyProperties(entry.getValue(), applicationGitInfo);
            //更新未配置过的
            applicationInfoMap.put(entry.getKey(), applicationGitInfo);
        }

    }
}


