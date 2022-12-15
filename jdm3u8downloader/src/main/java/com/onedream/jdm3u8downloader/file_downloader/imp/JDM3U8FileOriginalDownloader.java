package com.onedream.jdm3u8downloader.file_downloader.imp;

import android.os.ConditionVariable;

import androidx.annotation.NonNull;

import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.bean.state.JDM3U8DownloadHintMessage;
import com.onedream.jdm3u8downloader.bean.state.JDM3U8TsDownloadState;
import com.onedream.jdm3u8downloader.file_downloader.JDM3U8FileAbstractDownloader;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.utils.JDM3U8CloseUtils;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jdallen
 * @since 2021/1/22
 */
public class JDM3U8FileOriginalDownloader extends JDM3U8FileAbstractDownloader {

    @Override
    public void downloadM3U8MultiRateFileContent(final String urlPath, @NonNull final JDM3U8DownloaderContract.GetM3U8SingleRateContentListener baseDownloadListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> fileContentList = new ArrayList<>();
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(urlPath);
                    bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        fileContentList.add(line);
                    }
                    JDM3U8LogHelper.printLog("该电影的顶级M3U8多码率文件下载文件的内容为：" + fileContentList.toString());
                    baseDownloadListener.downloadSuccess(fileContentList, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JDM3U8LogHelper.printLog("该电影的顶级M3U8多码率文件下载失败,原因为:" + e.toString());
                    baseDownloadListener.downloadFailure(JDM3U8DownloadHintMessage.M3U8MultiRateFile_DOWNLOAD_ERROR + e.toString());
                } finally {
                    JDM3U8CloseUtils.close(bufferedReader);
                }
            }
        }).start();
    }

    @Override
    public void downloadM3U8SingleRateFileContent(final String urlPath, @NonNull final JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(urlPath);
                    //下载资源
                    bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                    String content = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content += line + "\n";
                    }
                    JDM3U8LogHelper.printLog("该电影的M3U8文件下载文件的内容为：" + content);
                    baseDownloadListener.downloadSuccess(content);
                } catch (Exception e) {
                    e.printStackTrace();
                    JDM3U8LogHelper.printLog("该电影的M3U8文件下载失败,原因为:" + e.toString());
                    baseDownloadListener.downloadFailure(JDM3U8DownloadHintMessage.M3U8SingleRateFile_DOWNLOAD_ERROR + e.toString());
                } finally {
                    JDM3U8CloseUtils.close(bufferedReader);
                }
            }
        }).start();
    }

    @Override
    public int downLoadTsFile(final JDM3U8TsBean m3U8TsBean, final File tempSaveFile) {
        final int[] result = {JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_DEFAULT};
        final ConditionVariable conditionVariable = new ConditionVariable();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream dataInputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    URL url = new URL(m3U8TsBean.getFullUrl());
                    //下载资源
                    dataInputStream = new DataInputStream(url.openStream());
                    fileOutputStream = new FileOutputStream(tempSaveFile);
                    byte[] bytes = new byte[8 * 1024 * 1024];
                    int length = 0;
                    while ((length = dataInputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, length);
                    }
                    JDM3U8LogHelper.printLog("下载ts文件成功");
                    result[0] = JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_SUCCESS;
                    conditionVariable.open();//打开阻塞
                } catch (Exception e) {
                    e.printStackTrace();
                    JDM3U8LogHelper.printLog("下载ts文件失败，原因为:" + e.toString());
                    result[0] = JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_FAILURE;
                    conditionVariable.open();//打开阻塞
                } finally {
                    JDM3U8CloseUtils.close(dataInputStream);
                    JDM3U8CloseUtils.close(fileOutputStream);
                }
            }
        }).start();
        conditionVariable.block();//阻塞
        return result[0];
    }
}
