package com.onedream.m3u8downloader.okhttp_file_downloader;

import android.os.ConditionVariable;

import androidx.annotation.NonNull;

import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.bean.state.JDM3U8DownloadHintMessage;
import com.onedream.jdm3u8downloader.bean.state.JDM3U8TsDownloadState;
import com.onedream.jdm3u8downloader.file_downloader.JDM3U8FileAbstractDownloader;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.utils.JDM3U8FileCacheUtils;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * OkHttp文件下载器
 *
 * @author jdallen
 * @since 2021/1/22
 */
public class OkHttpFileDownloader extends JDM3U8FileAbstractDownloader {

    @Override
    public void downloadM3U8MultiRateFileContent(String urlPath, @NonNull final JDM3U8DownloaderContract.GetM3U8SingleRateContentListener baseDownloadListener) {
        OkHttpUtils.get()
                .url(urlPath)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        JDM3U8LogHelper.printLog("该电影的顶级M3U8多码率文件下载失败,原因为:" + e.toString());
                        baseDownloadListener.downloadFailure(JDM3U8DownloadHintMessage.M3U8MultiRateFile_DOWNLOAD_ERROR);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JDM3U8LogHelper.printLog("该电影的顶级M3U8多码率文件下载文件的内容为：" + response);
                        List<String> tempFileContentList = new ArrayList<>();
                        try {
                            tempFileContentList.addAll(Arrays.asList(response.split("\n")));
                        } catch (Exception e) {
                            tempFileContentList.clear();
                            e.printStackTrace();
                        }
                        baseDownloadListener.downloadSuccess(tempFileContentList, true);
                    }
                });
    }

    @Override
    public void downloadM3U8SingleRateFileContent(String urlPath, @NonNull final JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener) {
        OkHttpUtils.get()
                .url(urlPath)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        JDM3U8LogHelper.printLog("该电影的M3U8文件下载失败,原因为:" + e.toString());
                        baseDownloadListener.downloadFailure(JDM3U8DownloadHintMessage.M3U8SingleRateFile_DOWNLOAD_ERROR);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JDM3U8LogHelper.printLog("该电影的M3U8文件下载文件的内容为：" + response);
                        baseDownloadListener.downloadSuccess(response);
                    }
                });
    }

    @Override
    public int downLoadTsFile(JDM3U8TsBean m3U8TsBean, final File tempSaveFile) {
        final int[] result = {JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_DEFAULT};
        final ConditionVariable conditionVariable = new ConditionVariable();
        OkHttpUtils.get()
                .url(m3U8TsBean.getFullUrl())
                .build()
                .execute(new OkHttpDownloaderFileCallBack(tempSaveFile) {
                    @Override
                    public void inProgress(float progress, long total, int id) {

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        JDM3U8LogHelper.printLog("下载ts文件失败，原因为:" + e.toString());
                        JDM3U8FileCacheUtils.clearInfoForFile(tempSaveFile);//清除文件内容
                        result[0] = JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_FAILURE;
                        conditionVariable.open();//打开阻塞
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        JDM3U8LogHelper.printLog("下载ts文件成功");
                        result[0] = JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_SUCCESS;
                        conditionVariable.open();//打开阻塞

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }
                });
        conditionVariable.block();//阻塞
        return result[0];
    }
}
