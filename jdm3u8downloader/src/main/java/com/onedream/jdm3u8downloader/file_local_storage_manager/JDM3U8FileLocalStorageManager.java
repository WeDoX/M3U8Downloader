package com.onedream.jdm3u8downloader.file_local_storage_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;

import java.io.File;
import java.util.List;

public interface JDM3U8FileLocalStorageManager {

    @NonNull
    String getTargetDir();

    @Nullable
    List<String> getM3u8MultiRateFileContent(JDDownloadQueue downloadQueue);

    @Nullable
    String getM3U8SingleRateFileContent(JDDownloadQueue downloadQueue);

    @Nullable
    File getTsFile(JDDownloadQueue downloadQueue, JDM3U8TsBean m3U8TsBean);

    @Nullable
    File getTsFileAndIsEmptyNeedCreate(JDDownloadQueue downloadQueue, JDM3U8TsBean m3U8TsBean);

    //保存多码率文件
    void saveM3U8MultiRateFile(JDDownloadQueue downloadQueue, String content);

    //保存单码率文件
    void saveM3U8SingleRateFile(JDDownloadQueue downloadQueue, String content);


    void saveLocalM3U8SingleRate(JDDownloadQueue downloadQueue, String oldString);
}
