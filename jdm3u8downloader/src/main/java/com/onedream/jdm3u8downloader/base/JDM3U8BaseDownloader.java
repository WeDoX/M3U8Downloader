package com.onedream.jdm3u8downloader.base;


import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.common.JDM3U8DownloadHintMessage;
import com.onedream.jdm3u8downloader.listener.DownloadStateCallback;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.listener.FileSaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用【模板模式】封装下载流程
 *
 * @author jdallen
 * @since 2020/4/3
 * <p>
 * 0、网络请求获取到m3u8多码率的文件内容
 * 1、得到单码率的下载地址(JDM3U8SingleRateUrlBean)
 * 2、网络请求获取到m3u8单码率的文件内容
 * 3、得到ts列表的下载地址(分为两个步骤：（一）获取到ts文件下载短链地址列表 (二）构建单个ts文件对象JDM3U8TsBean
 * 4、下载各个ts文件
 * 5、保存一份供本地播放的单码率m3u8文件
 */
public abstract class JDM3U8BaseDownloader {
    //0、网络请求获取到m3u8多码率的文件内容
    public abstract void getM3U8MultiRateFileContent(String urlPath, @NonNull JDM3U8DownloaderContract.GetM3U8SingleRateContentListener getM3U8SingleRateContentListener);

    //1、得到单码率的下载地址(JDM3U8SingleRateUrlBean)
    public abstract JDM3U8SingleRateUrlBean getM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> dataList);

    //2、网络请求获取到m3u8单码率的文件内容
    public abstract void getM3U8SingleRateFileContent(JDM3U8SingleRateUrlBean m3u8FileUrlBean, @NonNull JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener);

    //3、得到ts列表的下载地址
    public List<JDM3U8TsBean> getTsFileDownloadShortUrlList(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String M3U8SingleRateFileContent) {
        //3-1、获取到ts文件下载短链地址列表
        List<String> tsUrlPathList = getTsFileDownloadShortUrlList(M3U8SingleRateFileContent);
        if (null == tsUrlPathList || tsUrlPathList.isEmpty()) {
            return null;
        }
        List<JDM3U8TsBean> JDM3U8TsBeanList = new ArrayList<>();
        for (String tsShortUrl : tsUrlPathList) {
            //3-2、构建单个ts文件对象JDM3U8TsBean
            JDM3U8TsBean jdm3U8TsBean = getJDM3U8TsBean(m3U8SingleRateFileDownloadUrlBean, tsShortUrl);
            if (null != jdm3U8TsBean) {
                JDM3U8TsBeanList.add(jdm3U8TsBean);
            }
        }
        return JDM3U8TsBeanList;
    }

    //3-1、得到ts列表的字符串列表
    public abstract List<String> getTsFileDownloadShortUrlList(String M3U8SingleRateFileContent);

    //3-2、构建单个ts文件对象JDM3U8TsBean
    public abstract JDM3U8TsBean getJDM3U8TsBean(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String tsFileUrl);

    //4、下载各个ts文件
    public abstract void downloadM3U8Ts(List<JDM3U8TsBean> tsUrlPathList, @Nullable DownloadStateCallback downloadStateCallback, JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener jdm3U8DownloadFullSuccessListener);

    //5、保存一份供本地播放的单码率m3u8文件
    public abstract void saveLocalM3U8SingleRate(String oldString);


    //根据多码率m3u8下载地址，下载视频
    public void startDownloadMultiRateM3U8(final String multiRateM3U8DownloadUrl, @Nullable final DownloadStateCallback downloadStateCallback, @Nullable final FileSaveCallback fileSaveCallback) {
        //0、网络请求获取到m3u8多码率的文件内容
        getM3U8MultiRateFileContent(multiRateM3U8DownloadUrl, new JDM3U8DownloaderContract.GetM3U8SingleRateContentListener() {
            @Override
            public void downloadSuccess(String content, List<String> dataList, boolean isNeedSaveFile) {
                if (null == dataList || dataList.isEmpty()) {
                    if(null != downloadStateCallback){
                        downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8MultiRateFileContent_IS_EMPTY);
                    }
                    return;
                }

                if (isNeedSaveFile && !TextUtils.isEmpty(content)) {
                    //保存多码率的m3u8文件
                    if(null != fileSaveCallback) {
                        fileSaveCallback.saveM3U8MultiRateFile(content);
                    }
                }
                //1、得到单码率的下载地址(JDM3U8SingleRateUrlBean)
                JDM3U8SingleRateUrlBean m3U8SingleRateUrlBean = getM3U8SingleRateUrlBean(multiRateM3U8DownloadUrl, dataList);
                if (null == m3U8SingleRateUrlBean || TextUtils.isEmpty(m3U8SingleRateUrlBean.getM3u8FileDownloadUrl())) {
                    if(null != downloadStateCallback) {
                        downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8MultiRateFileContent_IS_EMPTY);
                    }
                    return;
                }
                //开始下载单码率的m3u8文件
                startDownloadSingleRateM3U8(m3U8SingleRateUrlBean.getM3u8FileDownloadUrl(), downloadStateCallback, fileSaveCallback);
            }

            @Override
            public void downloadFailure(String errMsg) {
                if(null != downloadStateCallback) {
                    downloadStateCallback.postDownloadErrorEvent(errMsg);
                }
            }
        });
    }


    //根据单码率m3u8下载地址，下载视频
    public void startDownloadSingleRateM3U8(String singleRateM3U8DownloadUrl, @Nullable final DownloadStateCallback downloadStateCallback, @Nullable final FileSaveCallback fileSaveCallback) {
        final JDM3U8SingleRateUrlBean m3U8SingleRateUrlBean = new JDM3U8SingleRateUrlBean(singleRateM3U8DownloadUrl);
        if (null == m3U8SingleRateUrlBean || TextUtils.isEmpty(m3U8SingleRateUrlBean.getM3u8FileDownloadUrl())) {
            if(null != downloadStateCallback){
                downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8SingleRateURL_IS_EMPTY);
            }
            return;
        }
        //1、网络请求获取到m3u8单码率的文件内容
        getM3U8SingleRateFileContent(m3U8SingleRateUrlBean, new JDM3U8DownloaderContract.BaseDownloadListener() {
            @Override
            public void downloadSuccess(String content) {
                if (TextUtils.isEmpty(content)) {
                    if(null != downloadStateCallback) {
                        downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8SingleRateFileContent_IS_EMPTY);
                    }
                    return;
                }

                //保存单码率的m3u8文件
                if(null != fileSaveCallback) {
                    fileSaveCallback.saveM3U8SingleRateFile(content);
                }

                //得到ts列表的下载地址
                List<JDM3U8TsBean> JDM3U8TsBeanList = getTsFileDownloadShortUrlList(m3U8SingleRateUrlBean, content);
                if (null == JDM3U8TsBeanList || JDM3U8TsBeanList.isEmpty()) {
                    if(null != downloadStateCallback) {
                        downloadStateCallback.postDownloadErrorEvent(JDM3U8DownloadHintMessage.M3U8_TS_LIST_IS_EMPTY);
                    }
                    return;
                }
                //下载各个ts文件
                downloadM3U8Ts(JDM3U8TsBeanList, downloadStateCallback, new JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener() {
                    @Override
                    public void downloadFullSuccessSaveLocalM3U8SingleRate(String oldString) {
                        saveLocalM3U8SingleRate(oldString);
                    }
                });
            }

            @Override
            public void downloadFailure(String errMsg) {
                if(null != downloadStateCallback) {
                    downloadStateCallback.postDownloadErrorEvent(errMsg);
                }
            }
        });
    }
}
