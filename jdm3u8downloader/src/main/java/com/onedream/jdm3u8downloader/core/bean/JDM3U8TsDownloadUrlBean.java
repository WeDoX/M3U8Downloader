package com.onedream.jdm3u8downloader.core.bean;

/**
 * @author jdallen
 * @since 2020/4/3
 */
// 在单码率m3u8文件ts列表的ts文件下载地址
// 如果是以/开头的话，使用单码率的下载地址url主机加该下载地址
// 如果不是以/开头的话，截取单码率的url到最后一个/杠,再加该下载地址
public class JDM3U8TsDownloadUrlBean {
    private final String tsFileName;//ts文件储存名称
    private final String oldString;//tsFileUrl与tsFileName多出的字符串，这个字段只要是供后面保存一份本地可以播放的单码率m3u8文件准备的
    private final String fullUrlString;

    public JDM3U8TsDownloadUrlBean(String tsFileName, String oldString, String fullUrlString) {
        this.tsFileName = tsFileName;
        this.oldString = oldString;
        this.fullUrlString = fullUrlString;
    }

    public String getTsFileName() {
        return tsFileName;
    }

    public String getOldString() {
        return oldString;
    }

    public String getFullUrl() {
        return fullUrlString;
    }
}
