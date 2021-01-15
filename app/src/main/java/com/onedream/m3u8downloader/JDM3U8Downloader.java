package com.onedream.m3u8downloader;

import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.onedream.m3u8downloader.base.JDM3U8BaseDownloader;
import com.onedream.m3u8downloader.bean.JDDownloadMessage;
import com.onedream.m3u8downloader.bean.JDDownloadProgress;
import com.onedream.m3u8downloader.bean.JDDownloadQueue;
import com.onedream.m3u8downloader.bean.JDM3U8SingleRateUrlBean;
import com.onedream.m3u8downloader.bean.JDM3U8TsBean;
import com.onedream.m3u8downloader.common.JDDownloadQueueState;
import com.onedream.m3u8downloader.common.JDM3U8DownloadHintMessage;
import com.onedream.m3u8downloader.common.JDM3U8TsDownloadState;
import com.onedream.m3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.m3u8downloader.listener.JDM3U8DownloaderFileCallBack;
import com.onedream.m3u8downloader.utils.JDM3U8FileCacheUtils;
import com.onedream.m3u8downloader.utils.JDM3U8LogHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8Downloader extends JDM3U8BaseDownloader {
    private JDDownloadQueue downloadQueue;
    private JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener;
    private String targetDir;


    public static final class Builder {
        private JDDownloadQueue downloadQueue;
        private JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener;
        private String targetDir;

        public Builder() {

        }

        public Builder DownloadQueue(JDDownloadQueue downloadQueue) {
            this.downloadQueue = downloadQueue;
            return this;
        }

        public Builder targetDir(String targetDir) {
            this.targetDir = targetDir;
            return this;
        }

        public Builder GetM3U8FileListener(JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener) {
            this.getM3U8FileListener = getM3U8FileListener;
            return this;
        }

        public JDM3U8Downloader build() {
            return new JDM3U8Downloader(targetDir, downloadQueue, getM3U8FileListener);
        }
    }


    JDM3U8Downloader(String targetDir, JDDownloadQueue downloadQueue, @NonNull JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener) {
        this.targetDir = targetDir;
        this.downloadQueue = downloadQueue;
        this.getM3U8FileListener = getM3U8FileListener;
    }


    @Override
    public void getM3U8MultiRateFileContent(String urlPath, @NonNull final JDM3U8DownloaderContract.GetM3U8SingleRateContentListener baseDownloadListener) {
        //判断本地是否已经有该多码率文件
        List<String> fileContentList = JDM3U8FileCacheUtils.fileContentToStrList(JDM3U8FileCacheUtils.getM3u8TopFile(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index()));
        if (null != fileContentList && fileContentList.size() > 0) {
            String content = "";
            for (String lineContent : fileContentList) {
                content += lineContent + "\n";
            }
            JDM3U8LogHelper.printLog("该电影的顶级M3U8多码率文件已经存在，不需要再次请求网络，内容为：" + content);
            //
            baseDownloadListener.downloadSuccess(content, fileContentList, false);
            return;
        }
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
                        baseDownloadListener.downloadSuccess(response, tempFileContentList, true);
                    }
                });
    }

    @Override
    public void getM3U8SingleRateFileContent(JDM3U8SingleRateUrlBean m3u8FileUrlBean, @NonNull final JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener) {
        //判断本地是否已经有该单码率文件
        String m3u8Content = JDM3U8FileCacheUtils.getM3U8FileContent(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index());
        if (!TextUtils.isEmpty(m3u8Content)) {
            JDM3U8LogHelper.printLog("该电影的M3U8单码率文件已经存在，不需要再次请求网络，内容为：" + m3u8Content);
            baseDownloadListener.downloadSuccess(m3u8Content);
            return;
        }

        OkHttpUtils.get()
                .url(m3u8FileUrlBean.getM3u8FileDownloadUrl())
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
    public void downloadM3U8Ts(List<String> tsUrlPathList, JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean) {
        //
        downloadQueue.setState(JDDownloadQueueState.STATE_DOWNLOAD_ING);
        //
        JDM3U8TsFileDownloadTask downloadTask = new JDM3U8TsFileDownloadTask();
        downloadTask.tsUrlPathList = tsUrlPathList;
        downloadTask.m3U8SingleRateFileDownloadUrlBean = m3U8SingleRateFileDownloadUrlBean;
        downloadTask.m3U8BaseDownloader = this;
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void saveLocalM3U8SingleRate(String oldString) {
        //保存一份供本地播放的单码率m3u8文件
        if (!TextUtils.isEmpty(oldString)) {
            /**
             * 创建一份供本地播放的m3u8
             */
            File m3u8File = JDM3U8FileCacheUtils.getM3u8File(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index());
            File m3u8LocalFile = JDM3U8FileCacheUtils.getM3u8LocalFile(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index());
            alterStringToCreateNewFile(m3u8File, m3u8LocalFile, oldString, "");
        } else {
            JDM3U8FileCacheUtils.saveM3U8LocalFile(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), JDM3U8FileCacheUtils.getM3U8FileContent(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index()));
        }
    }


    @Override
    public void saveM3U8MultiRateFile(String content) {
        JDM3U8FileCacheUtils.saveM3U8TopFile(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), content);
    }

    @Override
    public void saveM3U8SingleRateFile(String content) {
        JDM3U8FileCacheUtils.saveM3U8File(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), content);
    }


    @Override
    public boolean canDownloadTsFile() {
        return downloadQueue.getState() != JDDownloadQueueState.STATE_DOWNLOAD_NO;
    }

    @Override
    public int downLoadTsFile(JDM3U8TsBean m3U8TsBean) {
        File tsSaveFile = JDM3U8FileCacheUtils.getTsFile(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), m3U8TsBean.getTsFileName());
        if (null != tsSaveFile) {
            JDM3U8LogHelper.printLog("该电影" + downloadQueue.getMovie_id() + "的" + m3U8TsBean.getTsFileName() + "文件已经存在");
            return JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_SUCCESS;
        } else {
            tsSaveFile = JDM3U8FileCacheUtils.getTsFileAndIsEmptyNeedCreate(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), m3U8TsBean.getTsFileName());
        }
        if (null == tsSaveFile) {
            JDM3U8LogHelper.printLog("创建ts文件失败");
            return JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_FAILURE;
        }
        final int[] result = {JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_DEFAULT};
        final ConditionVariable conditionVariable = new ConditionVariable();
        final File tempSaveFile = tsSaveFile;

        OkHttpUtils.get()
                .url(m3U8TsBean.getFullUrl())
                .build()
                .execute(new JDM3U8DownloaderFileCallBack(tempSaveFile) {
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

    @Override
    public void postDownloadProgress(int successCount, int tsFileCount, long itemLength) {
        if (downloadQueue.getState() == JDDownloadQueueState.STATE_DOWNLOAD_ING || downloadQueue.getState() == JDDownloadQueueState.STATE_DOWNLOAD_FINISH) {
            getM3U8FileListener.postEvent(JDDownloadProgress.sendProgress(downloadQueue, successCount * itemLength, tsFileCount * itemLength, JDDownloadQueueState.STATE_DOWNLOAD_ING));
        }
    }

    @Override
    public void postDownloadErrorEvent(String errMsg) {
        getM3U8FileListener.downloadErrorEvent(new JDDownloadMessage(downloadQueue, errMsg));
        getM3U8FileListener.postEvent(JDDownloadProgress.sendState(downloadQueue, JDDownloadQueueState.STATE_DOWNLOAD_ERROR));
        getM3U8FileListener.removeDownloadQueueEvent(downloadQueue);
    }

    @Override
    public void downloadSuccessEvent() {
        getM3U8FileListener.downloadSuccessEvent(downloadQueue);
    }

    @Override
    public void downloadFinish() {
        getM3U8FileListener.removeDownloadQueueEvent(downloadQueue);
        getM3U8FileListener.postEvent(JDDownloadProgress.sendHide(downloadQueue));
    }


    @Override
    public long getTsFileSize(JDM3U8TsBean m3U8TsBean) {
        File file = JDM3U8FileCacheUtils.getTsFileAndIsEmptyNeedCreate(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), m3U8TsBean.getTsFileName());
        if (null != file) {
            return file.length();
        }
        return 0;
    }

    @Override
    public void pauseDownload() {
        getM3U8FileListener.pauseDownload(downloadQueue);
    }

    public void startDownload() {
        super.startDownloadMultiRateM3U8(downloadQueue.getMovie_download_url());
    }
}
