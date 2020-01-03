package com.fast.dev.core.helper;


import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.util.*;

/**
 * 磁盘缓存存储
 */
public abstract class DiskCacheStoreHelper {

    //磁盘存储地址
    private File storePath;

    //最大的缓存长度
    private long maxCacheSize;

    //文件缓存
    private FileCache fileCache = new FileCache();

    protected DiskCacheStoreHelper(File storePath, long maxCacheSize) {
        this.storePath = storePath;
        this.maxCacheSize = maxCacheSize;
    }

    /**
     * 创建缓存对象
     *
     * @return
     */
    public static DiskCacheStoreHelper build(File storePath, long maxCacheSize) {
        if (!storePath.exists()) {
            storePath.mkdirs();
        }
        DiskCacheStoreHelper diskCacheStore = new DiskCacheStoreHelper(storePath, maxCacheSize) {
        };
        diskCacheStore._initFileSize();
        return diskCacheStore;
    }


    /**
     * 默认的构建对象
     *
     * @return
     */
    public static DiskCacheStoreHelper build() {
        return build(new File(System.getProperty("java.io.tmpdir") + "/_diskcache"), 1024 * 1024 * 100);
    }


    /**
     * 获取总数量
     *
     * @return
     */
    public long getTotalSize() {
        return this.fileCache.getTotalFileSize();
    }


    /**
     * 获取所有的key
     *
     * @return
     */
    public Set<String> keySet() {
        return this.fileCache.keySet();
    }


    /**
     * 获取文件的长度
     *
     * @param key
     * @return
     */
    public synchronized Long size(String key) {
        String fileName = keyHash(key);
        File targetFile = new File(this.storePath.getAbsolutePath() + "/" + fileName);
        if (targetFile.exists()) {
            return targetFile.length();
        } else {
            return null;
        }
    }


