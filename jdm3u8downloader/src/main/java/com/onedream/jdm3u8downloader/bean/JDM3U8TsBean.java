package com.onedream.jdm3u8downloader.bean;


import com.onedream.jdm3u8downloader.utils.JDM3U8AnalysisUtils;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;

/**
 * @author jdallen
 * @since 2020/4/3
 */
// 在单码率m3u8文件ts列表的ts文件下载地址
// 如果是以/开头的话，使用单码率的下载地址url主机加该下载地址
// 如果不是以/开头的话，截取单码率的url到最后一个/杠,再加该下载地址
public class JDM3U8TsBean {
    private String tsFileUrl;//在单码率m3u8文件ts列表的ts文件下载地址
    private String preUrlPath;//
    private String tsFileName;//ts文件储存名称
    private String oldString;//tsFileUrl与tsFileName多出的字符串，这个字段只要是供后面保存一份本地可以播放的单码率m3u8文件准备的

    public JDM3U8TsBean(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String tsFileUrl) {
        this.tsFileUrl = tsFileUrl;
        if (tsFileUrl.startsWith("/")) {
            preUrlPath = JDM3U8AnalysisUtils.getHostAddressByUrlStr(m3U8SingleRateFileDownloadUrlBean.getM3u8FileDownloadUrl());
            String[] name = JDM3U8AnalysisUtils.getTsOldStrAndFileNameStr(tsFileUrl);
            oldString = name[0];
            tsFileName = name[1];
        } else {
            preUrlPath = JDM3U8AnalysisUtils.getRelativePathByUrlStr(m3U8SingleRateFileDownloadUrlBean.getM3u8FileDownloadUrl());
            tsFileName = tsFileUrl;
        }
        //
        JDM3U8LogHelper.printLog("前缀：" + preUrlPath + "<==>ts下载路径:" + tsFileUrl + "\n完整的下载路径：" + preUrlPath + tsFileUrl);
    }

    public String getTsFileName() {
        return tsFileName;
    }

    public String getOldString() {
        return oldString;
    }

    public String getFullUrl() {
        return preUrlPath + tsFileUrl;
    }
}
