package com.onedream.m3u8downloader.okhttp_file_downloader;

import com.onedream.jdm3u8downloader.ability.file_downloader.JDM3U8FileAbstractDownloaderFactory;

/**
 * 创建{@link OkHttpFileDownloader}的工厂类
 *
 * @author jdallen
 * @since 2021/2/3
 */
public class OkHttpFileDownloaderFactory extends JDM3U8FileAbstractDownloaderFactory<OkHttpFileDownloader> {

    public static OkHttpFileDownloaderFactory create() {
        return new OkHttpFileDownloaderFactory();
    }

    @Override
    public OkHttpFileDownloader createDownloader() {
        return new OkHttpFileDownloader();
    }
}
