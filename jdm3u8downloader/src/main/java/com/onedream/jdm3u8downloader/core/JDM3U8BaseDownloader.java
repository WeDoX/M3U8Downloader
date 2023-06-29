package com.onedream.jdm3u8downloader.core;


import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.ability.file_local_storage_manager.JDM3U8FileLocalStorageManager;
import com.onedream.jdm3u8downloader.core.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.core.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.core.bean.JDM3U8TsDownloadUrlBean;
import com.onedream.jdm3u8downloader.core.bean.state.JDM3U8DownloadHintMessage;
import com.onedream.jdm3u8downloader.core.listener.JDM3U8DownloaderContract;

import java.util.List;

/**
 * 使用【模板模式】封装下载流程
 *
 * @author jdallen
 * @since 2020/4/3
 * <p>
 * 0、网络请求获取到m3u8多码率的文件内容
 * 1、处理转换【m3u8多码率的文件内容】得到单码率的下载地址(JDM3U8SingleRateUrlBean)
 * 2、网络请求获取到m3u8单码率的文件内容
 * 3、处理转换【m3u8单码率的文件内容】得到ts列表的下载地址
 * 4、下载各个ts文件
 * 5、保存一份供本地播放的单码率m3u8文件
 */
public abstract class JDM3U8BaseDownloader {
    //0、网络请求获取到m3u8多码率的文件内容
    public abstract void downloadM3U8MultiRateFileContent(String m3u8MultiRateFileDownloadUrl, @NonNull JDM3U8DownloaderContract.GetM3U8SingleRateContentListener getM3U8SingleRateContentListener);

    //1、处理转换【m3u8多码率的文件内容】得到单码率的下载地址(JDM3U8SingleRateUrlBean)
    public abstract JDM3U8SingleRateUrlBean covertToM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> m3U8MultiRateFileContentLineStrList);

    //2、网络请求获取到m3u8单码率的文件内容
    public abstract void downloadM3U8SingleRateFileContent(JDM3U8SingleRateUrlBean mJDM3U8SingleRateUrlBean, @NonNull JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener);

    //3、处理转换【m3u8单码率的文件内容】得到ts列表的下载地址
    public abstract List<JDM3U8TsDownloadUrlBean> convertToM3U8TsDownloadUrlBeanList(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String m3U8SingleRateFileContent);

    //4、下载各个ts文件
    public abstract void downloadM3U8AllTsFile(List<JDM3U8TsDownloadUrlBean> mJDM3U8TsBeanList, @Nullable JDM3U8DownloaderContract.DownloadStateCallback downloadStateCallback, JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener jdm3U8DownloadFullSuccessListener);

    //5、保存一份供本地播放的单码率m3u8文件
    public abstract void saveLocalM3U8SingleRateFile(String oldString);


    //根据多码率m3u8下载地址，下载视频
    public void startDownloadMultiRateM3U8(final JDDownloadQueue downloadQueue, @Nullable final JDM3U8DownloaderContract.DownloadStateCallback downloadStateCallback, @Nullable final JDM3U8FileLocalStorageManager fileLocalStorageManager) {
        //0、网络请求获取到m3u8多码率的文件内容
        downloadM3U8MultiRateFileContent(downloadQueue.getMovie_download_url(), new JDM3U8DownloaderContract.GetM3U8SingleRateContentListener() {
            @Override
            public void downloadSuccess(List<String> dataList, boolean isNeedSaveFile) {
                if (null == dataList || dataList.isEmpty()) {
                    if (null != downloadStateCallback) {
                        downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8MultiRateFileContent_IS_EMPTY);
                    }
                    return;
                }

                if (isNeedSaveFile) {
                    //保存多码率的m3u8文件
                    if (null != fileLocalStorageManager) {
                        fileLocalStorageManager.saveM3U8MultiRateFile(downloadQueue, dataList);
                    }
                }
                //1、得到单码率的下载地址(JDM3U8SingleRateUrlBean)
                JDM3U8SingleRateUrlBean m3U8SingleRateUrlBean = covertToM3U8SingleRateUrlBean(downloadQueue.getMovie_download_url(), dataList);
                if (null == m3U8SingleRateUrlBean || TextUtils.isEmpty(m3U8SingleRateUrlBean.getM3u8FileDownloadUrl())) {
                    if (null != downloadStateCallback) {
                        downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8MultiRateFileContent_IS_EMPTY);
                    }
                    return;
                }
                //开始下载单码率的m3u8文件
                downloadQueue.setMovie_download_url(m3U8SingleRateUrlBean.getM3u8FileDownloadUrl());
                startDownloadSingleRateM3U8(downloadQueue, downloadStateCallback, fileLocalStorageManager);
            }

            @Override
            public void downloadFailure(String errMsg) {
                if (null != downloadStateCallback) {
                    downloadStateCallback.postDownloadErrorEvent(errMsg);
                }
            }
        });
    }


    //根据单码率m3u8下载地址，下载视频
    public void startDownloadSingleRateM3U8(final JDDownloadQueue downloadQueue, @Nullable final JDM3U8DownloaderContract.DownloadStateCallback downloadStateCallback, @Nullable final JDM3U8FileLocalStorageManager fileLocalStorageManager) {
        final JDM3U8SingleRateUrlBean m3U8SingleRateUrlBean = new JDM3U8SingleRateUrlBean(downloadQueue.getMovie_download_url());
        if (TextUtils.isEmpty(m3U8SingleRateUrlBean.getM3u8FileDownloadUrl())) {
            if (null != downloadStateCallback) {
                downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8SingleRateURL_IS_EMPTY);
            }
            return;
        }
        //1、网络请求获取到m3u8单码率的文件内容
        downloadM3U8SingleRateFileContent(m3U8SingleRateUrlBean, new JDM3U8DownloaderContract.BaseDownloadListener() {
            @Override
            public void downloadSuccess(String content) {
                if (TextUtils.isEmpty(content)) {
                    if (null != downloadStateCallback) {
                        downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8SingleRateFileContent_IS_EMPTY);
                    }
                    return;
                }

                //保存单码率的m3u8文件
                if (null != fileLocalStorageManager) {
                    fileLocalStorageManager.saveM3U8SingleRateFile(downloadQueue, content);
                }

                //得到ts列表的下载地址
                List<JDM3U8TsDownloadUrlBean> JDM3U8TsBeanList = convertToM3U8TsDownloadUrlBeanList(m3U8SingleRateUrlBean, content);
                if (null == JDM3U8TsBeanList || JDM3U8TsBeanList.isEmpty()) {
                    if (null != downloadStateCallback) {
                        downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8_TS_LIST_IS_EMPTY);
                    }
                    return;
                }
                //下载各个ts文件
                downloadM3U8AllTsFile(JDM3U8TsBeanList, downloadStateCallback, new JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener() {
                    @Override
                    public void downloadFullSuccessSaveLocalM3U8SingleRate(String oldString) {
                        saveLocalM3U8SingleRateFile(oldString);
                    }
                });
            }

            @Override
            public void downloadFailure(String errMsg) {
                if (null != downloadStateCallback) {
                    downloadStateCallback.postDownloadErrorEvent(errMsg);
                }
            }
        });
    }
}
