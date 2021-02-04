//package com.fast.build.helper.core.service;
//
//import com.fast.build.helper.core.conf.BuildGitConf;
//import com.fast.build.helper.core.conf.BuildTaskConf;
//import com.fast.build.helper.core.helper.GitApiHelper;
//import com.fast.build.helper.core.helper.GitHelper;
//import com.fast.build.helper.core.helper.MavenHelper;
//import com.fast.build.helper.core.helper.PathHelper;
//import com.fast.build.helper.core.model.ApplicationGitInfo;
//import com.fast.build.helper.core.model.ApplicationTask;
//import com.fast.build.helper.core.model.GitInfo;
//import com.fast.build.helper.core.model.UpdateBuildTask;
//import com.fast.build.helper.core.util.ApplicationHomeUtil;
//import com.fast.build.helper.core.util.JsonUtil;
//import com.fast.build.helper.core.util.PomXmlUtil;
//import lombok.SneakyThrows;
//import lombok.extern.java.Log;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.io.filefilter.IOFileFilter;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//
//import javax.annotation.PreDestroy;
//import java.io.File;
//import java.io.FileFilter;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Log
//@Service
//public class CoreService {
//
//    /**
//     * 默认的配置文件名
//     */
//    private static final String DefaultConfigFileName = "BuildAppliaction.json";
//
//    /**
//     * 默认的配置文件路径
//     */
//    private static final File ConfigFile = ApplicationHomeUtil.getResource(DefaultConfigFileName);
//
//    /**
//     * 应用任务，持久化json
//     */
//    private ApplicationTask applicationTask;
//
//
//    @Autowired
//    private BuildGitConf buildGitConf;
//
//    @Autowired
//    private BuildTaskConf buildTaskConf;
//
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Autowired
//    private PathHelper pathHelper;
//
//    @Autowired
//    private MavenHelper mavenHelper;
//
//    //随机的文件名，放在当前线程中
//    private ThreadLocal<String> randomFile = new ThreadLocal<>();
//
//
//    //线程池
//    private ExecutorService threadPool = Executors.newFixedThreadPool(1);
//
//    //用于备份文件名的生成
//    private final static SimpleDateFormat BackupNameDateFormat = new SimpleDateFormat("yyyyMMddHH");
//
//
//    /**
//     * git的实现
//     */
//    private GitApiHelper gitApiHelper;
//
//    @PreDestroy
//    private void shutdown() {
//        this.threadPool.shutdownNow();
//    }
//
//    @Autowired
//    private void init() {
//        for (GitApiHelper gitApiHelper : applicationContext.getBeansOfType(GitApiHelper.class).values()) {
//            if (gitApiHelper.gitType() == this.buildGitConf.getType()) {
//                this.gitApiHelper = gitApiHelper;
//                break;
//            }
//        }
//        if (this.gitApiHelper == null) {
//            throw new RuntimeException("未找到git的实现类型");
//        }
//    }
//
//
//    @Autowired
//    private void configCheck() {
//        Assert.hasText(buildTaskConf.getCoreProject(), "核心项目参数不能为空");
//    }
//
//
//    private UpdateBuildTask updateBuildTask = null;
//
//
//    /**
//     * 更新任务的状态
//     *
//     * @param updateBuildTask
//     */
//    public synchronized void setUpdateBuildTask(UpdateBuildTask updateBuildTask) {
//        this.updateBuildTask = updateBuildTask;
//    }
//
//
//    /**
//     * 备份所有的jar包
//     *
//     * @return 返回备份的名称
//     */
//    @SneakyThrows
//    public String backupBuild() {
//        String name = BackupNameDateFormat.format(new Date(System.currentTimeMillis()));
//        File sourceFile = this.pathHelper.getBuildPath();
//        File targetFile = this.pathHelper.getBackupPath(name);
//        FileUtils.copyDirectory(sourceFile, targetFile, new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                if (pathname.isDirectory()) {
//                    return true;
//                }
//                return FilenameUtils.getExtension(pathname.getName()).equalsIgnoreCase("jar");
//            }
//        }, true);
//
//        return name;
//    }
//
//
//    /**
//     * 删除备份目录
//     *
//     * @param name
//     * @return
//     */
//    public boolean removeBackup(String name) {
//
//
//        return false;
//    }
//
//
//    /**
//     * 入口
//     */
//    public synchronized UpdateBuildTask execute() {
//        if (this.updateBuildTask != null) {
//            return this.updateBuildTask;
//        }
//
//        //设置内存中的任务
//        setUpdateBuildTask(UpdateBuildTask.builder().createTime(System.currentTimeMillis()).build());
//
//        log.info("任务开始");
//        try {
//            threadPool.execute(() -> {
//                try {
//                    main();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return this.updateBuildTask;
//    }
//
//
//    /**
//     * 任务入口
//     */
//    private void main() throws Exception {
//
//        //随机的线程名
//        this.randomFile.set(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
//
//        //更新应用配置文件
//        updateApplicationInfos();
//
//        //获取需要编译的项目
//        Map<String, ApplicationGitInfo> needbuildProjects = getNeedbuildProject();
//        log.info("build : " + needbuildProjects.keySet());
//
//
//        updateAppProjectFromGit(new HashSet<String>() {{
//            add(buildTaskConf.getCoreProject());
//        }}, true);
//
//
//        //git本地仓库的克隆或更新
//        updateAppProjectFromGit(new HashSet<String>() {{
//            addAll(needbuildProjects.keySet());
//        }}, false);
//
//
//        //返回核心项目的路径
//        File corePath = copy2TempPathAndUpdateRootPom(needbuildProjects.keySet());
//
//
//        //调用maven,编译项目
//        try {
//            packageProject(corePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        //拷贝jar到打包目录
//        try {
//            copyJarsToBuildFile(corePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        //删除临时目录
//        try {
//            removeTempFile(getRootTempFile());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        //缓存配置
//        getBuildFilesInfo();
//
//
//        //释放内存资源
//        setUpdateBuildTask(null);
//        this.randomFile.remove();
//
//    }
//
//
//    /**
//     * 删除临时目录
//     */
//    private void removeTempFile(File corePath) {
//        try {
//            //清空该目录
//            FileUtils.cleanDirectory(corePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //删除目录
//        corePath.delete();
//    }
//
//
//    /**
//     * 拷贝jar包
//     */
//    private void copyJarsToBuildFile(File corePath) {
//
//
//        File applicationFile = getRootTempFile();
//
//        //扫描有多少模块
//        Collection<File> jarFiles = FileUtils.listFiles(applicationFile, new IOFileFilter() {
//            @Override
//            public boolean accept(File file) {
//                return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("jar");
//            }
//
//            @Override
//            public boolean accept(File dir, String name) {
//                return true;
//            }
//        }, new IOFileFilter() {
//            @Override
//            public boolean accept(File file) {
//                return true;
//            }
//
//            @Override
//            public boolean accept(File dir, String name) {
//                return true;
//            }
//        });
//
//
//        for (File jarFile : jarFiles) {
//            try {
//                File projectPath = new File(applicationFile.getAbsolutePath() + "/" + this.buildTaskConf.getCoreProject());
//                //相对路径
//                String relativePath = jarFile.getAbsolutePath().substring(projectPath.getAbsolutePath().length());
//                File target = new File(this.pathHelper.getBuildPath().getAbsolutePath() + "/" + relativePath);
//                FileUtils.copyFile(jarFile, target);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        log.info("[copy] :" + jarFiles);
//
//
//    }
//
//
//    /**
//     * 获取创建的文件
//     *
//     * @return
//     */
//    private Collection<File> getBuildFiles() {
//        return FileUtils.listFiles(this.pathHelper.getBuildPath(), new String[]{"jar"}, true);
//    }
//
//
//    /**
//     * @return
//     */
//    private Collection<File> _getBuildFilesInfo() {
////        Vector<FileInfo> fileInfos = new Vector<>();
////        for (File file : getBuildFiles()) {
////            FileInfo fileInfo = new FileInfo();
////            fileInfo.setFile(file);
////            fileInfo.setName(FilenameUtils.getName(file.getAbsolutePath()));
////            fileInfo.setLength(file.length());
////            fileInfo.setUpdateTime(file.lastModified());
////            fileInfos.add(fileInfo);
////        }
//        return new ArrayList<>(getBuildFiles());
//    }
//
//
//    public Collection<File> getBuildFilesInfo() {
//        return _getBuildFilesInfo();
//    }
//
//    /**
//     * 编译项目
//     */
//    private void packageProject(File corePath) {
//        log.info("开始打包 : " + corePath.getAbsolutePath());
//        this.mavenHelper.projectPackage(corePath);
//
//    }
//
//
//    /**
//     * 获取任务的临时文件
//     *
//     * @return
//     */
//    private File getRootTempFile() {
//        return ApplicationHomeUtil.getResource(this.buildTaskConf.getTempPath() + "/" + this.randomFile.get());
//    }
//
//
//    /**
//     * 拷贝到核心项目里，并修改核心项目中的pom
//     *
//     * @param appNames
//     * @return
//     */
//    private File copy2TempPathAndUpdateRootPom(Set<String> appNames) {
//        //随机目录
//        File targetFile = getRootTempFile();
//        //拷贝核心项目
//        File coreFile = copyDirectoryToTemp(pathHelper.getGitWorkProjectFile(this.buildTaskConf.getCoreProject()), new File(targetFile.getAbsolutePath() + "/" + this.buildTaskConf.getCoreProject()));
//        for (String appName : appNames) {
//            // git 的配置
//            ApplicationGitInfo applicationGitInfo = this.applicationTask.getApplications().get(appName);
//            if (applicationGitInfo == null) {
//                continue;
//            }
//
//            //取出原项目的路径
//            File gitWorkProjectFile = this.pathHelper.getGitWorkProjectFile(appName);
//
//            //复制并更新pom配置文件
//            updateProjectPom(gitWorkProjectFile, coreFile);
//        }
//        return coreFile;
//    }
//
//    /**
//     * 复制并更新pom配置文件
//     *
//     * @param gitWorkProjectFile
//     * @param corePath
//     */
//    private void updateProjectPom(File gitWorkProjectFile, File corePath) {
//
//
//        //扫描有多少模块
//        Collection<File> pomFiles = FileUtils.listFiles(gitWorkProjectFile, new IOFileFilter() {
//            @Override
//            public boolean accept(File file) {
//                return FilenameUtils.getName(file.getName()).equalsIgnoreCase("pom.xml");
//            }
//
//            @Override
//            public boolean accept(File dir, String name) {
//                return true;
//            }
//        }, new IOFileFilter() {
//            @Override
//            public boolean accept(File file) {
//                return true;
//            }
//
//            @Override
//            public boolean accept(File dir, String name) {
//                return true;
//            }
//        });
//
//
//        //核心项目
//        File coreProjectPomXml = new File(corePath.getAbsolutePath() + "/pom.xml");
//        try {
//            Set<String> modules = new HashSet<>();
//            for (File pomFile : pomFiles) {
//
//                //通过读取pom的方式，获取需要加入的模块名
//                String module = this.pathHelper.getProjectSavePath(pomFile);
//
//                //根据模块名构建需要复制到的目录结构
//                File appTargetFile = this.pathHelper.getTempAppModuleFile(corePath, module);
//
//                //开始复制文件
//                copyDirectoryToTemp(pomFile.getParentFile(), appTargetFile);
//
//
//                //如果是模块的声明则不加入编译中
//                if (!PomXmlUtil.hasModules(pomFile)) {
//                    modules.add(module);
//                }
//
//
//                log.info("[find] " + module);
//            }
//
//            //在核心项目中追加 模块
//            PomXmlUtil.append(coreProjectPomXml, modules);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    /**
//     * 拷贝项目文件到临时目录
//     *
//     * @return
//     */
//    private File copyDirectoryToTemp(File source, File target) {
//        //文件夹拷贝，过滤 git
//        try {
//            FileUtils.copyDirectory(source, target, new FileFilter() {
//                @Override
//                public boolean accept(File pathname) {
//                    return !pathname.getName().equalsIgnoreCase(".git");
//                }
//            });
//            return target;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    /**
//     * 克隆git
//     *
//     * @param appName
//     */
//    private void updateAppProjectFromGit(Set<String> appName, boolean force) {
//        if (appName == null || appName.size() == 0) {
//            log.info("无处理的项目");
//            return;
//        }
//
//        //利用线程池批量更新
//        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(this.buildTaskConf.getMaxPullGitThreadCount());
//
//        final CountDownLatch countDownLatch = new CountDownLatch(appName.size());
//        for (String name : appName) {
//            ApplicationGitInfo applicationGitInfo = this.applicationTask.getApplications().get(name);
//            if (force == false && applicationGitInfo == null) {
//                log.info("跳过不存在的app:" + name);
//                countDownLatch.countDown();
//                continue;
//            }
//
//
//            //为空构建一个配置
//            if (applicationGitInfo == null) {
//                applicationGitInfo = new ApplicationGitInfo();
//                applicationGitInfo.setUrl(buildGitConf.getHost() + "/" + this.buildTaskConf.getCoreProject());
//            }
//
//
//            final GitHelper gitHelper = this.applicationContext.getBean(GitHelper.class);
//            gitHelper.setAppName(name);
//            gitHelper.setApplicationGitInfo(applicationGitInfo);
//
//            fixedThreadPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        //clone
//                        gitHelper.gitProjectUpdate();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    //线程执行完成
//                    countDownLatch.countDown();
//                }
//            });
//
//
//        }
//        try {
//            countDownLatch.await();
//            fixedThreadPool.shutdownNow();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//
//    /**
//     * 构建默认的配置文件
//     */
//    private void updateApplicationInfos() throws Exception {
//
//
//        if (ConfigFile.exists()) {
//            applicationTask = JsonUtil.toObject(FileUtils.readFileToString(ConfigFile, "UTF-8"), ApplicationTask.class);
//        } else {
//            applicationTask = new ApplicationTask();
//            applicationTask.setApplications(new HashMap<>());
//        }
//
//        //更新应用信息
//        updateApplicationInfoMap(applicationTask.getApplications());
//
//
//        //json序列化到磁盘上
//        FileUtils.writeStringToFile(ConfigFile, JsonUtil.toJson(applicationTask, true), "UTF-8");
//
//    }
//
//
//    /**
//     * 获取需要编译的项目
//     *
//     * @return
//     */
//    private Map<String, ApplicationGitInfo> getNeedbuildProject() {
//        Map<String, ApplicationGitInfo> items = new HashMap<>();
//        for (Map.Entry<String, ApplicationGitInfo> entry : this.applicationTask.getApplications().entrySet()) {
//            ApplicationGitInfo val = entry.getValue();
//            if (val.isBuild()) {
//                items.put(entry.getKey(), entry.getValue());
//            }
//        }
//        return items;
//    }
//
//
//    /**
//     * 更新应用信息
//     *
//     * @param applicationInfoMap
//     */
//    private void updateApplicationInfoMap(Map<String, ApplicationGitInfo> applicationInfoMap) {
//        Map<String, GitInfo> ret = this.gitApiHelper.userRepos();
//        for (Map.Entry<String, GitInfo> entry : ret.entrySet()) {
//            if (applicationInfoMap.containsKey(entry.getKey())) {
//                continue;
//            }
//
//            log.info("增加新项目: " + entry.getKey());
//            ApplicationGitInfo applicationGitInfo = new ApplicationGitInfo();
//            BeanUtils.copyProperties(entry.getValue(), applicationGitInfo);
//            //更新未配置过的
//            applicationInfoMap.put(entry.getKey(), applicationGitInfo);
//        }
//
//    }
//}
//
//
