package com.onedream.m3u8downloader.bean;

import android.text.TextUtils;


import com.onedream.m3u8downloader.utils.JDM3U8FileCacheUtils;
import com.onedream.m3u8downloader.utils.JDM3U8AnalysisUtils;
import com.onedream.m3u8downloader.utils.JDM3U8LogHelper;

import java.io.File;
import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 */
//多码率中第三行内容（即单码率的m3u8的路径）
// 如果是以/开头的话，使用多码率的下载地址url主机加该内容；
// 如果不是以/开头的话，截取多码率的url到最后一个/杠，再加多码率中第三行内容
public class JDM3U8SingleRateUrlBean {//单码率下载路径类
    private String m3u8FileDownloadUrl;

    public JDM3U8SingleRateUrlBean(String m3u8FileDownloadUrl) {
        this.m3u8FileDownloadUrl = m3u8FileDownloadUrl;
    }

    //根据m3u8多码率文件内容获取单码率m3u8文件下载地址
    public static JDM3U8SingleRateUrlBean getM3U8FileUrl(String m3u8MultiRateFileDownloadUrl, File m3u8MultiRateFile) {
        List<String> dataList = JDM3U8FileCacheUtils.fileContentToStrList(m3u8MultiRateFile);
        return getM3U8FileUrl(m3u8MultiRateFileDownloadUrl, dataList);
    }

    public static JDM3U8SingleRateUrlBean getM3U8FileUrl(String m3u8MultiRateFileDownloadUrl, List<String> dataList) {
        JDM3U8SingleRateUrlBean downloadUrlBean = null;
        if (null != dataList && dataList.size() >= 3) {
            String link = dataList.get(2);//第三行
            if (TextUtils.isEmpty(link)) {
                return null;
            }
            if (link.startsWith("/")) {
                String preUrlPath = JDM3U8AnalysisUtils.getHostAddressByUrlStr(m3u8MultiRateFileDownloadUrl);
                downloadUrlBean = new JDM3U8SingleRateUrlBean( preUrlPath + link);
                JDM3U8LogHelper.printLog("m3u8多码率的域名为:" + preUrlPath);
                JDM3U8LogHelper.printLog("link开始有“/”m3u8单码率的完整路径为:" + preUrlPath + link);

            } else {
                String preUrlPath = JDM3U8AnalysisUtils.getRelativePathByUrlStr(m3u8MultiRateFileDownloadUrl);
                downloadUrlBean = new JDM3U8SingleRateUrlBean(preUrlPath + link);
                JDM3U8LogHelper.printLog("link开始没有“/”m3u8单码率的完整路径为:" + preUrlPath + link);
            }
        }
        return downloadUrlBean;
    }

    public String getM3u8FileDownloadUrl() {
        return m3u8FileDownloadUrl;
    }
}
