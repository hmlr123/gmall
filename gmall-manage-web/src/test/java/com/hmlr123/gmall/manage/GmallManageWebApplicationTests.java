package com.hmlr123.gmall.manage;

import com.hmlr123.gmall.bean.FastDFSFile;
import com.hmlr123.gmall.manage.utils.FastDFSClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageWebApplicationTests {

    @Test
    public void contextLoads() throws IOException {
//        //获取配置文件路径 配置fdfs的全局链接地址
//        String path = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();
//        ClientGlobal.init(path);
//
//        TrackerClient trackerClient = new TrackerClient();
//
//        //获得trackerServer的实例
//        TrackerServer trackerServer = trackerClient.getConnection();
//
//        //通过tracker获取Storage链接客户端
//        StorageClient storageClient = new StorageClient(trackerServer, null);
//        NameValuePair nameValuePair = new NameValuePair();
//        nameValuePair.setName("李威");
//        nameValuePair.setValue("19980101");
//        List<NameValuePair> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(nameValuePair);
//        String[] jpgs = storageClient.upload_file("C:/Users/liwei/Pictures/everton-vila-AsahNlC0VhQ-unsplash.jpg",
//                "jpg", null);
//
//        for (String jpg : jpgs) {
//            System.out.println(jpg);
//        }


        InputStream inputStream = new FileInputStream("C:/Users/liwei/Pictures/everton-vila-AsahNlC0VhQ-unsplash.jpg");
        byte[] file_buff = null;
        if (null != inputStream) {
            //字节流长度
            int available = inputStream.available();
            //定义字节数组
            file_buff = new byte[available];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile fastDFSFile = new FastDFSFile();
        fastDFSFile.setAuthor("liwei");
        fastDFSFile.setExt("jpg");
        fastDFSFile.setName("everton-vila-AsahNlC0VhQ-unsplash");
        fastDFSFile.setContent(file_buff);
        String[] upload = FastDFSClient.upload(fastDFSFile);

        String s = FastDFSClient.getTrackerUrl() + upload[0] + "/" + upload[1];
        System.out.println(s);
    }

}
