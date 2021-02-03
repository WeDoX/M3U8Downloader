package com.onedream.m3u8downloader.okhttp_downloader;

import com.onedream.jdm3u8downloader.downloader.JDM3U8AbstractDownloaderFactory;

/**
 * 创建{@link OkHttpDownloader}的工厂类
 *
 * @author jdallen
 * @since 2021/2/3
 */
public class OkHttpDownloaderFactory extends JDM3U8AbstractDownloaderFactory<OkHttpDownloader> {

    public static OkHttpDownloaderFactory create() {
        return new OkHttpDownloaderFactory();
    }

    @Override
    public OkHttpDownloader createDownloader() {
        return new OkHttpDownloader();
    }
}
