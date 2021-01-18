package com.onedream.jdm3u8downloader.bean;

import com.onedream.jdm3u8downloader.common.JDDownloadQueueState;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDDownloadProgress {
    public long movie_id;
    public int movie_num_index;
    public String movie_num_title;
    public long sofar;
    public long total;
    public int state;
    public boolean isHideDownloadProgress;

    private JDDownloadProgress() {
    }

    //更改隐藏下载布局状态
    private static void changeIsHideDownloadProgress(JDDownloadProgress downloadProgress) {
        downloadProgress.isHideDownloadProgress = (downloadProgress.state == JDDownloadQueueState.STATE_DOWNLOAD_ERROR);//下载出错也隐藏下载进度布局
    }

    //以下为对外接口
    //发布进度
    public static JDDownloadProgress sendProgress(JDDownloadQueue downloadQueue, long sofar, long total, int state) {
        JDDownloadProgress downloadProgress = new JDDownloadProgress();
        downloadProgress.movie_id = downloadQueue.getMovie_id();
        downloadProgress.movie_num_index = downloadQueue.getMovie_num_index();
        downloadProgress.sofar = sofar;
        downloadProgress.total = total;
        downloadProgress.state = state;
        downloadProgress.movie_num_title = downloadQueue.getMovie_num_title();
        changeIsHideDownloadProgress(downloadProgress);
        return downloadProgress;
    }

    //发布状态
    public static JDDownloadProgress sendState(JDDownloadQueue downloadQueue, int state) {
        JDDownloadProgress downloadProgress = new JDDownloadProgress();
        downloadProgress.movie_id = downloadQueue.getMovie_id();
        downloadProgress.movie_num_index = downloadQueue.getMovie_num_index();
        downloadProgress.state = state;
        changeIsHideDownloadProgress(downloadProgress);
        return downloadProgress;
    }


    //隐藏下载布局
    public static JDDownloadProgress sendHide(JDDownloadQueue downloadQueue) {
        JDDownloadProgress downloadProgress = new JDDownloadProgress();
        downloadProgress.movie_id = downloadQueue.getMovie_id();
        downloadProgress.movie_num_index = downloadQueue.getMovie_num_index();
        //
        downloadProgress.isHideDownloadProgress = true;
        return downloadProgress;
    }

    @Override
    public String toString() {
        return "JDDownloadProgress{" +
                "movie_id=" + movie_id +
                ", movie_num_index=" + movie_num_index +
                ", sofar=" + sofar +
                ", total=" + total +
                ", state=" + state +
                ", isHideDownloadProgress=" + isHideDownloadProgress +
                '}';
    }
}
