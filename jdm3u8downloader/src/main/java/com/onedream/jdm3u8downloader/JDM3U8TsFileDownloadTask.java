package com.onedream.jdm3u8downloader;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.listener.DownloadStateCallback;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.common.JDM3U8TsDownloadState;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;

import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8TsFileDownloadTask extends AsyncTask<Integer, Integer, Integer> {
    public List<JDM3U8TsBean> m3U8TsBeanList;
    public TsFileDownloadCallback tsFileDownloadCallback;
    public @Nullable DownloadStateCallback downloadStateCallback;
    public JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener jdm3U8DownloadFullSuccessListener;

    //
    private String oldString = "";

    @Override
    protected Integer doInBackground(Integer... params) {
        int failureCount = 0;
        int successCount = 0;
        int currentTsNum = 1;//当前是第几个ts
        long itemLength = 0;//每个item的大小
        for (JDM3U8TsBean m3U8TsBean : m3U8TsBeanList) {
            if (!tsFileDownloadCallback.canDownloadTsFile()) {//取消下载
                failureCount = -1;
                break;
            }
            oldString = m3U8TsBean.getOldString();
            //3、下载ts文件
            int ret = tsFileDownloadCallback.downLoadTsFile(m3U8TsBean);
            //
            if (ret == JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_SUCCESS) {
                successCount++;
                //由于每个ts文件的大小基本是固定的（头尾有点差距），可以通过单个文件的大小来算整个文件的大小
                if (itemLength == 0 && currentTsNum > 1 && currentTsNum < m3U8TsBeanList.size()) {
                    itemLength = tsFileDownloadCallback.getTsFileSize(m3U8TsBean);
                    JDM3U8LogHelper.printLog("以第" + currentTsNum + "个的大小：" + itemLength + "计算总大小");
                }
                if(null != downloadStateCallback){
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


    @Override
    protected void onPostExecute(Integer failureCount) {
        super.onPostExecute(failureCount);
        if (failureCount > 0) {
            if(null != downloadStateCallback) {
                downloadStateCallback.postDownloadErrorEvent("失败下载次数" + failureCount);
            }
        } else if (failureCount == 0) {
            if(null != downloadStateCallback) {
                downloadStateCallback.postDownloadSuccessEvent();
            }
            //保存一份供本地播放的单码率m3u8文件
            jdm3U8DownloadFullSuccessListener.downloadFullSuccessSaveLocalM3U8SingleRate(oldString);
        } else if (failureCount == -1) {//手动暂停不处理
            JDM3U8LogHelper.printLog("手动暂停");
            if(null != downloadStateCallback) {
                downloadStateCallback.postDownloadPauseEvent();
            }
        }
        if(null != downloadStateCallback) {
            downloadStateCallback.postDownloadCloseEvent();
        }
    }


    public interface TsFileDownloadCallback {
        //是否继续下载ts文件
        boolean canDownloadTsFile();

        //下载单个ts文件
        int downLoadTsFile(JDM3U8TsBean m3U8TsBean);

        //获取指定的ts文件大小
        long getTsFileSize(JDM3U8TsBean m3U8TsBean);
    }
}