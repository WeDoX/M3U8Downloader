package com.onedream.jdm3u8downloader.listener;

public interface DownloadStateCallback {

    //发布下载进度信息
    void postDownloadProgressEvent(int successCount, int tsFileCount, long itemLength);

    //暂停下载
    void postDownloadPauseEvent();

    //发布下载错误信息
    void postDownloadErrorEvent(String errMsg);

    //发布下载成功信息
    void postDownloadSuccessEvent();

    //下载流程完成（下载失败或下载成功）
    void postDownloadCloseEvent();
}
