package com.fast.dev.filecenter.core.service;

import com.fast.dev.filecenter.core.dao.FileDataDao;
import com.fast.dev.filecenter.core.model.FileDataModel;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

@Service
@Slf4j
public class FileDataService {

    @Autowired
    private FileDataDao fileDataDao;


    /**
     * 上传数据流
     *
     * @param mFile
     */
    public FileDataModel uploadFile(MultipartFile mFile) throws IOException {
        //上传文件，返回当前文件对象
        GridFSFile gridFSFile = fileDataDao.uploadFile(mFile.getInputStream(), mFile.getOriginalFilename(), mFile.getContentType());
        //根据hash删除冗余的文件，同一份文件只能保留一份
        //获取当前文件的hash
//        String hash = gridFSFile.getMD5();
        String hash = null;
        fileDataDao.delRedundantFile(hash);
        return convertToFileDataModel(fileDataDao.findByHash(hash));
    }


    /**
     * 根据hash查找file
     *
     * @param hash
     * @return
     */
    public FileDataModel findByHash(String hash) {
        return convertToFileDataModel(fileDataDao.findByHash(hash));
    }

    /**
     * 根据fileId查找file
     *
     * @param fileId
     * @return
     */
    public FileDataModel findByFileId(String fileId) {
        return convertToFileDataModel(fileDataDao.findByFileId(fileId));
    }

    /**
     * 转换为FileDataModel
     *
     * @param gridFSFile
     * @return
     */
    private FileDataModel convertToFileDataModel(GridFSFile gridFSFile) {
        if (null == gridFSFile) {
            return null;
        }
        FileDataModel fileDataModel = new FileDataModel();
        fileDataModel.setId(gridFSFile.getObjectId().toString());
//        fileDataModel.setHash(gridFSFile.getMD5());
        fileDataModel.setHash(null);
        fileDataModel.setFileName(gridFSFile.getFilename());
        fileDataModel.setContentType(gridFSFile.getMetadata().get("_contentType").toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(gridFSFile.getUploadDate());
        fileDataModel.setCreateTime(calendar.getTimeInMillis());
        return fileDataModel;
    }

    public void getFileStream(String fileId, OutputStream out) throws IOException {
        fileDataDao.getFileStream(fileId, out);
    }
}
