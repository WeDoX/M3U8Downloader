package com.onedream.jdm3u8downloader.downloader;

/**
 * 创建{@link JDM3U8OriginalDownloader}的工厂类
 *
 * @author jdallen
 * @since 2021/2/3
 */
public class JDM3U8OriginalDownloaderFactory extends JDM3U8AbstractDownloaderFactory<JDM3U8OriginalDownloader> {

    public static JDM3U8OriginalDownloaderFactory create() {
        return new JDM3U8OriginalDownloaderFactory();
    }

    @Override
    public JDM3U8OriginalDownloader createDownloader() {
        return new JDM3U8OriginalDownloader();
    }
}
