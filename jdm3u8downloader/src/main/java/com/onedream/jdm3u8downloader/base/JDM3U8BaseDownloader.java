package com.onedream.jdm3u8downloader.base;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.onedream.jdm3u8downloader.common.JDM3U8DownloadHintMessage;
import com.onedream.jdm3u8downloader.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.utils.JDM3U8AnalysisUtils;
import com.onedream.jdm3u8downloader.utils.JDM3U8FileCacheUtils;

import java.io.File;
import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 * <p>
 * 0、网络请求获取到m3u8多码率的文件内容
 * 1、得到单码率的下载地址(JDM3U8SingleRateUrlBean)
 * 2、网络请求获取到m3u8单码率的文件内容
 * 3、得到ts列表的下载地址
 * 4、下载各个ts文件
 * 5、保存一份供本地播放的单码率m3u8文件
 */
public abstract class JDM3U8BaseDownloader {
    //下载获取多码率文件内容
    public abstract void getM3U8MultiRateFileContent(String urlPath, @NonNull JDM3U8DownloaderContract.GetM3U8SingleRateContentListener getM3U8SingleRateContentListener);

    //获取单码率文件下载地址对象
    public abstract JDM3U8SingleRateUrlBean getM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> dataList);

    //下载获取单码率文件内容
    public abstract void getM3U8SingleRateFileContent(JDM3U8SingleRateUrlBean m3u8FileUrlBean, @NonNull JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener);

    //得到ts列表的下载地址

    /**
     * 根据单码率的m3u8文件内容，获取ts列表
     */
    public List<String> getM3U8TsBeanList(String M3U8SingleRateFileContent) {
        return JDM3U8AnalysisUtils.analysisIndex(M3U8SingleRateFileContent);
    }


    //下载各个ts文件
    public abstract void downloadM3U8Ts(List<String> tsUrlPathList, JDM3U8SingleRateUrlBean m3u8FileUrlBean);

    //保存一份供本地播放的单码率m3u8文件
    public abstract void saveLocalM3U8SingleRate(String oldString);

    //保存多码率文件
    public abstract void saveM3U8MultiRateFile(String content);

    //保存单码率文件
    public abstract void saveM3U8SingleRateFile(String content);

    //是否继续下载ts文件
    public abstract boolean canDownloadTsFile();



    //获取下载单个ts文件对象
    public abstract JDM3U8TsBean getJDM3U8TsBean(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String tsFileUrl);

    //下载单个ts文件
    public abstract int downLoadTsFile(JDM3U8TsBean m3U8TsBean);

    //
    //发布下载进度信息
    public abstract void postDownloadProgress(int successCount, int tsFileCount, long itemLength);

    //发布下载错误信息
    public abstract void postDownloadErrorEvent(String errMsg);

    //发布下载成功信息
    public abstract void downloadSuccessEvent();

    //下载流程完成（下载失败或下载成功）
    public abstract void downloadFinish();

    //获取指定的ts文件大小
    public abstract long getTsFileSize(JDM3U8TsBean m3U8TsBean);

    //暂停下载
    public abstract void pauseDownload();


    /**
     * 全部替换目标文件的某个字符串并生成一个新的文件
     */
    public void alterStringToCreateNewFile(File m3u8File, File m3u8LocalFile, String oldString, String newString) {
        JDM3U8FileCacheUtils.alterStringToCreateNewFile(m3u8File, m3u8LocalFile, oldString, newString);
    }


    public void startDownloadMultiRateM3U8(final String multiRateM3U8DownloadUrl) {
        //0、网络请求获取到m3u8多码率的文件内容
        getM3U8MultiRateFileContent(multiRateM3U8DownloadUrl, new JDM3U8DownloaderContract.GetM3U8SingleRateContentListener() {
            @Override
            public void downloadSuccess(String content, List<String> dataList, boolean isNeedSaveFile) {
                if (null == dataList || dataList.isEmpty()) {
                    postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8MultiRateFileContent_IS_EMPTY);
                    return;
                }

                if (isNeedSaveFile && !TextUtils.isEmpty(content)) {
                    //保存多码率的m3u8文件
                    saveM3U8MultiRateFile(content);
                }
                //1、得到单码率的下载地址(JDM3U8SingleRateUrlBean)
                JDM3U8SingleRateUrlBean m3U8SingleRateUrlBean = getM3U8SingleRateUrlBean(multiRateM3U8DownloadUrl, dataList);
                if (null == m3U8SingleRateUrlBean || TextUtils.isEmpty(m3U8SingleRateUrlBean.getM3u8FileDownloadUrl())) {
                    postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8MultiRateFileContent_IS_EMPTY);
                    return;
                }
                //开始下载单码率的m3u8文件
                startDownloadSingleRateM3U8(m3U8SingleRateUrlBean.getM3u8FileDownloadUrl());
            }

            @Override
            public void downloadFailure(String errMsg) {
                postDownloadErrorEvent(errMsg);
            }
        });
    }

    //根据单码率m3u8下载地址，下载视频
    public  void startDownloadSingleRateM3U8(String singleRateM3U8DownloadUrl) {
        final JDM3U8SingleRateUrlBean m3U8SingleRateUrlBean = new JDM3U8SingleRateUrlBean(singleRateM3U8DownloadUrl);
        if (null == m3U8SingleRateUrlBean || TextUtils.isEmpty(m3U8SingleRateUrlBean.getM3u8FileDownloadUrl())) {
            postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8SingleRateURL_IS_EMPTY);
            return;
        }
        //1、网络请求获取到m3u8单码率的文件内容
        getM3U8SingleRateFileContent(m3U8SingleRateUrlBean, new JDM3U8DownloaderContract.BaseDownloadListener() {
            @Override
            public void downloadSuccess(String content) {
                if (TextUtils.isEmpty(content)) {
                    postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8SingleRateFileContent_IS_EMPTY);
                    return;
                }

                //保存单码率的m3u8文件
                saveM3U8SingleRateFile(content);

                //2、得到ts列表的下载地址
                List<String> tsUrlPathList = getM3U8TsBeanList(content);
                if (null == tsUrlPathList || tsUrlPathList.isEmpty()) {
                    postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8_TS_LIST_IS_EMPTY);
                    return;
                }

                //3、下载各个ts文件
                downloadM3U8Ts(tsUrlPathList, m3U8SingleRateUrlBean);
            }

            @Override
            public void downloadFailure(String errMsg) {
                postDownloadErrorEvent(errMsg);
            }
        });
    }
}
