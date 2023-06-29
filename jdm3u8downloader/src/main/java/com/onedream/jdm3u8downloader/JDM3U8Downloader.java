package com.onedream.jdm3u8downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.ability.model_converter.JDM3U8BaseModelConverter;
import com.onedream.jdm3u8downloader.core.JDM3U8BaseDownloader;
import com.onedream.jdm3u8downloader.core.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.core.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.core.bean.JDM3U8TsDownloadUrlBean;
import com.onedream.jdm3u8downloader.ability.model_converter.imp.JDM3U8ModelConverterImp;
import com.onedream.jdm3u8downloader.ability.file_downloader.JDM3U8FileAbstractDownloader;
import com.onedream.jdm3u8downloader.ability.file_downloader.JDM3U8FileAbstractDownloaderFactory;
import com.onedream.jdm3u8downloader.ability.file_downloader.imp.JDM3U8FileOriginalDownloaderFactory;
import com.onedream.jdm3u8downloader.ability.file_local_storage_manager.JDM3U8FileLocalStorageManager;
import com.onedream.jdm3u8downloader.ability.file_local_storage_manager.imp.JDM3U8FileLocalStorageManagerImp;
import com.onedream.jdm3u8downloader.core.listener.JDM3U8DownloaderContract;

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
                             @NonNull JDM3U8BaseModelConverter modelConvert,
                             @NonNull JDM3U8FileLocalStorageManager fileLocalStorageManager) {
        realDownloader = new JDM3U8RealDownloader(downloadQueue, getM3U8FileListener, fileDownloader, modelConvert, fileLocalStorageManager);
    }

    public static final class Builder {
        private JDDownloadQueue downloadQueue;
        private JDM3U8DownloaderContract.JDM3U8DownloadBaseListener getM3U8FileListener;
        private String targetDir;
        private JDM3U8FileAbstractDownloaderFactory jdm3U8AbstractDownloaderFactory;
        private JDM3U8BaseModelConverter jdm3U8ModelConvert;
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


        public Builder setModelConvert(JDM3U8BaseModelConverter modelConvert) {
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
                jdm3U8ModelConvert = new JDM3U8ModelConverterImp();
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
    public void downloadM3U8MultiRateFileContent(String urlPath, @NonNull final JDM3U8DownloaderContract.GetM3U8SingleRateContentListener baseDownloadListener) {
        realDownloader.downloadM3U8MultiRateFileContent(urlPath, baseDownloadListener);
    }


    @Override
    public JDM3U8SingleRateUrlBean covertToM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> dataList) {
        return realDownloader.covertToM3U8SingleRateUrlBean(m3u8MultiRateFileDownloadUrl, dataList);
    }

    @Override
    public void downloadM3U8SingleRateFileContent(JDM3U8SingleRateUrlBean m3u8FileUrlBean, @NonNull final JDM3U8DownloaderContract.BaseDownloadListener baseDownloadListener) {
        realDownloader.downloadM3U8SingleRateFileContent(m3u8FileUrlBean, baseDownloadListener);
    }

    @Override
    public List<JDM3U8TsDownloadUrlBean> convertToM3U8TsDownloadUrlBeanList(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String M3U8SingleRateFileContent) {
        return realDownloader.convertToM3U8TsDownloadUrlBeanList(m3U8SingleRateFileDownloadUrlBean, M3U8SingleRateFileContent);
    }

    @Override
    public void downloadM3U8AllTsFile(List<JDM3U8TsDownloadUrlBean> tsUrlPathList, @Nullable JDM3U8DownloaderContract.DownloadStateCallback downloadStateCallback, JDM3U8DownloaderContract.JDM3U8DownloadFullSuccessListener jdm3U8DownloadFullSuccessListener) {
        realDownloader.downloadM3U8AllTsFile(tsUrlPathList, downloadStateCallback, jdm3U8DownloadFullSuccessListener);
    }

    @Override
    public void saveLocalM3U8SingleRateFile(String oldString) {
        realDownloader.saveLocalM3U8SingleRateFile(oldString);
    }


    public void startDownload() {
        realDownloader.startDownload();
    }

}
