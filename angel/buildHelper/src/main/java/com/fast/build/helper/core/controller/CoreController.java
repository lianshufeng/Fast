package com.fast.build.helper.core.controller;

import com.fast.build.helper.core.helper.PathHelper;
import com.fast.build.helper.core.model.FileInfoModel;
import com.fast.build.helper.core.model.UpdateBuildTask;
import com.fast.build.helper.core.service.CoreService;
import com.fast.dev.core.util.response.ResponseUtil;
import com.fast.dev.core.util.result.InvokerResult;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;

@RestController
public class CoreController {


    //下载目录
    private final static String DownloadPath = "dl";


    @Autowired
    private PathHelper pathHelper;

    @Autowired
    private CoreService coreService;

    @RequestMapping("doUpdate")
    public InvokerResult<UpdateBuildTask> doUpdate() {
        return InvokerResult.notNull(this.coreService.execute());
    }

    @RequestMapping("doBackup")
    public InvokerResult<UpdateBuildTask> doBackup() {
        return InvokerResult.notNull(this.coreService.backupBuild());
    }

//    @SneakyThrows
//    @RequestMapping("dl/**/*")
//    public void download(HttpServletRequest request, HttpServletResponse response) {
//        String uri = request.getRequestURI();
//        String fileName = uri.substring(DownloadPath.length() + 1);
//        File file = this.pathHelper.getResourcePath(fileName);
//        if (!file.exists()) {
//            response.sendError(404);
//            return;
//        }
//
//        @Cleanup FileInputStream fileInputStream = new FileInputStream(file);
//        ResponseUtil.writeStream(request, response, fileInputStream, file.length(), FilenameUtils.getExtension(fileName));
//    }


    @SneakyThrows
    @RequestMapping("removeBackup/{fileName}")
    public InvokerResult<UpdateBuildTask> removeBackup(@PathVariable("fileName") String fileName) {
        File file = this.pathHelper.getBackupPath(fileName);
        if (file.exists()) {
            FileUtils.cleanDirectory(file);
            file.delete();
        }
        return InvokerResult.notNull(!file.exists());
    }


    /**
     * 寻找文件
     *
     * @param request
     * @param filePath
     * @return
     */
    public List<FileInfoModel> listFile(HttpServletRequest request, File filePath) {
        List<FileInfoModel> files = new ArrayList<>(getFileInfoModel(request, filePath));
        //排序
        Collections.sort(files, new Comparator<FileInfoModel>() {
            @Override
            public int compare(FileInfoModel o1, FileInfoModel o2) {
                return Integer.parseInt(String.valueOf(o2.getFile().length() - o1.getFile().length()));
            }
        });
        return files;
    }


    /**
     * 遍历文件
     *
     * @param request
     * @param pathFile
     * @return
     */
    public Collection<FileInfoModel> getFileInfoModel(HttpServletRequest request, File pathFile) {
        String host = request.getScheme() + "://" + request.getHeader("Host") + "/resources";
        List<FileInfoModel> fileInfos = new ArrayList<>();

        for (File file : FileUtils.listFiles(pathFile, new String[]{"jar"}, true)) {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setFile(file);

            //转换为访问的url
            fileInfoModel.setUrl(host + "/" + filePath2Uri(file.getAbsolutePath()));

            // 尺寸格式化
            BigDecimal bigDecimal = new BigDecimal((double) file.length() / 1024 / 1024);
            fileInfoModel.setSize(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " 兆");

            fileInfos.add(fileInfoModel);
        }
        return fileInfos;
    }


    /**
     * 文件名转url
     *
     * @return
     */
    private String filePath2Uri(String fileName) {
        String pathName = fileName.substring(this.pathHelper.getResourcePath("").getAbsolutePath().length());
        pathName = pathName.replaceAll("\\\\", "/");
        while (pathName.substring(0, 1).equals("/")) {
            pathName = pathName.substring(1, pathName.length());
        }
        return pathName;
    }


}
