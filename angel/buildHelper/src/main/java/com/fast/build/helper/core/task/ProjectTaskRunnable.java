package com.fast.build.helper.core.task;

import com.fast.build.helper.core.conf.BuildTaskConf;
import com.fast.build.helper.core.helper.*;
import com.fast.build.helper.core.model.ApplicationGitInfo;
import com.fast.build.helper.core.model.MavenProjectRelationItem;
import com.fast.build.helper.core.model.ProjectItem;
import com.fast.build.helper.core.model.TaskProgress;
import com.fast.build.helper.core.service.TaskService;
import com.fast.build.helper.core.type.TaskStatus;
import com.fast.build.helper.core.util.JsonUtil;
import com.fast.build.helper.core.util.PathUtil;
import com.fast.build.helper.core.util.PomXmlUtil;
import com.fast.dev.core.util.text.TextUtil;
import com.fast.dev.core.util.token.TokenUtil;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
public class ProjectTaskRunnable implements Runnable {

    //构建项目的临时目录
    private File projectTmpFile = null;

    @Setter
    private TaskProgress taskProgress;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BuildTaskConf buildTaskConf;

    @Autowired
    private ProjectHelper projectHelper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PathHelper pathHelper;

    @Autowired
    private MavenProjectHelper mavenProjectHelper;

