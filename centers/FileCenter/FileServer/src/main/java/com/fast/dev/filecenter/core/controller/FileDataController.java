package com.fast.dev.filecenter.core.controller;


import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.core.util.result.InvokerState;
import com.fast.dev.filecenter.core.model.FileDataModel;
import com.fast.dev.filecenter.core.service.FileDataService;
import com.fast.dev.filecenter.core.service.UrlCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("file")
public class FileDataController {

    @Autowired
    private FileDataService fileDataService;

    @Autowired
    private UrlCacheService urlCacheService;



    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @RequestMapping("uploadFile")
    public Object uploadFile(@RequestParam("file") MultipartFile[] file) {
        if (file == null || file.length <= 0) {
            return InvokerResult.builder().state(InvokerState.Error).content("上传文件不能为空").build();
        }
        try {
            List<FileDataModel> fileDataModels = new ArrayList<>();
            for (MultipartFile mFile : file) {
                fileDataModels.add(this.fileDataService.uploadFile(mFile));
            }
            return InvokerResult.builder().state(InvokerState.Success).content(fileDataModels).build();
        } catch (Exception e) {
            e.printStackTrace();
            return InvokerResult.builder().state(InvokerState.Exception).content("程序异常").build();
        }
    }

    /**
     * 根据hash查找文件99
     *
     * @param hash
     * @return
     */
    @RequestMapping("/findByHash")
    public Object findByHash(String hash) {
        Assert.hasText(hash,"hash不能为空");
        return InvokerResult.builder().state(InvokerState.Success).content(this.fileDataService.findByHash(hash)).build();
    }


    /**
     * 根据fileId查找文件
     *
     * @param fileId
     * @return
     */
    @RequestMapping("/findByFileId")
    public Object findByFileId(String fileId) {
        Assert.hasText(fileId,"fileId不能为空");
        return InvokerResult.builder().state(InvokerState.Success).content(this.fileDataService.findByFileId(fileId)).build();
    }

    /**
     * 根据fileId下载文件
     *
     * @param fileId
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/{fileId}")
    public void getFileByFileId(@PathVariable String fileId, HttpServletResponse response) throws IOException {
        FileDataModel fileDataModel = fileDataService.findByFileId(fileId);
        response.setContentType(fileDataModel.getContentType());
        fileDataService.getFileStream(fileId, response.getOutputStream());
    }

    /**
     * 根据fileId下载文件伪静态
     *
     * @param fileId
     * @param response
     * @return
     * @throws IOException
     */
//    @RequestMapping("/{fileId}.{extendName}")
//    public void redirectToDownloadFileByFileId(@PathVariable String fileId,@PathVariable String extendName, HttpServletResponse response) throws IOException {
//        log.info("进入重定向查询文件controller");
//        log.info("打印上行参数fileId : {} , extName : {}" , fileId,extendName);
//        response.sendRedirect("http://192.168.8.15/fileserver/file/" + fileId);
//        log.info("重定向查询文件，操作结束，success");
//    }

    /**
     * 自定义获取文件
     *
     * @param userId
     * @param url
     * @param response
     * @throws IOException
     */
    @RequestMapping("**/{userId}/{url:.+}")
    public void findFileByUrl(@PathVariable String userId, @PathVariable String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String func = "thumb_600_600/watermark_text/";
//        String func = "thumb_600_600/";
        //查询文件
        String fileId = urlCacheService.findFileByUrl(func,userId,url);
        if (!StringUtils.isEmpty(fileId)) {
            response.sendRedirect("http://192.168.8.15/fileserver/file/" + fileId);
        }
    }
}
