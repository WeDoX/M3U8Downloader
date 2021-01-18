package com.onedream.jdm3u8downloader;

import android.os.AsyncTask;

import com.onedream.jdm3u8downloader.base.JDM3U8BaseDownloader;
import com.onedream.jdm3u8downloader.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.common.JDM3U8TsDownloadState;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;

import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8TsFileDownloadTask extends AsyncTask<Integer, Integer, Integer> {
    public JDM3U8BaseDownloader m3U8BaseDownloader;
    public JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean;
    public List<String> tsUrlPathList;
    //
    private String oldString = "";

    @Override
    protected Integer doInBackground(Integer... params) {
        int failureCount = 0;
        int successCount = 0;
        int currentTsNum = 1;//当前是第几个ts
        long itemLength = 0;//每个item的大小
        for (String tsFileUrl : tsUrlPathList) {
            if (!m3U8BaseDownloader.canDownloadTsFile()) {//取消下载
                failureCount = -1;
                break;
            }
            JDM3U8TsBean m3U8TsBean = new JDM3U8TsBean(m3U8SingleRateFileDownloadUrlBean, tsFileUrl);
            oldString = m3U8TsBean.getOldString();
            //3、下载ts文件
            int ret = m3U8BaseDownloader.downLoadTsFile(m3U8TsBean);
            //
            if (ret == JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_SUCCESS) {
                successCount++;
                //由于每个ts文件的大小基本是固定的（头尾有点差距），可以通过单个文件的大小来算整个文件的大小
                if (itemLength == 0 && currentTsNum > 1 && currentTsNum < tsUrlPathList.size()) {
                    itemLength = m3U8BaseDownloader.getTsFileSize(m3U8TsBean);
                    JDM3U8LogHelper.printLog("以第" + currentTsNum + "个的大小：" + itemLength + "计算总大小");
                }
                m3U8BaseDownloader.postDownloadProgress(successCount, tsUrlPathList.size(), itemLength);
            } else {
                failureCount++;
            }
            currentTsNum++;
        }//end for:

        JDM3U8LogHelper.printLog("failureCount  = " + failureCount +"当前ts数量：" +currentTsNum);
        return failureCount;
    }


    @Override
    protected void onPostExecute(Integer failureCount) {
        super.onPostExecute(failureCount);
        if (failureCount > 0) {
            m3U8BaseDownloader.postDownloadErrorEvent("失败下载次数" + failureCount);
        } else if (failureCount == 0) {
            m3U8BaseDownloader.downloadSuccessEvent();
            //保存一份供本地播放的单码率m3u8文件
            m3U8BaseDownloader.saveLocalM3U8SingleRate(oldString);
        } else if (failureCount == -1) {//手动暂停不处理
            JDM3U8LogHelper.printLog("手动暂停");
            m3U8BaseDownloader.pauseDownload();
        }
        m3U8BaseDownloader.downloadFinish();
    }
}