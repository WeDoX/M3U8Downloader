package com.onedream.jdm3u8downloader.file_downloader;

/**
 * 创建{@link JDM3U8FileAbstractDownloader}的工厂类
 *
 * @author jdallen
 * @since 2021/2/3
 */
public abstract class JDM3U8FileAbstractDownloaderFactory<P extends JDM3U8FileAbstractDownloader> {

    public abstract P createDownloader();
}