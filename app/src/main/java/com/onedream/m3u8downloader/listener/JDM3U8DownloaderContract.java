package com.onedream.m3u8downloader.listener;


import com.onedream.m3u8downloader.bean.JDDownloadMessage;
import com.onedream.m3u8downloader.bean.JDDownloadProgress;
import com.onedream.m3u8downloader.bean.JDDownloadQueue;

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
        void downloadSuccess(String content, List<String> dataList, boolean isNeedSaveFile);//isNeedSaveFile为false时，content可以为空

        void downloadFailure(String errMsg);
    }

    interface GetM3U8FileListener {
        void downloadErrorEvent(JDDownloadMessage message);

        void postEvent(JDDownloadProgress progress);

        void downloadSuccessEvent(JDDownloadQueue downloadQueue);

        void removeDownloadQueueEvent(JDDownloadQueue downloadQueue);

        void pauseDownload(JDDownloadQueue downloadQueue);
    }

}
