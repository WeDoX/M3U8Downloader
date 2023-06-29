package com.onedream.jdm3u8downloader.core.bean.state;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDDownloadQueueState {
    //0-未下载 1-排队中 2-正在下载 3-已下载 4-下载出错
    public static final int STATE_DOWNLOAD_NO = 0;
    public static final int STATE_DOWNLOAD_QUEUE = 1;
    public static final int STATE_DOWNLOAD_ING = 2;
    public static final int STATE_DOWNLOAD_FINISH = 3;
    public static final int STATE_DOWNLOAD_ERROR = 4;
    public static final int STATE_DOWNLOAD_SUCCESS = 5;
    public static final int STATE_DOWNLOAD_PAUSE = 6;//下载暂停

    //0-未下载 1-排队中 2-正在下载 3-已下载 4-下载出错
    public static String getSateStr(int state) {
        switch (state) {
            case STATE_DOWNLOAD_NO:
                return "未下载";
            case STATE_DOWNLOAD_QUEUE:
                return "排队中";
            case STATE_DOWNLOAD_ING:
                return "正在下载";
            case STATE_DOWNLOAD_FINISH:
                return "下载结束";
            case STATE_DOWNLOAD_ERROR:
                return "下载出错";
            case STATE_DOWNLOAD_SUCCESS:
                return "下载成功";
            case STATE_DOWNLOAD_PAUSE:
                return "下载暂停";
        }
        return "未下载";
    }
}
