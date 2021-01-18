package com.onedream.jdm3u8downloader.common;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 下载ts文件的下载状态
 *
 * @author jdallen
 * @since 2020/4/3
 */
@IntDef({
        JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_DEFAULT,
        JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_FAILURE,
        JDM3U8TsDownloadState.DOWNLOAD_TS_FILE_SUCCESS,
})
@Retention(RetentionPolicy.SOURCE)
public @interface JDM3U8TsDownloadState {
    int DOWNLOAD_TS_FILE_DEFAULT = 0;
    int DOWNLOAD_TS_FILE_FAILURE = 1;
    int DOWNLOAD_TS_FILE_SUCCESS = 2;
}