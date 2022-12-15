package com.onedream.jdm3u8downloader.bean.state;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8DownloadHintMessage {
    public static final String M3U8MultiRateFile_DOWNLOAD_ERROR = "该电影的顶级M3U8多码率文件下载失败";
    public static final String M3U8SingleRateFile_DOWNLOAD_ERROR = "该电影的M3U8文件下载失败";
    public static final String M3U8MultiRateFileContent_IS_EMPTY = "下载出错，该电影的m3u8多码率文件内容为空";
    public static final String M3U8SingleRateURL_IS_EMPTY = "下载出错，该电影的M3U8单码率下载地址为空";
    public static final String M3U8SingleRateFileContent_IS_EMPTY = "下载出错，该电影的M3U8单码率文件内容为空";
    public static final String M3U8_TS_LIST_IS_EMPTY = "下载出错，M3U8文件内容没有ts文件列表";
}
