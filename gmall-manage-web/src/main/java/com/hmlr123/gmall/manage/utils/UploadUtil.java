package utils;

import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName: UploadUtil
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/18 14:18
 * @Version: 1.0
 */
public class UploadUtil {


    public static String uploadImage(MultipartFile multipartFile) {
        String tracker = UploadUtil.class.getResource("/tracker.conf").getPath();// 获得配置文件的路径

    }


}
