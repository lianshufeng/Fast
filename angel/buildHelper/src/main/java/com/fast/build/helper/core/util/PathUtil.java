package com.fast.build.helper.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class PathUtil {

    /**
     * 读取文件流
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws URISyntaxException
     */
    public static URL readFileUrl(String fileName) {
        URL url = PathUtil.class.getClassLoader().getResource(fileName);
        return url;
    }

    /**
     * 寻找指定目录下的资源文件
     *
     * @param files
     */
    public static void listFiles(final File targetFile, List<File> files) {
        if (targetFile == null || files == null) {
            return;
        }
        for (File file : targetFile.listFiles()) {
            if (file.isDirectory()) {
                listFiles(file, files);
            } else {
                files.add(file);
            }
        }
    }

    /**
     * 格式化文件
     *
     * @param file
     * @return
     */
    public static String format(final File file) {
        return format(file.getAbsolutePath());
    }

    /**
     * 格式化文件
     *
     * @param file
     * @return
     */
    public static String format(final String file) {
        String path = file;
        while (path.indexOf("\\") > -1) {
            path = path.replaceAll("\\\\", "/");
        }
        while (path.indexOf("//") > -1) {
            path = path.replaceAll("//", "/");
        }
        return path;
    }

    /**
     * 取出相当路径
     *
     * @param rootPath 根目录
     * @param path
     * @return
     */
    public static String relative(final String rootPath, final String path) {
        String formatRootPath = format(rootPath);
        String formatPath = format(path);
        String left = formatPath.substring(0, formatRootPath.length());
        if (left.equals(formatRootPath)) {
            return formatPath.substring(formatRootPath.length(), formatPath.length());
        }
        return null;
    }

    /**
     * 取出相对路径
     *
     * @param rootPath
     * @param path
     * @return
     */
    public static String relative(final File rootPath, final File path) {
        String formatRootPath = format(rootPath);
        String formatPath = format(path);
        return relative(formatRootPath, formatPath);
    }

    /**
     * 获取文件名称
     *
     * @param file
     * @return
     */
    public static String getExtName(final File file) {
        String name = file.getName();
        int at = name.indexOf(".");
        if (at > -1) {
            return name.substring(at, name.length());
        }
        return null;
    }

    /**
     * 获取文件名
     *
     * @param file
     * @return
     */
    public static String getFileName(final File file) {
        String name = file.getName();
        int at = name.indexOf(".");
        if (at > -1) {
            return name.substring(0, at);
        }
        return name;
    }

}