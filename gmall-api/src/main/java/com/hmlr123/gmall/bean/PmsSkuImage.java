package com.hmlr123.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @param
 * @return
 */
public class PmsSkuImage implements Serializable {


    //主键ID
    @Id
    @Column
    String id;

    //SKU id
    @Column
    String skuId;

    //图片名称
    @Column
    String imgName;

    //图片路径
    @Column
    String imgUrl;

    //sku图片id
    @Column
    String skuImgId;

    //是否默认
    @Column
    String isDefault;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSkuImgId() {
        return skuImgId;
    }

    public void setSkuImgId(String skuImgId) {
        this.skuImgId = skuImgId;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}