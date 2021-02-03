package com.onedream.jdm3u8downloader;

import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.onedream.jdm3u8downloader.base.JDM3U8BaseDownloader;
import com.onedream.jdm3u8downloader.bean.JDDownloadMessage;
import com.onedream.jdm3u8downloader.bean.JDDownloadProgress;
import com.onedream.jdm3u8downloader.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.common.JDDownloadQueueState;
import com.onedream.jdm3u8downloader.common.JDM3U8TsDownloadState;
import com.onedream.jdm3u8downloader.downloader.JDM3U8AbstractDownloader;
import com.onedream.jdm3u8downloader.downloader.JDM3U8AbstractDownloaderFactory;
import com.onedream.jdm3u8downloader.downloader.JDM3U8OriginalDownloaderFactory;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.utils.JDM3U8FileCacheUtils;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;

import java.io.File;
import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8Downloader extends JDM3U8BaseDownloader {
    private JDDownloadQueue downloadQueue;
    private JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener;
    private String targetDir;
    private JDM3U8AbstractDownloader abstractDownloader;


    public static final class Builder {
        private JDDownloadQueue downloadQueue;
        private JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener;
        private String targetDir;
        private JDM3U8AbstractDownloaderFactory jdm3U8AbstractDownloaderFactory;

        public Builder() {

        }

        /**
         * @deprecated use method setDownloadQueue to code
         * {@link Builder setDownloadQueue(JDDownloadQueue downloadQueue)}
         */
        @Deprecated
        public Builder DownloadQueue(JDDownloadQueue downloadQueue) {
            this.downloadQueue = downloadQueue;
            return this;
        }

        public Builder setDownloadQueue(JDDownloadQueue downloadQueue) {
            this.downloadQueue = downloadQueue;
            return this;
        }

        /**
         * @deprecated use method setSaveDir to code
         * {@link Builder setSaveDir(String saveDir)}
         */
        @Deprecated
        public Builder targetDir(String targetDir) {
            this.targetDir = targetDir;
            return this;
        }

        public Builder setSaveDir(String saveDir) {
            this.targetDir = saveDir;
            return this;
        }


        /**
         * @deprecated use method setDownloaderListener to code
         * {@link Builder setDownloaderListener(JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener)}
         */
        @Deprecated
        public Builder GetM3U8FileListener(JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener) {
            this.getM3U8FileListener = getM3U8FileListener;
            return this;
        }

        public Builder setDownloaderListener(JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener) {
            this.getM3U8FileListener = getM3U8FileListener;
            return this;
        }


        /**
         * @deprecated use method setDownloaderFactory to code
         * {@link Builder setDownloaderFactory(JDM3U8AbstractDownloaderFactory jdm3U8AbstractDownloaderFactory)}
         */
        @Deprecated
        public Builder AbstractDownloader(final JDM3U8AbstractDownloader abstractDownloader) {
            this.jdm3U8AbstractDownloaderFactory = new JDM3U8AbstractDownloaderFactory() {
                @Override
                public JDM3U8AbstractDownloader createDownloader() {
                    return abstractDownloader;
                }
            };
            return this;
        }

        public Builder setDownloaderFactory(JDM3U8AbstractDownloaderFactory jdm3U8AbstractDownloaderFactory) {
            this.jdm3U8AbstractDownloaderFactory = jdm3U8AbstractDownloaderFactory;
            return this;
        }

        public JDM3U8Downloader build() {
            if (null == jdm3U8AbstractDownloaderFactory) {
                jdm3U8AbstractDownloaderFactory = JDM3U8OriginalDownloaderFactory.create();
            }
            return new JDM3U8Downloader(targetDir, downloadQueue, getM3U8FileListener, jdm3U8AbstractDownloaderFactory.createDownloader());
        }
    }


    private JDM3U8Downloader(String targetDir, JDDownloadQueue downloadQueue, @NonNull JDM3U8DownloaderContract.GetM3U8FileListener getM3U8FileListener, JDM3U8AbstractDownloader abstractDownloader) {
        this.targetDir = targetDir;
        this.downloadQueue = downloadQueue;
        this.getM3U8FileListener = getM3U8FileListener;
        this.abstractDownloader = abstractDownloader;
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
        abstractDownloader.downloadM3U8MultiRateFileContent(urlPath, baseDownloadListener);
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
        abstractDownloader.downloadM3U8SingleRateFileContent(m3u8FileUrlBean.getM3u8FileDownloadUrl(), baseDownloadListener);
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
        return abstractDownloader.downLoadTsFile(m3U8TsBean, tsSaveFile);
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
        if (downloadQueue.isSingleRate()) {
            super.startDownloadSingleRateM3U8(downloadQueue.getMovie_download_url());
        } else {
            super.startDownloadMultiRateM3U8(downloadQueue.getMovie_download_url());
        }

    }
}
