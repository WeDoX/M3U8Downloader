package com.onedream.jdm3u8downloader.core.listener;


import com.onedream.jdm3u8downloader.core.bean.JDDownloadQueue;

import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public interface JDM3U8DownloaderContract {

    interface BaseDownloadListener {
        void downloadSuccess(String content);

        void downloadFailure(String errMsg);
    }

    interface GetM3U8SingleRateContentListener {
        void downloadSuccess(List<String> dataList, boolean isNeedSaveFile);//isNeedSaveFile为false时，content可以为空

        void downloadFailure(String errMsg);
    }

    interface JDM3U8DownloadBaseListener {
        void downloadState(JDDownloadQueue downloadQueue, int downloadState, String msg);

        void downloadProgress(JDDownloadQueue downloadQueue, long sofar, long total);
    }

    interface JDM3U8DownloadListener extends JDM3U8DownloadBaseListener {
        void downloadSuccess(JDDownloadQueue downloadQueue);

        void downloadError(JDDownloadQueue downloadQueue, String errMsg);

        void downloadPause(JDDownloadQueue downloadQueue);
    }

    interface JDM3U8DownloadFullSuccessListener{
        void downloadFullSuccessSaveLocalM3U8SingleRate(String oldString);
    }

    interface DownloadStateCallback {

        //发布下载进度事件
        void postDownloadProgressEvent(int successCount, int tsFileCount, long itemLength);

        //发布下载已暂停事件
        void postDownloadPauseEvent();

        //发布下载错误事件
        void postDownloadErrorEvent(String errMsg);

        //发布下载成功事件
        void postDownloadSuccessEvent();

        //下载流程完成（下载失败或下载成功）事件
        void postDownloadCloseEvent();
    }

}