    /**
     * 根据key打开一个输出流，写完管道后并关闭
     *
     * @param key
     * @return
     */
    public synchronized OutputStream store(String key) {
        final String fileName = keyHash(key);
        //保证目录一定存在
        if (!this.storePath.exists()){
            this.storePath.mkdirs();
        }

        final File tmpFile = new File(this.storePath.getAbsolutePath() + "/" + fileName + "_new");
        //拷贝流
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(tmpFile) {

                //不允许多次关闭该对象
                private boolean idClosed = false;

                @Override
                public synchronized void close() throws IOException {
                    if (idClosed) {
                        return;
                    }
                    try {
                        super.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    _updateTmpFile(fileName, tmpFile);
                    idClosed = true;
                }
            };
            return fileOutputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 存储对象
     *
     * @param key
     * @param inputStream
     * @return
     */
    public synchronized boolean store(String key, InputStream inputStream) {
        try {
            @Cleanup OutputStream outputStream = store(key);
            StreamUtils.copy(inputStream, outputStream);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 更新文件
     */
    private void _updateTmpFile(String fileName, File tmpFile) {
        //限制磁盘空间的大小
        limitDiskSize(tmpFile.length());

        //更新文件
        File target = new File(storePath.getAbsolutePath() + "/" + fileName);
        //存在删除旧文件
        if (target.exists()) {
            target.delete();
        }
        //新文件更新路径
        if (tmpFile.renameTo(target)) {
            updateCacheFileSize(fileName);
        }
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
    }


    /**
     * 查询文件
     *
     * @param key
     * @param outputStream
     */
    public Long get(String key, OutputStream outputStream) {
        InputStream inputStream = get(key);
        if (inputStream == null) {
            return null;
        }
        try {
            Integer size = StreamUtils.copy(inputStream, outputStream);
            inputStream.close();
            return size + 0l;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public File getFile(String key) {
        String fileName = keyHash(key);
        File targetFile = new File(this.storePath.getAbsolutePath() + "/" + fileName);
        return targetFile;
    }


    /**
     * 请手动关闭流
     *
     * @param key
     * @return
     */
    public InputStream get(String key) {
        File targetFile = getFile(key);
        if (!targetFile.exists()) {
            return null;
        }
        try {
            return new FileInputStream(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除磁盘及缓存
     *
     * @param key
     */
    public synchronized void remove(String key) {
        _remove(keyHash(key));
    }

    public boolean exists(String key) {
        String fileName = keyHash(key);
        File file = new File(this.storePath.getAbsolutePath() + "/" + fileName);
        return file.exists();
    }


    /**
     * 删除
     *
     * @param fileName
     */
    private void _remove(String fileName) {

        //从磁盘删除
        removeDiskFile(fileName);

        //删除缓存
        this.fileCache.remove(fileName);

    }


    /**
     * 限制磁盘空间的大小
     */
    private void limitDiskSize(long newDataSize) {
        while (this.fileCache.getCount() > 0 && this.fileCache.getTotalFileSize() + newDataSize > this.maxCacheSize) {
            FileInfo fileInfo = this.fileCache.removeBestOldFile();
            if (fileInfo != null) {
                removeDiskFile(fileInfo.getFileName());
            }
        }
    }


    /**
     * 删除占用空间最小文件
     */
    private void removeDiskFile(String fileName) {
        if (fileName != null) {
            File file = new File(this.storePath.getAbsolutePath() + "/" + fileName);
            if (file.exists()) {
                file.delete();
            }
        }

    }


    /**
     * 实例化的时候加载
     */
    private void _initFileSize() {
        for (File file : this.storePath.listFiles()) {
            updateCacheFileSize(file.getName());
        }
    }


    /**
     * 计算key的hash值
     *
     * @param key
     * @return
     */
    public static String keyHash(String key) {
        return DigestUtils.md5Hex(key);
    }


    /**
     * 更新文件的缓存
     *
     * @param fileName
     */
    private void updateCacheFileSize(String fileName) {
        File file = new File(this.storePath.getAbsolutePath() + "/" + fileName);
        if (file.exists()) {
            this.fileCache.addFile(file);
        }
    }


    class FileCache {


        //总文件大小
        private long totalFileSize = 0;

        //文件列表，按照创建时间排序
        private List<FileInfo> fileInfos = new ArrayList<FileInfo>();

        //文件名列表
        private Map<String, FileInfo> fileNames = new HashMap<>();


        /**
         * 添加文件
         */
        public synchronized void addFile(File file) {
            //非空
            if (file == null || !file.exists()) {
                return;
            }

            //文件名
            String fileName = file.getName();

            //存储对象
            FileInfo fileInfo = new FileInfo(fileName, file.length(), file.lastModified());


            int index = 0;
            if (this.fileInfos.size() > 0 && fileInfo.getCreateTime() >= this.fileInfos.get(this.fileInfos.size() - 1).getCreateTime()) {
                index = this.fileInfos.size();
            } else {
                //倒叙插入，理论上插入的都是新文件，从最后一个按时间的创建顺序排序效率最高
                for (int i = 0; i < this.fileInfos.size(); i++) {
                    index = i;
                    if (fileInfo.getCreateTime() <= this.fileInfos.get(index).getCreateTime()) {
                        break;
                    }
                }
            }


            //插入数据
            this.fileInfos.add(index, fileInfo);

            //更新文件总量
            this.totalFileSize += fileInfo.getFileSize();

            //更新文件名hash
            this.fileNames.put(fileName, fileInfo);
        }


        /**
         * 通过文件名删除
         *
         * @param fileName
         * @return
         */
        public synchronized FileInfo remove(String fileName) {
            FileInfo fileInfo = this.fileNames.get(fileName);
            if (fileInfo == null) {
                return null;
            }
            _remove(fileInfo);

            return fileInfo;

        }


        /**
         * 删除序号第一个元素
         */
        public synchronized FileInfo removeBestOldFile() {
            if (this.fileInfos.size() <= 0) {
                return null;
            }
            FileInfo fileInfo = this.fileInfos.remove(0);
            if (fileInfo == null) {
                return null;
            }

            _remove(fileInfo);

            return fileInfo;
        }


        /**
         * 获取文件缓存的总数量
         *
         * @return
         */
        public int getCount() {
            return this.fileInfos.size();
        }


        private void _remove(FileInfo fileInfo) {

            //删除缓存的文件名
            this.fileNames.remove(fileInfo.getFileName());

            //减少总记录
            this.totalFileSize -= fileInfo.getFileSize();
        }


        /**
         * 获取总文件大小
         *
         * @return
         */
        public long getTotalFileSize() {
            return this.totalFileSize;
        }


        /**
         * 获取所有的key
         *
         * @return
         */
        public Set<String> keySet() {
            return this.fileNames.keySet();
        }


    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    class FileInfo {

        /**
         * 文件名
         */
        private String fileName;

        /**
         * 文件大小
         */
        private long fileSize;


        /**
         * 文件的创建时间
         */
        private long createTime;


    }


}
