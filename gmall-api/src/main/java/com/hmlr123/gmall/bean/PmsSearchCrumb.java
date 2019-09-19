package com.hmlr123.gmall.bean;

import java.io.Serializable;

/**
 * 面包屑.
 *
 * @author liwei
 * @date 2019/9/8 21:50
 */
public class PmsSearchCrumb implements Serializable {

    private String valueId;

    private String valueName;

    private String urlParam;

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }
}
