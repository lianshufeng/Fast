package com.fast.dev.filecenter.core.service.fileDataCommand;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface FileDataCommandService {


    ByteArrayOutputStream command(String param,InputStream inputStream) throws IOException;

    /**
     *  命令头
     * @return
     */
    String cmd();
}
