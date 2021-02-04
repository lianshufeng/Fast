package com.fast.dev.core.helper;

import org.springframework.boot.system.ApplicationHome;

import java.io.File;


public abstract class ApplicationHomeHelper {


    public abstract Class<?> getMeClass();


    /**
     * 获取home的路径
     *
     * @return
     */
    public File getHomeFile() {
        return new ApplicationHome(getMeClass()).getDir();
    }


    /**
     * 获取资源路径
     *
     * @param fileName
     * @return
     */
    public File getResource(String fileName) {
        return new File(getHomeFile().getAbsolutePath() + "/" + fileName);
    }


}
