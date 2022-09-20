package com.onedream.jdm3u8downloader.bean;

/**
 * 单码率下载路径类
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8SingleRateUrlBean {
    private final String m3u8FileDownloadUrl;

    public JDM3U8SingleRateUrlBean(String m3u8FileDownloadUrl) {
        this.m3u8FileDownloadUrl = m3u8FileDownloadUrl;
    }

    public String getM3u8FileDownloadUrl() {
        return m3u8FileDownloadUrl;
    }
}
