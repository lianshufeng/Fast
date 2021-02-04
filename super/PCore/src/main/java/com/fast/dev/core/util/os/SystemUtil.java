package com.fast.dev.core.util.os;

/**
 * 系统工具
 */
public class SystemUtil {


    /**
     * 是否linux系统
     *
     * @return
     */
    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    /**
     * 是否windows系统
     *
     * @return
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    /**
     * 获取CPu核心数
     *
     * @return
     */
    public static int getCpuCoreCount() {
        return Runtime.getRuntime().availableProcessors();
    }

}
