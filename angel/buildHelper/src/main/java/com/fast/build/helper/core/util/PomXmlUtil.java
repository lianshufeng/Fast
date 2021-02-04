package com.fast.build.helper.core.util;

import com.fast.build.helper.core.model.MavenProjectRelationItem;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PomXmlUtil {


    /**
     * 是否模块管理文件
     *
     * @param pomFile
     * @return
     */
    @SneakyThrows
    public static boolean isModulesManagerFile(File pomFile) {
        Document doc = new SAXReader().read(pomFile);
        Element rootElement = doc.getRootElement();

        Element packagingElement = rootElement.element("packaging");
        if (packagingElement == null || !"pom".equalsIgnoreCase(packagingElement.getText())) {
            return false;
        }
        Element modulesElement = rootElement.element("modules");
        return modulesElement != null && modulesElement.elements("module").size() > 0;
    }


    /**
     * 在指定的pom中增加模块
     *
     * @param pomFile
     * @param modules
     */
    @SneakyThrows
    public static void setmodules(File pomFile, List<String> modules) {
        Document doc = new SAXReader().read(pomFile);
        Element modulesElement = doc.getRootElement().element("modules");


        //删除所有的,包含注释等。。
        modulesElement.clearContent();


        for (String moduleName : modules) {
            modulesElement.addElement("module").setText(moduleName);
        }

        @Cleanup FileOutputStream out = new FileOutputStream(pomFile);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        @Cleanup XMLWriter writer = new XMLWriter(out, format);
        writer.write(doc);

    }


    /**
     * 在指定的pom中增加模块
     *
     * @param pomFile
     * @param modules
     */
    public static void append(File pomFile, Set<String> modules) throws Exception {
        Document doc = new SAXReader().read(pomFile);
        Element modulesElement = doc.getRootElement().element("modules");


        for (String moduleName : modules) {
//            Element module = new DOMElement("module");
//            module.setText(moduleName);
            modulesElement.addElement("module").setText(moduleName);
        }

        FileOutputStream out = new FileOutputStream(pomFile);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(doc);
        writer.close();
        out.close();

    }


    /**
     * 读取Pom信息
     *
     * @param pomFile
     * @return
     */
    @SneakyThrows
    public static MavenProjectRelationItem.MavenProject readPomXml(File pomFile) {
        if (!pomFile.exists()) {
            return null;
        }

        MavenProjectRelationItem.MavenProject mavenProject = new MavenProjectRelationItem.MavenProject();
        mavenProject.setPomFile(pomFile);

        //读取pom信息
        Document doc = new SAXReader().read(pomFile);
        Element rootElement = doc.getRootElement();
        Element groupId = rootElement.element("groupId");
        Element artifactId = rootElement.element("artifactId");
        Element name = rootElement.element("name");
        Element description = rootElement.element("description");
        Element parent = rootElement.element("parent");


        Optional.ofNullable(name).ifPresent((it) -> {
            mavenProject.setName(it.getText());
        });

        Optional.ofNullable(description).ifPresent((it) -> {
            mavenProject.setDescription(it.getText());
        });

        Optional.ofNullable(groupId).ifPresent((it) -> {
            mavenProject.setGroupId(it.getText());
        });

        Optional.ofNullable(artifactId).ifPresent((it) -> {
            mavenProject.setArtifactId(it.getText());
        });

        Optional.ofNullable(parent).ifPresent((it) -> {

            //如果当取不到当前则取父类的
            if (mavenProject.getGroupId() == null) {
                mavenProject.setGroupId(it.element("groupId").getText());
            }

            if (mavenProject.getArtifactId() == null) {
                mavenProject.setArtifactId(it.element("artifactId").getText());
            }

            //取出相对路径
            Optional.ofNullable(it.element("relativePath")).ifPresent((relativePath) -> {
                mavenProject.setRelativePath(relativePath.getText());
            });

        });

        //处理依赖关系
        Optional.ofNullable(rootElement.element("dependencies")).ifPresent((it) -> {
            List<MavenProjectRelationItem.MavenProject> dependencyItems = new ArrayList<>();
            Optional.ofNullable(it.elements("dependency")).ifPresent((dependency) -> {
                dependency.forEach((d) -> {

                    final MavenProjectRelationItem.MavenProject dependencyItem = new MavenProjectRelationItem.MavenProject();
                    Optional.ofNullable(d.element("groupId")).ifPresent((dg) -> {
                        dependencyItem.setGroupId(dg.getText());
                    });

                    Optional.ofNullable(d.element("artifactId")).ifPresent((da) -> {
                        dependencyItem.setArtifactId(da.getText());
                    });

                    dependencyItems.add(dependencyItem);
                });

            });

            mavenProject.setDependency(dependencyItems);
        });

        return mavenProject;
    }


    /**
     * 读取pom项目应该存放的位置
     *
     * @param pomFile
     * @return
     */
    public static int getAppDepth(File pomFile) throws Exception {
        Document doc = new SAXReader().read(pomFile);
        Element modulesElement = doc.getRootElement().element("parent");
        if (modulesElement == null) {
            return -1;
        }
        Element groupId = modulesElement.element("groupId");
        Element artifactId = modulesElement.element("artifactId");
        if (groupId.getText().equals("com.fast.dev") && artifactId.getText().equals("PParent")) {
            String relativePath = modulesElement.element("relativePath").getText();
            relativePath = PathUtil.format(relativePath);
            String[] arr = relativePath.split("/");
            //确定依赖的核心包
            if (arr[arr.length - 2].equals("super") && arr[arr.length - 1].equals("PParent")) {
                // 去除最后两位，计算相对位移的路径，不支持自定义路径。。。。
                return arr.length - 2;
            }
        }
        return -1;
    }

    /**
     * 是否包含模块的pom
     *
     * @param pomFile
     * @return
     * @throws Exception
     */
    public static boolean hasModules(File pomFile) throws Exception {
        Document doc = new SAXReader().read(pomFile);
        Element modulesElement = doc.getRootElement().element("modules");
        return modulesElement != null;
    }


    public static void main(String[] args) {
//        try {
//            System.out.println(getAppDepth(new File("E:\\GitStore\\BaseFast\\application\\ServiceToken\\pom.xml")));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        System.out.println(readPomXml(new File("E:\\git\\aiyihema\\BaseFast\\application\\CommentCenter\\CommentServer\\pom.xml")));

    }

}
