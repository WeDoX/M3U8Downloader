package com.onedream.jdm3u8downloader.file_downloader;

/**
 * 此接口使用方法：
 * 1.继承{@link JDM3U8FileAbstractDownloader}扩展自己的M3U8文件下载器。
 * 2.继承此接口并实现{@link #createDownloader()}，返回步骤1中的M3U8文件下载器。
 * 可参照{@link JDM3U8FileOriginalDownloader}和{@link JDM3U8FileOriginalDownloaderFactory}的实现。
 */
public abstract class JDM3U8FileAbstractDownloaderFactory<P extends JDM3U8FileAbstractDownloader> {

    public abstract P createDownloader();
}