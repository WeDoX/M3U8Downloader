package com.onedream.jdm3u8downloader.file_downloader.imp;

import com.onedream.jdm3u8downloader.file_downloader.JDM3U8FileAbstractDownloaderFactory;

/**
 * 创建{@link JDM3U8FileOriginalDownloader}的工厂类
 *
 * @author jdallen
 * @since 2021/2/3
 */
public class JDM3U8FileOriginalDownloaderFactory extends JDM3U8FileAbstractDownloaderFactory<JDM3U8FileOriginalDownloader> {

    public static JDM3U8FileOriginalDownloaderFactory create() {
        return new JDM3U8FileOriginalDownloaderFactory();
    }

    @Override
    public JDM3U8FileOriginalDownloader createDownloader() {
        return new JDM3U8FileOriginalDownloader();
    }
}
