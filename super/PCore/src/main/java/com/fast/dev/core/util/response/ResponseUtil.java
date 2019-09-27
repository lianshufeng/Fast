package com.fast.dev.core.util.response;

import com.fast.dev.core.util.response.model.MimeType;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 响应工具类
 */
@Log
public class ResponseUtil {


    private final static Map<String, MimeType> extNameMimeMap = new ConcurrentHashMap<>();

    static {
        initMimeType();
    }

    @SneakyThrows
    private static void initMimeType() {
        @Cleanup InputStream inputStream = ResponseUtil.class.getClassLoader().getResourceAsStream("tomcat/web.xml");
        WebXmlUtil.readMime(inputStream, extNameMimeMap);
        log.info("mime : [" + extNameMimeMap.size() + "]");
    }


    /**
     * 写出流
     *
     * @param response
     */
    @SneakyThrows
    public static boolean writeStream(HttpServletRequest request, HttpServletResponse response, InputStream
            sourceStream, long sourceSize, String extName) {

        MimeType mimetype = extNameMimeMap.get(extName);

        //根据不同的类型进行处理
        if (mimetype != null) {
            response.addHeader(HttpHeaders.CONTENT_TYPE, mimetype.getName());
        }


        //对断点续传的支持
        rangeHelper(request, response, sourceStream, sourceSize);


        //输入流拷贝到输出流
        @Cleanup OutputStream outputStream = response.getOutputStream();
        StreamUtils.copy(sourceStream, outputStream);


        return true;

    }


    /**
     * 断点续传的支持
     *
     * @param request
     * @param response
     * @param sourceStream
     * @param sourceSize
     */
    @SneakyThrows
    private static void rangeHelper(HttpServletRequest request, HttpServletResponse response, InputStream sourceStream, long sourceSize) {


        //支持断点续传
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");


        //新文件
        if (request.getHeader(HttpHeaders.RANGE) == null) {
            setContentLength(response, sourceSize);
            return;
        }


        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
        List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        //只支持一组断点续传
        if (httpRanges != null && httpRanges.size() > 0) {
            HttpRange httpRange = httpRanges.get(0);

            //开始
            long start = httpRange.getRangeStart(0);
            //结束
            long end = httpRange.getRangeEnd(0);
            if (end == -1) {
                end = sourceSize;
            }
            if (start == -1) {
                start = sourceSize - end;
            }
            //跳过已读取的流
            sourceStream.skip(start);
            //设置总长度
            setContentLength(response, end - start);
            //设置响应头的响应字节
            response.addHeader(HttpHeaders.CONTENT_RANGE, String.format("bytes %s-%s/%s", String.valueOf(start), String.valueOf(end - 1), String.valueOf(end)));
            log.info(String.format("Range : {%s}-{%s}", String.valueOf(start), String.valueOf(end)));
        }


    }


    /**
     * 断点续传的支持
     *
     * @param request
     * @param response
     * @param sourceStream
     * @param sourceSize
     */
    @SneakyThrows
    private static void rangeHelper2(HttpServletRequest request, HttpServletResponse response, InputStream
            sourceStream, long sourceSize) {

        //支持断点续传
        response.addHeader("Accept-Ranges", "bytes");

        //读取指定的字节数
        String range = request.getHeader("Range");

        //新文件
        if (!StringUtils.hasText(range)) {
            setContentLength(response, sourceSize);
            return;
        }

        //取随机读取的字节集数
        String[] items = range.split("=");
        if (items.length == 0 || !items[0].equalsIgnoreCase("bytes")) {
            //新文件
            setContentLength(response, sourceSize);
            return;
        }


        //获取开始于结束的数
        long start = 0;
        long end = 0;
        String[] rangeArr = items[1].split("-");
        if (rangeArr.length == 0) {
            setContentLength(response, sourceSize);
            return;
        } else if (rangeArr.length == 1) {
            //重新构建一个下标为2的数组
            rangeArr = new String[]{rangeArr[0], ""};
        }


        if (StringUtils.hasText(rangeArr[0]) || !StringUtils.hasText(rangeArr[1])) {
            start = Long.valueOf(rangeArr[0]);
            end = sourceSize;
        } else if (StringUtils.hasText(rangeArr[1])) {
            start = sourceSize - Long.valueOf(rangeArr[1]);
            end = sourceSize;
        } else {
            start = Long.valueOf(rangeArr[0]);
            end = Long.valueOf(rangeArr[1]);
        }

        log.info(String.format("from {%s} to {%s}", String.valueOf(start), String.valueOf(end)));


        //内容长度 = 结束位置 - 开始位置
        setContentLength(response, end - start);


        //设置响应头
        response.addHeader("Content-Range", String.format("bytes %s-%s/%s", String.valueOf(start), String.valueOf(end - 1), String.valueOf(end)));


        //设置从哪开始读取流
        sourceStream.skip(start);


        //设置响应码 (206)
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);


    }


    /**
     * 设置内容长度
     *
     * @param response
     * @param size
     */
    private static void setContentLength(HttpServletResponse response, long size) {
        response.addHeader("Content-Length", String.valueOf(size));
    }


}

