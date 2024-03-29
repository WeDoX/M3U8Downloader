package com.onedream.jdm3u8downloader;

import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.core.JDM3U8BaseDownloader;
import com.onedream.jdm3u8downloader.core.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.core.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.core.bean.JDM3U8TsDownloadUrlBean;
import com.onedream.jdm3u8downloader.core.bean.state.JDDownloadQueueState;
import com.onedream.jdm3u8downloader.ability.model_converter.JDM3U8ModelConverter;
import com.onedream.jdm3u8downloader.core.download_ts_file.JDM3U8TsFileDownloadTask;
import com.onedream.jdm3u8downloader.ability.file_downloader.JDM3U8FileAbstractDownloader;
import com.onedream.jdm3u8downloader.ability.file_local_storage_manager.JDM3U8FileLocalStorageManager;
import com.onedream.jdm3u8downloader.core.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.core.utils.JDM3U8LogHelper;

import java.util.List;

/**
 * @author jdallen
 * @since 2022/12/15
 */
class JDM3U8RealDownloader extends JDM3U8BaseDownloader {

    private final JDDownloadQueue downloadQueue;
    private final JDM3U8DownloaderContract.JDM3U8DownloadBaseListener getM3U8FileListener;
    private final JDM3U8FileAbstractDownloader fileDownloader;
    private final JDM3U8ModelConverter modelConvert;
    private final JDM3U8FileLocalStorageManager fileLocalStorageManager;

    public JDM3U8RealDownloader(JDDownloadQueue downloadQueue,
                                @NonNull JDM3U8DownloaderContract.JDM3U8DownloadBaseListener getM3U8FileListener,
                                JDM3U8FileAbstractDownloader fileDownloader,
                                @NonNull JDM3U8ModelConverter modelConvert,
                                @NonNull JDM3U8FileLocalStorageManager fileLocalStorageManager) {
        this.downloadQueue = downloadQueue;
        this.getM3U8FileListener = getM3U8FileListener;
        this.fileDownloader = fileDownloader;
        this.modelConvert = modelConvert;
        this.fileLocalStorageManager = fileLocalStorageManager;
    }


    @Override
    public void downloadM3U8MultiRateFileContent(String urlPath, @NonNull final JDM3U8DownloaderContract.GetM3U8SingleRateContentListener baseDownloadListener) {
        //判断本地是否已经有该多码率文件
        List<String> fileContentList = fileLocalStorageManager.getM3u8MultiRateFileContent(downloadQueue);
        if (null != fileContentList && fileContentList.size() > 0) {
            JDM3U8LogHelper.printLog("该电影的顶级M3U8多码率文件已经存在，不需要再次请求网络，内容为：" + fileContentList.toString());
            baseDownloadListener.downloadSuccess(fileContentList, false);
            return;
        }
        fileDownloader.downloadM3U8MultiRateFileContent(urlPath, baseDownloadListener);
    }