    @Autowired
    private DockerHelper dockerHelper;


    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            finishTask();
        }
    }


    /**
     * 完成任务
     */
    private void finishTask() {
        //删除临时目录
        setProcess(null, TaskStatus.Clean, 0f);
        this.cleanProjectFile();


        setProcess(null, TaskStatus.Finish, 0f);
        //删除内存任务
        this.taskService.removeTask(taskProgress);
    }

    /**
     * 执行任务
     */
    private void execute() {
        log.info("执行任务 : {}", List.of(this.taskProgress.getProjectTask().getProjectName()));

        if (this.taskProgress.getProjectTask().isBuild()) {
            executeBuild();
        }

        if (this.taskProgress.getProjectTask().isRestart()) {
            executeRestart();
        }


    }


    //执行编译
    private void executeBuild() {
        //更新仓库
        updateGitProjects();


        //分析 maven项目的依赖关系
        List<MavenProjectRelationItem.MavenProject> mavenProjectList = findMavenProjectRelation();

        //构建编译环境
        makeBuildProjects(mavenProjectList);


        //更新maven项目的关系
        updateMavenProjectRelation(mavenProjectList);


        //开始构建项目
        executeBuildProject();


        //拷贝到资源目录
        copyBuildToResourcePath();

    }

    /**
     * 拷贝编译到资源目录
     */
    private void copyBuildToResourcePath() {

        setProcess(null, TaskStatus.CopyResources, 0f);

        //应用存放的路径
        File appResourceFile = this.pathHelper.getResourcePath(this.buildTaskConf.getAppResourceName());

        Optional.ofNullable(this.projectTmpFile).ifPresent((file) -> {
            Collection<File> jars = FileUtils.listFiles(file, new String[]{"jar"}, true);
            jars.parallelStream().forEach((f) -> {
                String fileName = FilenameUtils.getName(f.getAbsolutePath());
                File targetFile = new File(appResourceFile.getAbsolutePath() + "/" + fileName);
                try {
                    FileUtils.copyFile(f, targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            });
        });
    }


    /**
     * 执行构建项目
     */
    private void executeBuildProject() {
        MavenHelper mavenHelper = this.applicationContext.getBean(MavenHelper.class);
        mavenHelper.setTaskProgress(this.taskProgress);
        mavenHelper.projectPackage(this.projectTmpFile);
    }


    //执行重启
    private void executeRestart() {
        setProcess(null, TaskStatus.ReStartContainer, 0f);
        Optional.ofNullable(this.taskProgress.getProjectTask().getProjectName()).ifPresent((projectNames) -> {
            for (String projectName : projectNames) {
                Optional.ofNullable(this.projectHelper.getProjectItem(projectName)).ifPresent((project) -> {
                    try {
                        Optional.ofNullable(project.getContainerName()).ifPresent((containerName)->{
                            this.dockerHelper.restart(containerName);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }


    /**
     * 更新maven项目的关系
     */
    private void updateMavenProjectRelation(List<MavenProjectRelationItem.MavenProject> mavenProjectList) {
        File pomFile = new File(this.projectTmpFile.getAbsolutePath() + "/pom.xml");

        Set<String> modules = new HashSet<>() {{
            add("super/PConfigure");
            add("super/PParent");
            add("super/PCore");
        }};

        final String projectRootFile = PathUtil.format(this.pathHelper.getGitWorkProjectRootFile());
        //计算出模块名
        Set<String> relations = mavenProjectList.stream().filter((project) -> {
            //过滤配置项目模块的pom文件
            return !PomXmlUtil.isModulesManagerFile(project.getPomFile());
        }).map((project) -> {
            String fileName = PathUtil.format(project.getPomFile().getParentFile());
            fileName = fileName.substring(projectRootFile.length() + 1, fileName.length());

            String[] relation = fileName.split("/");

            //获取项目名
            String projectName = getProjectName(project.getPomFile());
            //是否基础框架，基础框架则仅去掉项目名
            if (this.buildTaskConf.getCoreProject().equals(projectName)) {
                //去除前两级目录(项目名)
                relation = ArrayUtils.remove(relation, 0);
                relation = ArrayUtils.remove(relation, 0);
            } else {
                //去掉第一级目录(项目的所属人)
                relation = ArrayUtils.remove(relation, 0);
                //其他项目则需要加 application/
                relation = ArrayUtils.insert(0, relation, this.buildTaskConf.getApplicationPath());
            }
            return TextUtil.join(relation, "/");
        }).collect(Collectors.toSet());
        modules.addAll(relations);

        //排序
        List<String> modulesSort = new ArrayList<>(modules);
        Collections.sort(modulesSort, (it1, it2) -> {
            return it2.compareTo(it1);
        });

        PomXmlUtil.setmodules(pomFile, modulesSort);
    }


    /**
     * 清空临时目录
     */
    @SneakyThrows
    private void cleanProjectFile() {
        if (projectTmpFile == null) {
            return;
        }

        if (projectTmpFile.exists()) {
            log.info("cleanProjectFile : {}", projectTmpFile.getAbsolutePath());
            FileUtils.cleanDirectory(projectTmpFile);
            projectTmpFile.delete();
        }
    }


    //开始分析maven项目的关系
    @SneakyThrows
    private List<MavenProjectRelationItem.MavenProject> findMavenProjectRelation() {
        //分析所有的maven项目
        List<MavenProjectRelationItem> mavenProjectRelations = this.mavenProjectHelper.listMavenProjectRelationItem(this.pathHelper.getGitWorkProjectRootFile());

        //分析项目依赖
        return this.mavenProjectHelper.analysis(mavenProjectRelations, this.taskProgress.getProjectTask().getProjectName(), this.taskProgress.getProjectTask().getArtifact());
    }


    /**
     * 更新项目
     */
    private void updateGitProjects() {
        this.projectHelper.getItems().values().parallelStream().forEach((projectItem) -> {
            if (projectItem.getGit() != null) {
                updateGitProject(projectItem, 0);
            }
        });
    }


    /**
     * 构建编译项目
     */

    private void makeBuildProjects(List<MavenProjectRelationItem.MavenProject> mavenProjectList) {
        setProcess(null, TaskStatus.MakeBuildProjects, 0.01f);

        this.projectTmpFile = new File(System.getProperty("java.io.tmpdir") + "/build/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())) + "_" + TokenUtil.create());
        this.projectTmpFile.mkdirs();

        //核心项目
        File coreFile = this.pathHelper.getGitWorkProjectFile(this.buildTaskConf.getCoreProject());
        //拷贝项目到临时的目录
        copyProjectToBuildFile(coreFile, coreFile, projectTmpFile.getAbsolutePath(), new File(coreFile.getAbsolutePath() + "/.git"));

        for (int i = 0; i < mavenProjectList.size(); i++) {
            MavenProjectRelationItem.MavenProject mavenProject = mavenProjectList.get(i);
            setProcess(mavenProject.getArtifactId(), TaskStatus.MakeBuildProjects, 0.01f * (i + 1));

            final File pomFile = mavenProject.getPomFile();


            //取出项目名
            String projectName = getProjectName(pomFile);
            //如果当前就是核心项目则过滤不需要拷贝
            if (projectName.equalsIgnoreCase(this.buildTaskConf.getCoreProject())) {
                continue;
            }

            //根项目的路径
            File rootProjectFile = new File(this.pathHelper.getGitWorkProjectRootFile().getAbsolutePath() + "/" + projectName);

            //构建编译目录的地址
            File buildProjectFile = buildProjectFile(projectName);

            //拷贝项目到编译目录
            copyProjectToBuildFile(pomFile.getParentFile(), rootProjectFile, buildProjectFile.getAbsolutePath(), new File(pomFile.getParentFile().getAbsolutePath() + "/.git"));

        }


    }


    /**
     * 构建编译的目录
     *
     * @param projectName
     * @return
     */
    private File buildProjectFile(String projectName) {
        //取消第一级项目名
        return new File(PathUtil.format(this.projectTmpFile) + "/" + this.buildTaskConf.getApplicationPath() + "/" + projectName.split("/")[1]);
    }


    /**
     * 取出项目名
     *
     * @param pomFile
     * @return
     */
    private String getProjectName(File pomFile) {
        //取出代码仓库路径
        String rootFilePath = PathUtil.format(this.pathHelper.getGitWorkProjectRootFile());
        //pom的路径
        String pomFilePath = PathUtil.format(pomFile);
        //相对路径
        String relativePath = pomFilePath.substring(rootFilePath.length() + 1, pomFilePath.length());
        String[] paths = relativePath.split("/");
        return paths[0] + "/" + paths[1];
    }


    @SneakyThrows
    private static void copyProjectToBuildFile(File projectFile, File projectRootFile, String path, File ignoreFile) {

        //根项目路径，格式化
        final String projectRootFilePath = PathUtil.format(projectRootFile);


        //忽略的文件名
        final String ignoreFileName = PathUtil.format(ignoreFile);


        List<File> sourceFiles = FileUtils.listFiles(projectFile, new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return true;
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
        }).stream().filter((file) -> {
            if (ignoreFileName == null) {
                return true;
            }
            String fileName = PathUtil.format(file);
            return !(fileName.length() > ignoreFileName.length() && fileName.substring(0, ignoreFileName.length()).equalsIgnoreCase(ignoreFileName));
        }).collect(Collectors.toList());


        //拷贝文件
        sourceFiles.parallelStream().forEach((file) -> {
            String filePath = PathUtil.format(file);

            //取出相对路径
            String relativePath = filePath.substring(projectRootFilePath.length() + 1, filePath.length());
            //构建目标文件
            File targetFile = new File(path + "/" + relativePath);

            try {
                FileUtils.copyFile(file, targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }


    /**
     * 更新GIT项目
     */
    private void updateGitProject(ProjectItem projectItem, float offSet) {

        synchronized (projectItem.getName()) {
            setProcess(projectItem.getName(), TaskStatus.UpdateGit, offSet);

            GitHelper gitHelper = this.applicationContext.getBean(GitHelper.class);

            //构建参数
            ApplicationGitInfo applicationGitInfo = new ApplicationGitInfo();
            applicationGitInfo.setUrl(projectItem.getGit().getUrl());
            applicationGitInfo.setBranch(projectItem.getBranch());
            gitHelper.setApplicationGitInfo(applicationGitInfo);
            gitHelper.setAppName(projectItem.getName());


            //开始更新仓库
            gitHelper.gitProjectUpdate();
        }


    }


    /**
     * 计算出当前百分比,并缩小百倍
     *
     * @param index
     * @param total
     * @return
     */
    private static float getOffSet(long index, long total) {
        return new BigDecimal(index).divide(new BigDecimal(total), RoundingMode.HALF_UP).floatValue() * 0.01f;
    }

    /**
     * 设置进度
     *
     * @param status
     */
    private void setProcess(String title, TaskStatus status, float offSet) {
        if (!StringUtils.hasText(title)) {
            title = JsonUtil.toJson(this.taskProgress.getProjectTask().getProjectName());
        }
        float process = status.getProcess() + offSet;
        log.info("{} -> {} : {}", title, status, process);

        this.taskProgress.setStatus(status);
        this.taskProgress.setProcess(process);
        this.taskProgress.setMsg(status.getMsg());
    }


}
