package com.fast.dev.core.util.path;

import com.fast.dev.core.util.text.TextUtil;

public class PathUtil {


    /**
     * 取路径的父类
     *
     * @param path
     * @param split
     * @return
     */
    public static String getParent(String path, String split) {
        String[] sources = path.split(split);
        String[] newPath = new String[sources.length - 1];
        System.arraycopy(sources, 0, newPath, 0, newPath.length);
        return TextUtil.join(newPath, split);
    }


    /**
     * 获取当前名称
     *
     * @param path
     * @param split
     * @return
     */
    public static String getName(String path, String split) {
        String[] sources = path.split(split);
        return sources[sources.length - 1];
    }

}
