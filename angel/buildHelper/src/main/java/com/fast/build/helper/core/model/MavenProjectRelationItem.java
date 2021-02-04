package com.fast.build.helper.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Maven项目关系分析
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MavenProjectRelationItem {

    //文件项目名
    private String fileProjectName;

    //Maven项目
    private List<MavenProject> mavenProjects;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MavenProject {
        //pom路径
        private File pomFile;

        private String name;

        private String groupId;

        private String artifactId;

        private String description;

        //相对父项目路径
        private String relativePath;

        //依赖
        private List<MavenProject> dependency;


        /**
         * 转换到简单的名字
         *
         * @return
         */
        public String toSimpleName() {
            return this.getGroupId() + "." + this.artifactId;
        }


    }


}
