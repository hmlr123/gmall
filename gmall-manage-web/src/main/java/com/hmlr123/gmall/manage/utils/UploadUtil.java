package com.hmlr123.gmall.manage.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName: UploadUtil
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/18 14:18
 * @Version: 1.0
 */
public class UploadUtil {

    public static String uploadImage(MultipartFile multipartFile) {
        String imgURL = "119.23.110.43";
        String tracker = UploadUtil.class.getResource("/tracker.conf").getPath();// 获得配置文件的路径

        try {
            ClientGlobal.init(tracker);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = null;

        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StorageClient storageClient = new StorageClient(trackerServer, null);

        //上传附件的二进制对象
        try {
            byte[] bytes = multipartFile.getBytes();
            //获取文件名
            String originalFilename = multipartFile.getOriginalFilename();
            int indexOf = originalFilename.lastIndexOf(".");
            String extName = originalFilename.substring(indexOf + 1);

            String[] uploadFile = storageClient.upload_file(bytes, extName, null);

            for (String uploadInfo : uploadFile) {
                imgURL += "/" + uploadInfo;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgURL;
    }


}
