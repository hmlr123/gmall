package com.hmlr123.gmall.bean;

/**
 * FastDFSFile上传实体类.
 *
 * @author liwei
 * @date 2019/9/2 12:43
 */
public class FastDFSFile {

    //文件名
    private String name;
    //文件内容
    private byte[] content;
    //文件后缀名
    private String ext;
    //加密
    private String md5;
    //作者
    private String author;

    public FastDFSFile() {
    }

    public FastDFSFile(String fileName, byte[] file_buff, String ext) {
        this.name = fileName;
        this.content = file_buff;
        this.ext = ext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
