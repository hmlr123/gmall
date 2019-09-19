package com.hmlr123.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hmlr123.gmall.bean.FastDFSFile;
import com.hmlr123.gmall.bean.PmsProductImage;
import com.hmlr123.gmall.bean.PmsProductInfo;
import com.hmlr123.gmall.bean.PmsProductSaleAttr;
import com.hmlr123.gmall.manage.utils.FastDFSClient;
import com.hmlr123.gmall.manage.utils.UploadUtil;
import com.hmlr123.gmall.service.SpuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @ClassName: SpuController
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/4 20:19
 * @Version: 1.0
 */
@Controller
@CrossOrigin(origins = "*", maxAge = 3600)//解决跨域问题 跨域访问注解
public class SpuController {

    private static Logger logger = LoggerFactory.getLogger(SpuController.class);

    @Reference
    private SpuService spuService;


    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id) {
        return spuService.spuList(catalog3Id);
    }

    /**
     * 保存spu数据
     * @param pmsProductInfo
     * @return
     */
    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }

    /**
     * 文件上传
     * @param multipartFile
     * @return
     */
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return "出错啦";
        }

        //将图片上传到分布式文件存储系统
//        String uploadImage = UploadUtil.uploadImage(multipartFile);
        try {
            String saveFile = this.saveFile(multipartFile);
            return saveFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将图片路径返回前端
        return null;
    }

    /**
     * 获取销售属性列表.
     * @param spuId SPU id
     * @return      属性集合
     */
    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        return spuService.spuSaleAttrList(spuId);
    }

    /**
     * 获取销售的照片.
     * @param spuId SPUID
     * @return      图片集合
     */
    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId) {
        return spuService.spuImageList(spuId);
    }


    /**
     * 保存文件.
     *
     * @param multipartFile
     * @return
     */
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if (null != inputStream) {
            int available = inputStream.available();
            file_buff = new byte[available];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile fastDFSFile = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = FastDFSClient.upload(fastDFSFile);
        } catch (Exception e) {
            logger.error("upload file Exception!", e);
        }
        if (null == fileAbsolutePath) {
            logger.error("upload file failed, Please upload again!");
        }
        String path = FastDFSClient.getTrackerUrl() + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
        return path;
    }

}
