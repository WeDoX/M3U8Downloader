package com.onedream.jdm3u8downloader.listener;

public interface FileSaveCallback {
    //保存多码率文件
    void saveM3U8MultiRateFile(String content);

    //保存单码率文件
    void saveM3U8SingleRateFile(String content);
}
