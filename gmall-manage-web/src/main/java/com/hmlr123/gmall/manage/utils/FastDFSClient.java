package com.hmlr123.gmall.manage.utils;


import com.hmlr123.gmall.bean.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FastDFS 上传工具类.
 *
 * @author liwei
 * @date 2019/9/2 12:44
 */
public class FastDFSClient {

    private static final Logger logger = LoggerFactory.getLogger(FastDFSClient.class);

    //读取并初始化配置信息
    static {
        try {
            String filePath = new ClassPathResource("tracker.conf").getFile().getAbsolutePath();
            ClientGlobal.init(filePath);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
        } catch (Exception e) {
            logger.error("FastDFS Client Init Fail!", e);
        }
    }


    /**
     * 文件上传.
     *
     * @param fastDFSFile 上传实体数据
     * @return
     */
    public static String[] upload(FastDFSFile fastDFSFile) {
        logger.info("File Name:" + fastDFSFile.getName() + "File Length" + fastDFSFile.getContent().length);

        //元数据
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author", fastDFSFile.getAuthor());
        //开始时间
        long startTime = System.currentTimeMillis();
        //上传结果
        String[] uploadResults = null;
        //存储客户端
        StorageClient storageClient = null;

        try {
            storageClient = getTrackerClient();
            uploadResults = storageClient.upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), meta_list);
        } catch (IOException e) {
            logger.error("IO Exception whenr uploading the file:" + fastDFSFile.getName(), e);
        } catch (Exception e) {
            logger.error("Non IO Exception when uploading the file:" + fastDFSFile.getName(), e);
        }

        logger.info("upload_file time used:" + (System.currentTimeMillis() - startTime) + " ms");
        if (null == fastDFSFile && null != storageClient) {
            logger.error("upload_file fail, error code:", storageClient.getErrorCode());
        }
        //组名
        String groupName = uploadResults[0];
        //远程文件名
        String remoteFileName = uploadResults[1];
        logger.info("upload file successfully!" + "group_name:" + groupName, ", remoteFileName: " + remoteFileName);
        return uploadResults;
    }

    /**
     * 获取文件信息.
     *
     * @param groupName         组名
     * @param remoteFileName    远程文件名
     * @return                  文件信息
     */
    public static FileInfo getFile(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getTrackerClient();
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (IOException e) {
            logger.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (MyException e) {
            logger.error("Non IO Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    /**
     * 文件下载.
     *
     * @param groupName         组名
     * @param remoteFileName    远程文件名
     * @return                  输出流
     */
    public static InputStream downFile(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getTrackerClient();
            byte[] fileBytes = storageClient.download_file(groupName, remoteFileName);
            return new ByteArrayInputStream(fileBytes);
        } catch (IOException e) {
            logger.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (MyException e) {
            logger.error("Non IO Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    /**
     * 删除文件.
     *
     * @param groupName         组名
     * @param remoteFileName    远程文件名
     * @throws IOException
     */
    public static void deleteFile(String groupName, String remoteFileName) throws IOException, MyException {
        StorageClient storageClient = getTrackerClient();
        int file = storageClient.delete_file(groupName, remoteFileName);
        logger.info("delete file successfully!" + file);
    }

    /**
     * 获取存储服务端.
     *
     * @param groupName     组名
     * @return              存储服务端信息
     * @throws IOException
     */
    public static StorageServer getStorageServer(String groupName) throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getStoreStorage(trackerServer, groupName);
    }

    /**
     * 获取服务器信息.
     * @param groupName         组名
     * @param remoteFileName    远程文件名称
     * @return                  服务器端信息
     * @throws IOException
     */
    public static ServerInfo[] getFetchStorages(String groupName, String remoteFileName) throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
    }

    /**
     * 获取tracker地址.
     *
     * @return              tracker地址
     * @throws IOException
     */
    public static String getTrackerUrl() throws IOException {
        return "http://" + getTrackerServer().getInetSocketAddress().getHostString()
                + ":" + Constant.STORAGE_PORT + "/";
    }

    /**
     * 获取tracker 服务端.
     *
     * @return  tracker服务端
     * @throws IOException
     */
    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerServer;
    }

    /**
     * 获取存储客户端.
     *
     * @return  存储客户端
     * @throws IOException
     */
    private static StorageClient getTrackerClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }
}
