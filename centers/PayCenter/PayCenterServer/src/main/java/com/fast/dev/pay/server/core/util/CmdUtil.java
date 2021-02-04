package com.fast.dev.pay.server.core.util;

import com.fast.dev.core.util.os.SystemUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

@Slf4j
public class CmdUtil {


    @SneakyThrows
    public static String execute(String... cmds) {
        String ret = readRet(Runtime.getRuntime().exec(cmds));
        log.info("cmd : {} ret : {}", cmds, ret);
        return ret;
    }

//
//    @SneakyThrows
//    public static String execute(String cmd) {
//        cmd = new String(cmd.getBytes(), "UTF-8");
//        String ret = readRet(Runtime.getRuntime().exec(cmd));
//        log.info("cmd : {} ret : {}", cmd, ret);
//        return ret;
//    }

    /**
     * 执行脚本
     *
     * @param cmds
     */
    @SneakyThrows
    public static void runCmd(String... cmds) {
        File file = File.createTempFile("cert_", SystemUtil.isLinux() ? ".sh" : ".bat");
        file.delete();


        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(file);
        if (SystemUtil.isLinux()) {
            fileOutputStream.write("#!/bin/bash\n".getBytes());
        }
        for (String cmd : cmds) {
            String encode = SystemUtil.isWindows() ? "GBK" : "UTF-8";
            fileOutputStream.write((cmd + " ").getBytes(encode));
        }
        fileOutputStream.flush();
        fileOutputStream.close();

        String headCmd = SystemUtil.isWindows() ? "cmd /c" : "/bin/bash";
        readRet(Runtime.getRuntime().exec(headCmd + " " + file.getAbsolutePath()));
    }

    /**
     * 读取结果集
     *
     * @param process
     */
    @SneakyThrows
    private static String readRet(Process process) {
        @Cleanup InputStream inputStream = process.getInputStream();
        @Cleanup InputStream ErrorStream = process.getErrorStream();
        process.waitFor();
        return StreamUtils.copyToString(inputStream, Charset.forName("UTF-8")) + StreamUtils.copyToString(ErrorStream, Charset.forName("UTF-8"));
    }
}
