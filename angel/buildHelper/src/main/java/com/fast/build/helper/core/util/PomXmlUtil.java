package com.fast.build.helper.core.util;

import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;

public class PomXmlUtil {
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
     * 读取pom项目应该存放的位置
     *
     * @param pomFile
     * @return
     */
    public static int getAppDepth(File pomFile) throws Exception {
        Document doc = new SAXReader().read(pomFile);
        Element modulesElement = doc.getRootElement().element("parent");
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

    public static void main(String[] args) {
        try {
            System.out.println(getAppDepth(new File("E:\\GitStore\\BaseFast\\application\\ServiceToken\\pom.xml")));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
