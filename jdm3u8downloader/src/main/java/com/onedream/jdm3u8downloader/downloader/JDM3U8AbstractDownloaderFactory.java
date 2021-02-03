package com.onedream.jdm3u8downloader.downloader;

/**
 * 此接口使用方法：
 * 1.继承{@link JDM3U8AbstractDownloader}扩展自己的M3U8下载器。
 * 2.继承此接口并实现{@link #createDownloader()}，返回步骤1中的M3U8下载器。
 * 可参照{@link JDM3U8OriginalDownloader}和{@link JDM3U8OriginalDownloaderFactory}的实现。
 */
public abstract class JDM3U8AbstractDownloaderFactory<P extends JDM3U8AbstractDownloader> {

    public abstract P createDownloader();
}