package com.onedream.jdm3u8downloader;

import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.base.JDM3U8BaseDownloader;
import com.onedream.jdm3u8downloader.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.common.JDDownloadQueueState;
import com.onedream.jdm3u8downloader.common.JDM3U8TsDownloadState;
import com.onedream.jdm3u8downloader.convert.JDM3U8ModelConvert;
import com.onedream.jdm3u8downloader.convert.imp.JDM3U8ModelConvertImp;
import com.onedream.jdm3u8downloader.file_downloader.JDM3U8FileAbstractDownloader;
import com.onedream.jdm3u8downloader.file_downloader.JDM3U8FileAbstractDownloaderFactory;
import com.onedream.jdm3u8downloader.file_downloader.imp.JDM3U8FileOriginalDownloaderFactory;
import com.onedream.jdm3u8downloader.file_local_storage_manager.JDM3U8FileLocalStorageManager;
import com.onedream.jdm3u8downloader.file_local_storage_manager.imp.JDM3U8FileLocalStorageManagerImp;
import com.onedream.jdm3u8downloader.listener.DownloadStateCallback;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;

import java.io.File;
import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8Downloader extends JDM3U8BaseDownloader {
    private final JDDownloadQueue downloadQueue;
    private final JDM3U8DownloaderContract.JDM3U8DownloadBaseListener getM3U8FileListener;
    private final JDM3U8FileAbstractDownloader abstractDownloader;
    private final JDM3U8ModelConvert jdm3U8SingleRateUrlBeanConvert;
    private final JDM3U8FileLocalStorageManager jdm3U8FileLocalStorageManager;

    private JDM3U8Downloader(JDDownloadQueue downloadQueue,
                             @NonNull JDM3U8DownloaderContract.JDM3U8DownloadBaseListener getM3U8FileListener,
                             JDM3U8FileAbstractDownloader abstractDownloader,
                             @NonNull JDM3U8ModelConvert jdm3U8SingleRateUrlBeanConvert,
                             @NonNull JDM3U8FileLocalStorageManager jdm3U8FileLocalStorageManager) {
        this.downloadQueue = downloadQueue;
        this.getM3U8FileListener = getM3U8FileListener;
        this.abstractDownloader = abstractDownloader;
        this.jdm3U8SingleRateUrlBeanConvert = jdm3U8SingleRateUrlBeanConvert;
        this.jdm3U8FileLocalStorageManager = jdm3U8FileLocalStorageManager;
    }

    public static final class Builder {
        private JDDownloadQueue downloadQueue;
        private JDM3U8DownloaderContract.JDM3U8DownloadBaseListener getM3U8FileListener;
        private String targetDir;
        private JDM3U8FileAbstractDownloaderFactory jdm3U8AbstractDownloaderFactory;
        private JDM3U8ModelConvert jdm3U8ModelConvert;
        private JDM3U8FileLocalStorageManager jdm3U8FileLocalStorageManager;

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

        public Builder setSaveDir(String saveDir) {
            this.targetDir = saveDir;
            return this;
        }

        public Builder setDownloaderListener(JDM3U8DownloaderContract.JDM3U8DownloadBaseListener getM3U8FileListener) {
            this.getM3U8FileListener = getM3U8FileListener;
            return this;
        }


        public Builder setFileDownloaderFactory(JDM3U8FileAbstractDownloaderFactory jdm3U8AbstractDownloaderFactory) {
            this.jdm3U8AbstractDownloaderFactory = jdm3U8AbstractDownloaderFactory;
            return this;
        }


        public Builder setModelConvert(JDM3U8ModelConvert modelConvert) {
            this.jdm3U8ModelConvert = modelConvert;
            return this;
        }

        public Builder setFileLocalStorageManager(JDM3U8FileLocalStorageManager fileLocalStorageManager) {
            this.jdm3U8FileLocalStorageManager = fileLocalStorageManager;
            return this;
        }

        public JDM3U8Downloader build() {
            if (null == jdm3U8AbstractDownloaderFactory) {
                jdm3U8AbstractDownloaderFactory = JDM3U8FileOriginalDownloaderFactory.create();
            }
            if (null == jdm3U8ModelConvert) {
                jdm3U8ModelConvert = new JDM3U8ModelConvertImp();
            }
            if (null == jdm3U8FileLocalStorageManager) {
                jdm3U8FileLocalStorageManager = new JDM3U8FileLocalStorageManagerImp(targetDir);
            }
            return new JDM3U8Downloader(
                    downloadQueue,
                    getM3U8FileListener,
                    jdm3U8AbstractDownloaderFactory.createDownloader(),
                    jdm3U8ModelConvert,
                    jdm3U8FileLocalStorageManager);
        }
    }


    @Override
    public void getM3U8MultiRateFileContent(String urlPath, @NonNull final JDM3U8DownloaderContract.GetM3U8SingleRateContentListener baseDownloadListener) {
        //判断本地是否已经有该多码率文件
        List<String> fileContentList = jdm3U8FileLocalStorageManager.getM3u8MultiRateFileContent(downloadQueue);
        if (null != fileContentList && fileContentList.size() > 0) {
            String content = "";
            for (String lineContent : fileContentList) {
                content += lineContent + "\n";
            }
            JDM3U8LogHelper.printLog("该电影的顶级M3U8多码率文件已经存在，不需要再次请求网络，内容为：" + fileContentList.toString());
            //
            baseDownloadListener.downloadSuccess(content, fileContentList, false);
            return;
        }
        abstractDownloader.downloadM3U8MultiRateFileContent(urlPath, baseDownloadListener);
    }


    @Override
    public JDM3U8SingleRateUrlBean getM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> dataList) {
        return jdm3U8SingleRateUrlBeanConvert.convertM3U8SingleRateUrlBean(m3u8MultiRateFileDownloadUrl, dataList);
    }

    @Override
    public void getM3U8SingleRateFileContent(JDM3U8SingleRateUrlBean m3u8FileUrlBean, @NonNull final JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener) {
        //判断本地是否已经有该单码率文件
        String m3u8Content = jdm3U8FileLocalStorageManager.getM3U8SingleRateFileContent(downloadQueue);
        if (!TextUtils.isEmpty(m3u8Content)) {
            JDM3U8LogHelper.printLog("该电影的M3U8单码率文件已经存在，不需要再次请求网络，内容为：" + m3u8Content);
            baseDownloadListener.downloadSuccess(m3u8Content);
            return;
        }
        abstractDownloader.downloadM3U8SingleRateFileContent(m3u8FileUrlBean.getM3u8FileDownloadUrl(), baseDownloadListener);
    }

    @Override
    public List<String> getTsFileDownloadShortUrlList(String M3U8SingleRateFileContent) {
        return jdm3U8SingleRateUrlBeanConvert.convertTsFileDownloadShortUrlList(M3U8SingleRateFileContent);
    }

    @Override
    public JDM3U8TsBean getJDM3U8TsBean(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String tsFileUrl) {
        return jdm3U8SingleRateUrlBeanConvert.convertM3U8TsBean(m3U8SingleRateFileDownloadUrlBean, tsFileUrl);
    }

    @Override
    public void downloadM3U8Ts(List<JDM3U8TsBean> tsUrlPathList, @Nullable DownloadStateCallback downloadStateCallback, JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener jdm3U8DownloadFullSuccessListener) {
        //
        downloadQueue.setState(JDDownloadQueueState.STATE_DOWNLOAD_ING);
        //
        JDM3U8TsFileDownloadTask downloadTask = new JDM3U8TsFileDownloadTask();
        downloadTask.m3U8TsBeanList = tsUrlPathList;
        downloadTask.tsFileDownloadCallback = tsFileDownloadCallback;
        downloadTask.downloadStateCallback = downloadStateCallback;
        downloadTask.jdm3U8DownloadFullSuccessListener = jdm3U8DownloadFullSuccessListener;
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void saveLocalM3U8SingleRate(String oldString) {
        jdm3U8FileLocalStorageManager.saveLocalM3U8SingleRate(downloadQueue, oldString);
    }


    private final JDM3U8TsFileDownloadTask.TsFileDownloadCallback tsFileDownloadCallback = new JDM3U8TsFileDownloadTask.TsFileDownloadCallback() {
        @Override
        public boolean canDownloadTsFile() {
            return downloadQueue.getState() != JDDownloadQueueState.STATE_DOWNLOAD_NO;
        }

        @Override
        public int downLoadTsFile(JDM3U8TsBean m3U8TsBean) {
            File tsSaveFile = jdm3U8FileLocalStorageManager.getTsFile(downloadQueue, m3U8TsBean);
            if (null != tsSaveFile) {
                JDM3U8LogHelper.printLog("该电影" + downloadQueue.getMovie_id() + "的" + m3U8TsBean.getTsFileName() + "文件已经存在");
                return JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_SUCCESS;
            } else {
                tsSaveFile = jdm3U8FileLocalStorageManager.getTsFileAndIsEmptyNeedCreate(downloadQueue, m3U8TsBean);
            }
            if (null == tsSaveFile) {
                JDM3U8LogHelper.printLog("创建ts文件失败");
                return JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_FAILURE;
            }
            return abstractDownloader.downLoadTsFile(m3U8TsBean, tsSaveFile);
        }

        @Override
        public long getTsFileSize(JDM3U8TsBean m3U8TsBean) {
            File file = jdm3U8FileLocalStorageManager.getTsFileAndIsEmptyNeedCreate(downloadQueue, m3U8TsBean);
            if (null != file) {
                return file.length();
            }
            return 0;
        }
    };


    public void startDownload() {
        if (downloadQueue.isSingleRate()) {
            super.startDownloadSingleRateM3U8(downloadQueue, downloadStateCallback, jdm3U8FileLocalStorageManager);
        } else {
            super.startDownloadMultiRateM3U8(downloadQueue, downloadStateCallback, jdm3U8FileLocalStorageManager);
        }
    }


    private final DownloadStateCallback downloadStateCallback = new DownloadStateCallback() {
        @Override
        public void postDownloadProgressEvent(int successCount, int tsFileCount, long itemLength) {
            if (downloadQueue.getState() == JDDownloadQueueState.STATE_DOWNLOAD_ING || downloadQueue.getState() == JDDownloadQueueState.STATE_DOWNLOAD_FINISH) {
                getM3U8FileListener.downloadProgress(downloadQueue, successCount * itemLength, tsFileCount * itemLength);
            }
        }

        @Override
        public void postDownloadErrorEvent(String errMsg) {
            if (getM3U8FileListener instanceof JDM3U8DownloaderContract.JDM3U8DownloadListener) {
                ((JDM3U8DownloaderContract.JDM3U8DownloadListener) getM3U8FileListener).downloadError(downloadQueue, errMsg);
            }
            getM3U8FileListener.downloadState(downloadQueue, JDDownloadQueueState.STATE_DOWNLOAD_ERROR, errMsg);
        }

        @Override
        public void postDownloadSuccessEvent() {
            if (getM3U8FileListener instanceof JDM3U8DownloaderContract.JDM3U8DownloadListener) {
                ((JDM3U8DownloaderContract.JDM3U8DownloadListener) getM3U8FileListener).downloadSuccess(downloadQueue);
            }
            getM3U8FileListener.downloadState(downloadQueue, JDDownloadQueueState.STATE_DOWNLOAD_SUCCESS, JDDownloadQueueState.getSateStr(JDDownloadQueueState.STATE_DOWNLOAD_SUCCESS));
        }

        @Override
        public void postDownloadCloseEvent() {
            getM3U8FileListener.downloadState(downloadQueue, JDDownloadQueueState.STATE_DOWNLOAD_FINISH, JDDownloadQueueState.getSateStr(JDDownloadQueueState.STATE_DOWNLOAD_FINISH));
        }


        @Override
        public void postDownloadPauseEvent() {
            if (getM3U8FileListener instanceof JDM3U8DownloaderContract.JDM3U8DownloadListener) {
                ((JDM3U8DownloaderContract.JDM3U8DownloadListener) getM3U8FileListener).downloadPause(downloadQueue);
            }
            getM3U8FileListener.downloadState(downloadQueue, JDDownloadQueueState.STATE_DOWNLOAD_PAUSE, JDDownloadQueueState.getSateStr(JDDownloadQueueState.STATE_DOWNLOAD_PAUSE));
        }
    };

}
