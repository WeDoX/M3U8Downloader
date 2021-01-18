package com.onedream.jdm3u8downloader.bean;

import com.onedream.jdm3u8downloader.common.JDDownloadQueueState;

/**
 * 下载队列实体
 *
 * @author jdallen
 * @since 2020/4/3
 */
public class JDDownloadQueue {
    private boolean isSingleRate = true;//默认为但码率的
    private long movie_id;
    private int movie_num_index;
    private String movie_title;
    private String movie_num_title;
    private long sofar;
    private long total;
    private String movie_download_url;
    private String movie_fengmiantu;
    private int state = JDDownloadQueueState.STATE_DOWNLOAD_NO;
    //
    private boolean isNeedStopIsExits = false;//是否需要停止


    public JDDownloadQueue() {
    }

    public JDDownloadQueue(long movie_id, int movie_num_index, String movie_title, String movie_num_title, long sofar, long total, String movie_download_url, String movie_fengmiantu, int state) {
        this.movie_id = movie_id;
        this.movie_num_index = movie_num_index;
        this.movie_title = movie_title;
        this.movie_num_title = movie_num_title;
        this.sofar = sofar;
        this.total = total;
        this.movie_download_url = movie_download_url;
        this.movie_fengmiantu = movie_fengmiantu;
        this.state = state;
    }

    public boolean isSingleRate() {
        return isSingleRate;
    }

    public void setSingleRate(boolean singleRate) {
        isSingleRate = singleRate;
    }

    public long getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(long movie_id) {
        this.movie_id = movie_id;
    }

    public int getMovie_num_index() {
        return movie_num_index;
    }

    public void setMovie_num_index(int movie_num_index) {
        this.movie_num_index = movie_num_index;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getMovie_num_title() {
        return movie_num_title;
    }

    public void setMovie_num_title(String movie_num_title) {
        this.movie_num_title = movie_num_title;
    }

    public long getSofar() {
        return sofar;
    }

    public void setSofar(long sofar) {
        this.sofar = sofar;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getMovie_download_url() {
        return movie_download_url;
    }

    public void setMovie_download_url(String movie_download_url) {
        this.movie_download_url = movie_download_url;
    }

    public String getMovie_fengmiantu() {
        return movie_fengmiantu;
    }

    public void setMovie_fengmiantu(String movie_fengmiantu) {
        this.movie_fengmiantu = movie_fengmiantu;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isNeedStopIsExits() {
        return isNeedStopIsExits;
    }

    public void setNeedStopIsExits(boolean needStopIsExits) {
        isNeedStopIsExits = needStopIsExits;
    }
}
