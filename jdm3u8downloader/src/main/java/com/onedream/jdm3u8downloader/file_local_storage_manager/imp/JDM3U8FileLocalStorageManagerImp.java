package com.onedream.jdm3u8downloader.file_local_storage_manager.imp;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onedream.jdm3u8downloader.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.file_local_storage_manager.JDM3U8FileLocalStorageManager;
import com.onedream.jdm3u8downloader.utils.JDM3U8FileCacheUtils;

import java.io.File;
import java.util.List;

public class JDM3U8FileLocalStorageManagerImp implements JDM3U8FileLocalStorageManager {

    private final String targetDir;

    public JDM3U8FileLocalStorageManagerImp(String targetDir) {
        this.targetDir = targetDir;
    }

    @NonNull
    @Override
    public String getTargetDir() {
        return targetDir;
    }


    @Nullable
    @Override
    public List<String> getM3u8MultiRateFileContent(JDDownloadQueue downloadQueue) {
        return JDM3U8FileCacheUtils.fileContentToStrList(JDM3U8FileCacheUtils.getM3u8TopFile(getTargetDir(), downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index()));
    }

    @Nullable
    @Override
    public String getM3U8SingleRateFileContent(JDDownloadQueue downloadQueue) {
        return JDM3U8FileCacheUtils.getM3U8FileContent(getTargetDir(), downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index());
    }

    @Nullable
    @Override
    public File getTsFile(JDDownloadQueue downloadQueue, JDM3U8TsBean m3U8TsBean) {
        return JDM3U8FileCacheUtils.getTsFile(getTargetDir(), downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), m3U8TsBean.getTsFileName());
    }

    @Nullable
    @Override
    public File getTsFileAndIsEmptyNeedCreate(JDDownloadQueue downloadQueue, JDM3U8TsBean m3U8TsBean) {
        return JDM3U8FileCacheUtils.getTsFileAndIsEmptyNeedCreate(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), m3U8TsBean.getTsFileName());
    }

    @Override
    public void saveM3U8MultiRateFile(JDDownloadQueue downloadQueue, List<String> contentList) {
        String content = "";
        for (String lineContent : contentList) {
            content += lineContent + "\n";
        }
        JDM3U8FileCacheUtils.saveM3U8TopFile(getTargetDir(), downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), content);
    }

    @Override
    public void saveM3U8SingleRateFile(JDDownloadQueue downloadQueue, String content) {
        JDM3U8FileCacheUtils.saveM3U8File(getTargetDir(), downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), content);
    }

    @Override
    public void saveLocalM3U8SingleRate(JDDownloadQueue downloadQueue, String oldString) {
        //保存一份供本地播放的单码率m3u8文件
        if (!TextUtils.isEmpty(oldString)) {
            File m3u8File = JDM3U8FileCacheUtils.getM3u8File(getTargetDir(), downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index());
            File m3u8LocalFile = JDM3U8FileCacheUtils.getM3u8LocalFile(getTargetDir(), downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index());
            JDM3U8FileCacheUtils.alterStringToCreateNewFile(m3u8File, m3u8LocalFile, oldString, "");
        } else {
            JDM3U8FileCacheUtils.saveM3U8LocalFile(getTargetDir(), downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index(), JDM3U8FileCacheUtils.getM3U8FileContent(targetDir, downloadQueue.getMovie_id(), downloadQueue.getMovie_num_index()));
        }
    }
}