    @Override
    public JDM3U8SingleRateUrlBean covertToM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> dataList) {
        return modelConvert.covertToM3U8SingleRateUrlBean(m3u8MultiRateFileDownloadUrl, dataList);
    }

    @Override
    public void downloadM3U8SingleRateFileContent(JDM3U8SingleRateUrlBean m3u8FileUrlBean, @NonNull final JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener) {
        //判断本地是否已经有该单码率文件
        String m3u8Content = fileLocalStorageManager.getM3U8SingleRateFileContent(downloadQueue);
        if (!TextUtils.isEmpty(m3u8Content)) {
            JDM3U8LogHelper.printLog("该电影的M3U8单码率文件已经存在，不需要再次请求网络，内容为：" + m3u8Content);
            baseDownloadListener.downloadSuccess(m3u8Content);
            return;
        }
        fileDownloader.downloadM3U8SingleRateFileContent(m3u8FileUrlBean.getM3u8FileDownloadUrl(), baseDownloadListener);
    }


    @Override
    public List<JDM3U8TsDownloadUrlBean> convertToM3U8TsDownloadUrlBeanList(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String M3U8SingleRateFileContent) {
        return modelConvert.convertToM3U8TsDownloadUrlBeanList(m3U8SingleRateFileDownloadUrlBean, M3U8SingleRateFileContent);
    }

    @Override
    public void downloadM3U8AllTsFile(List<JDM3U8TsDownloadUrlBean> tsUrlPathList, @Nullable JDM3U8DownloaderContract.DownloadStateCallback downloadStateCallback, JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener jdm3U8DownloadFullSuccessListener) {
        //
        downloadQueue.setState(JDDownloadQueueState.STATE_DOWNLOAD_ING);
        //
        JDM3U8TsFileDownloadTask downloadTask = new JDM3U8TsFileDownloadTask();
        downloadTask.downloadQueue = downloadQueue;
        downloadTask.m3U8TsBeanList = tsUrlPathList;
        downloadTask.downloadStateCallback = downloadStateCallback;
        downloadTask.jdm3U8DownloadFullSuccessListener = jdm3U8DownloadFullSuccessListener;
        downloadTask.fileDownloader = fileDownloader;
        downloadTask.fileLocalStorageManager = fileLocalStorageManager;
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void saveLocalM3U8SingleRateFile(String oldString) {
        fileLocalStorageManager.saveLocalM3U8SingleRate(downloadQueue, oldString);
    }


    public void startDownload() {
        if (downloadQueue.isSingleRate()) {
            super.startDownloadSingleRateM3U8(downloadQueue, downloadStateCallback, fileLocalStorageManager);
        } else {
            super.startDownloadMultiRateM3U8(downloadQueue, downloadStateCallback, fileLocalStorageManager);
        }
    }


    private final JDM3U8DownloaderContract.DownloadStateCallback downloadStateCallback = new JDM3U8DownloaderContract.DownloadStateCallback() {
        @Override
        public void postDownloadProgressEvent(int successCount, int tsFileCount, long itemLength) {
            if (downloadQueue.getState() == JDDownloadQueueState.STATE_DOWNLOAD_ING || downloadQueue.getState() == JDDownloadQueueState.STATE_DOWNLOAD_FINISH) {
                getM3U8FileListener.downloadProgress(downloadQueue, successCount * itemLength, tsFileCount * itemLength);
            }
        }

        @Override
        public void postDownloadErrorEvent(String errMsg) {
            if (getM3U8FileListener instanceof JDM3U8DownloaderContract.JDM3U8DownloadListener) {
                ((JDM3U8DownloaderContract.JDM3U8DownloadListener) getM3U8FileListener).downloadError(downloadQueue, errMsg);
            }
            getM3U8FileListener.downloadState(downloadQueue, JDDownloadQueueState.STATE_DOWNLOAD_ERROR, errMsg);
        }

        @Override
        public void postDownloadSuccessEvent() {
            if (getM3U8FileListener instanceof JDM3U8DownloaderContract.JDM3U8DownloadListener) {
                ((JDM3U8DownloaderContract.JDM3U8DownloadListener) getM3U8FileListener).downloadSuccess(downloadQueue);
            }
            getM3U8FileListener.downloadState(downloadQueue, JDDownloadQueueState.STATE_DOWNLOAD_SUCCESS, JDDownloadQueueState.getSateStr(JDDownloadQueueState.STATE_DOWNLOAD_SUCCESS));
        }

        @Override
        public void postDownloadCloseEvent() {
            getM3U8FileListener.downloadState(downloadQueue, JDDownloadQueueState.STATE_DOWNLOAD_FINISH, JDDownloadQueueState.getSateStr(JDDownloadQueueState.STATE_DOWNLOAD_FINISH));
        }


        @Override
        public void postDownloadPauseEvent() {
            if (getM3U8FileListener instanceof JDM3U8DownloaderContract.JDM3U8DownloadListener) {
                ((JDM3U8DownloaderContract.JDM3U8DownloadListener) getM3U8FileListener).downloadPause(downloadQueue);
            }
            getM3U8FileListener.downloadState(downloadQueue, JDDownloadQueueState.STATE_DOWNLOAD_PAUSE, JDDownloadQueueState.getSateStr(JDDownloadQueueState.STATE_DOWNLOAD_PAUSE));
        }
    };

}