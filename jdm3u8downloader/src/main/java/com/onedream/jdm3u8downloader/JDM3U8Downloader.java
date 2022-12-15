package com.onedream.jdm3u8downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.base.JDM3U8BaseDownloader;
import com.onedream.jdm3u8downloader.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.convert.JDM3U8ModelConvert;
import com.onedream.jdm3u8downloader.convert.imp.JDM3U8ModelConvertImp;
import com.onedream.jdm3u8downloader.file_downloader.JDM3U8FileAbstractDownloader;
import com.onedream.jdm3u8downloader.file_downloader.JDM3U8FileAbstractDownloaderFactory;
import com.onedream.jdm3u8downloader.file_downloader.imp.JDM3U8FileOriginalDownloaderFactory;
import com.onedream.jdm3u8downloader.file_local_storage_manager.JDM3U8FileLocalStorageManager;
import com.onedream.jdm3u8downloader.file_local_storage_manager.imp.JDM3U8FileLocalStorageManagerImp;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;

import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8Downloader extends JDM3U8BaseDownloader {
    private final JDM3U8RealDownloader realDownloader;

    private JDM3U8Downloader(JDDownloadQueue downloadQueue,
                             @NonNull JDM3U8DownloaderContract.JDM3U8DownloadBaseListener getM3U8FileListener,
                             JDM3U8FileAbstractDownloader fileDownloader,
                             @NonNull JDM3U8ModelConvert modelConvert,
                             @NonNull JDM3U8FileLocalStorageManager fileLocalStorageManager) {
        realDownloader = new JDM3U8RealDownloader(downloadQueue, getM3U8FileListener, fileDownloader, modelConvert, fileLocalStorageManager);
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
        realDownloader.getM3U8MultiRateFileContent(urlPath, baseDownloadListener);
    }


    @Override
    public JDM3U8SingleRateUrlBean getM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> dataList) {
        return realDownloader.getM3U8SingleRateUrlBean(m3u8MultiRateFileDownloadUrl, dataList);
    }

    @Override
    public void getM3U8SingleRateFileContent(JDM3U8SingleRateUrlBean m3u8FileUrlBean, @NonNull final JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener) {
        realDownloader.getM3U8SingleRateFileContent(m3u8FileUrlBean, baseDownloadListener);
    }

    @Override
    public List<String> getTsFileDownloadShortUrlList(String M3U8SingleRateFileContent) {
        return realDownloader.getTsFileDownloadShortUrlList(M3U8SingleRateFileContent);
    }

    @Override
    public JDM3U8TsBean getJDM3U8TsBean(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String tsFileUrl) {
        return realDownloader.getJDM3U8TsBean(m3U8SingleRateFileDownloadUrlBean, tsFileUrl);
    }

    @Override
    public void downloadM3U8Ts(List<JDM3U8TsBean> tsUrlPathList, @Nullable JDM3U8DownloaderContract.DownloadStateCallback downloadStateCallback, JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener jdm3U8DownloadFullSuccessListener) {
        realDownloader.downloadM3U8Ts(tsUrlPathList, downloadStateCallback, jdm3U8DownloadFullSuccessListener);
    }

    @Override
    public void saveLocalM3U8SingleRate(String oldString) {
        realDownloader.saveLocalM3U8SingleRate(oldString);
    }


    public void startDownload() {
        realDownloader.startDownload();
    }

}
