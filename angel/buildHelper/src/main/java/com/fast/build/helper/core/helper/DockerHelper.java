package com.fast.build.helper.core.helper;

import com.fast.dev.core.util.token.TokenUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

@Component
@Slf4j
public class DockerHelper {


    private final static String ReStartCommand = "curl -XPOST --unix-socket /var/run/docker.sock http://localhost/containers/%s/restart";


    /**
     * 重启容器
     */
    @SneakyThrows
    public void restart(String[] containerNames) {
        String fileName = "restart_" + TokenUtil.create();
        @Cleanup("delete") File cmdFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName + ".sh");
        StringBuffer sb = new StringBuffer();
        for (String containerName : containerNames) {
            sb.append(String.format(ReStartCommand, containerName));
            sb.append("\n");
        }
        FileUtils.writeStringToFile(cmdFile, sb.toString(), "UTF-8");
        ProcessBuilder processBuilder = new ProcessBuilder("sh", cmdFile.getAbsolutePath());
        Process process = processBuilder.start();
        @Cleanup InputStream inputStream = process.getInputStream();
        @Cleanup InputStream errorStream = process.getErrorStream();
        readStream(inputStream);
        readStream(errorStream);
        process.waitFor();
    }

    @SneakyThrows
    private void readStream(InputStream inputStream) {
        log.info(StreamUtils.copyToString(inputStream, Charset.forName("UTF-8")));
    }


}
