package com.fast.build.helper.core.helper;

import com.fast.build.helper.core.model.MavenProjectRelationItem;
import com.fast.build.helper.core.util.PomXmlUtil;
import com.fast.dev.core.util.text.TextUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MavenProjectHelper {


    /**
     * 分析出项目里需要依赖的模块
     *
     * @param mavenProjectRelations
     * @param fileProjectNames
     * @return
     */
    public List<MavenProjectRelationItem.MavenProject> analysis(final List<MavenProjectRelationItem> mavenProjectRelations, String[] fileProjectNames, String[] filter) {
        //todo key 存 groupId + artifactId , 如果存在这个对象则无需在分析，不存在则递归分析依赖
        final Map<String, MavenProjectRelationItem.MavenProject> ret = new HashMap<>();

        //转换为map对象
        final Map<String, MavenProjectRelationItem> fileProjectMap = new HashMap<>();
        mavenProjectRelations.forEach((it) -> {
            fileProjectMap.put(it.getFileProjectName(), it);
        });


        //转换为maven项目
        final Map<String, MavenProjectRelationItem.MavenProject> mavenProjects = toMavenProject(mavenProjectRelations);


        //遍历需要分析的项目
        for (String fileProjectName : fileProjectNames) {
            //分析当前项目下的所有maven项目依赖的模块
            MavenProjectRelationItem mavenProjectRelationItem = getItem(fileProjectMap, fileProjectName);


            //分析文件项目里所有maven项目的依赖
            mavenProjectRelationItem.getMavenProjects().stream().filter((it) -> {
                //过滤仅需要分析的项目
                return filter == null || filter.length == 0 ? true : Arrays.binarySearch(filter, it.toSimpleName()) > -1;
            }).forEach((it) -> {
                //追加到maven的依赖关系里
                appendMavenProjectRelationRelationItem(ret, mavenProjects, it);
            });


        }

        return new ArrayList<>(ret.values());
    }


    private static void appendMavenProjectRelationRelationItem(final Map<String, MavenProjectRelationItem.MavenProject> ret, Map<String, MavenProjectRelationItem.MavenProject> source, MavenProjectRelationItem.MavenProject mavenProject) {
        //查询maven项目的所有依赖模块
        List<MavenProjectRelationItem.MavenProject> mavenProjectList = findMavenRelationProject(source, mavenProject);

        //递归分析关系
        mavenProjectList.forEach((it) -> {
            //如果依赖的模块没有加入到结果集中，则需要先查询该模块的依赖关系在加入
            if (!ret.containsKey(it.toSimpleName())) {
                appendMavenProjectRelationRelationItem(ret, source, it);
                ret.put(it.toSimpleName(), it);
            }
        });


        if (!ret.containsKey(mavenProject.toSimpleName())) {
            ret.put(mavenProject.toSimpleName(), mavenProject);
        }

    }


    /**
     * 查询Maven项目依赖的模块
     */
    private static List<MavenProjectRelationItem.MavenProject> findMavenRelationProject(final Map<String, MavenProjectRelationItem.MavenProject> sources, MavenProjectRelationItem.MavenProject mavenProject) {
        final Map<String, MavenProjectRelationItem.MavenProject> ret = new HashMap<>();
        Optional.ofNullable(mavenProject.getDependency()).ifPresent((dependencys) -> {
            dependencys.forEach((dependencyMavenProject) -> {
                MavenProjectRelationItem.MavenProject mp = sources.get(dependencyMavenProject.toSimpleName());
                if (mp != null && !ret.containsKey(mp.toSimpleName())) {
                    ret.put(mp.toSimpleName(), mp);
                }
            });
        });
        return new ArrayList<>(ret.values());
    }


    /**
     * 转换为简易的关系
     *
     * @return
     */
    private static Map<String, MavenProjectRelationItem.MavenProject> toMavenProject(final Collection<MavenProjectRelationItem> mavenProjectRelations) {
        Map<String, MavenProjectRelationItem.MavenProject> ret = new HashMap<>();
        mavenProjectRelations.stream().forEach((it) -> {
            it.getMavenProjects().forEach((mavenProject) -> {
                ret.put(mavenProject.toSimpleName(), mavenProject);
            });
        });
        return ret;
    }


    /**
     * 开始分析maven项目
     *
     * @param projectFile
     * @return
     */
    public List<MavenProjectRelationItem> listMavenProjectRelationItem(File projectFile) {
        Collection<File> pomFiles = findAllPomFile(projectFile);
        Map<String, MavenProjectRelationItem> result = new HashMap<>();
        pomFiles.stream().map((pomFile) -> {
            //取出相对路径
            final String relativePath[] = getRelativePath(projectFile, pomFile).split("/");
            if (relativePath.length < 3) {
                return null;
            }
            return relativePath;
        }).filter((it) -> {
            //过滤不能为为null的路径
            return it != null;
        }).forEach((relativePath) -> {
            final File pomFile = new File(projectFile.getAbsolutePath() + "/" + TextUtil.join(relativePath, "/"));

            //项目文件名
            String fileProjectName = relativePath[0] + "/" + relativePath[1];
            MavenProjectRelationItem projectRelationItem = getItem(result, fileProjectName);

            //读取maven项目信息
            MavenProjectRelationItem.MavenProject mavenProject = PomXmlUtil.readPomXml(pomFile);
            projectRelationItem.getMavenProjects().add(mavenProject);
        });
        return new ArrayList<>(result.values());
    }


    /**
     * 寻找项，没有则创建一个项
     *
     * @param map
     * @param fileProjectName
     * @return
     */
    private static MavenProjectRelationItem getItem(Map<String, MavenProjectRelationItem> map, String fileProjectName) {
        MavenProjectRelationItem item = map.get(fileProjectName);
        if (item == null) {
            item = new MavenProjectRelationItem();
            item.setFileProjectName(fileProjectName);
            item.setMavenProjects(new ArrayList<>());
            map.put(fileProjectName, item);
        }
        return item;
    }


    private static String getRelativePath(File rootFile, File pomFile) {
        //转换为通用符号的文件名
        String pomFilePath = pomFile.getAbsolutePath();
        //取相对路径
        pomFilePath = pomFilePath.substring(rootFile.getAbsolutePath().length() + 1, pomFilePath.length());
        //转换为兼容系统的格式
        return pomFilePath.replaceAll("\\\\", "/");
    }


    /**
     * 找到所有的pom文件
     *
     * @param projectFile
     * @return
     */
    private static Collection<File> findAllPomFile(File projectFile) {
        return FileUtils.listFiles(projectFile, new IOFileFilter() {
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
            return file.getName().equalsIgnoreCase("pom.xml");
        }).collect(Collectors.toList());
    }


}
