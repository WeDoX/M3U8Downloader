package com.onedream.jdm3u8downloader.download_ts_file;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.bean.state.JDDownloadQueueState;
import com.onedream.jdm3u8downloader.bean.state.JDM3U8TsDownloadState;
import com.onedream.jdm3u8downloader.file_downloader.JDM3U8FileAbstractDownloader;
import com.onedream.jdm3u8downloader.file_local_storage_manager.JDM3U8FileLocalStorageManager;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;

import java.io.File;
import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8TsFileDownloadTask extends AsyncTask<Integer, Integer, Integer> {
    public JDDownloadQueue downloadQueue;
    public List<JDM3U8TsBean> m3U8TsBeanList;
    public @Nullable
    JDM3U8DownloaderContract.DownloadStateCallback downloadStateCallback;
    public JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener jdm3U8DownloadFullSuccessListener;
    public JDM3U8FileAbstractDownloader fileDownloader;
    public JDM3U8FileLocalStorageManager fileLocalStorageManager;

    //
    private String oldString = "";

    @Override
    protected Integer doInBackground(Integer... params) {
        int failureCount = 0;
        int successCount = 0;
        int currentTsNum = 1;//当前是第几个ts
        long itemLength = 0;//每个item的大小
        for (JDM3U8TsBean m3U8TsBean : m3U8TsBeanList) {
            if (downloadQueue.getState() == JDDownloadQueueState.STATE_DOWNLOAD_NO) {//取消下载
                failureCount = -1;
                break;
            }
            oldString = m3U8TsBean.getOldString();
            //3、下载ts文件
            int ret = downLoadTsFile(m3U8TsBean);
            //
            if (ret == JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_SUCCESS) {
                successCount++;
                //由于每个ts文件的大小基本是固定的（头尾有点差距），可以通过单个文件的大小来算整个文件的大小
                if (itemLength == 0 && currentTsNum > 1 && currentTsNum < m3U8TsBeanList.size()) {
                    itemLength = getTsFileSize(m3U8TsBean);
                    JDM3U8LogHelper.printLog("以第" + currentTsNum + "个的大小：" + itemLength + "计算总大小");
                }
                if (null != downloadStateCallback) {
                    downloadStateCallback.postDownloadProgressEvent(successCount, m3U8TsBeanList.size(), itemLength);
                }
            } else {
                failureCount++;
            }
            currentTsNum++;
        }//end for:

        JDM3U8LogHelper.printLog("failureCount  = " + failureCount + "当前ts数量：" + currentTsNum);
        return failureCount;
    }

    private int downLoadTsFile(JDM3U8TsBean m3U8TsBean) {
        File tsSaveFile = fileLocalStorageManager.getTsFile(downloadQueue, m3U8TsBean);
        if (null != tsSaveFile) {
            JDM3U8LogHelper.printLog("该电影" + downloadQueue.getMovie_id() + "的" + m3U8TsBean.getTsFileName() + "文件已经存在");
            return JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_SUCCESS;
        }
        tsSaveFile = fileLocalStorageManager.getTsFileAndIsEmptyNeedCreate(downloadQueue, m3U8TsBean);
        if (null == tsSaveFile) {
            JDM3U8LogHelper.printLog("创建ts文件失败");
            return JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_FAILURE;
        }
        return fileDownloader.downLoadTsFile(m3U8TsBean, tsSaveFile);
    }


    private long getTsFileSize(JDM3U8TsBean m3U8TsBean) {
        File file = fileLocalStorageManager.getTsFileAndIsEmptyNeedCreate(downloadQueue, m3U8TsBean);
        if (null != file) {
            return file.length();
        }
        return 0;
    }


    @Override
    protected void onPostExecute(Integer failureCount) {
        super.onPostExecute(failureCount);
        if (failureCount > 0) {
            if (null != downloadStateCallback) {
                downloadStateCallback.postDownloadErrorEvent("失败下载次数" + failureCount);
            }
        } else if (failureCount == 0) {
            if (null != downloadStateCallback) {
                downloadStateCallback.postDownloadSuccessEvent();
            }
            //保存一份供本地播放的单码率m3u8文件
            jdm3U8DownloadFullSuccessListener.downloadFullSuccessSaveLocalM3U8SingleRate(oldString);
        } else if (failureCount == -1) {//手动暂停不处理
            JDM3U8LogHelper.printLog("手动暂停");
            if (null != downloadStateCallback) {
                downloadStateCallback.postDownloadPauseEvent();
            }
        }
        if (null != downloadStateCallback) {
            downloadStateCallback.postDownloadCloseEvent();
        }
    }


}