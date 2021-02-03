package com.onedream.jdm3u8downloader.bean;

import com.onedream.jdm3u8downloader.common.JDDownloadQueueState;

/**
 * 下载队列实体
 *
 * @author jdallen
 * @since 2020/4/3
 */
public class JDDownloadQueue {
    private long movie_id;//电影或电视剧id
    private int movie_num_index;//集数id(电影的集数id为0、电视剧的集数id为1、2、3.....）
    private String movie_title;//电影或电视剧名称
    private String movie_num_title;//集数名称
    private String movie_download_url;//下载地址
    private boolean isSingleRate = true;//下载地址是否是单码率地址，默认为单码率地址
    private String movie_cover;//封面图片
    private int state = JDDownloadQueueState.STATE_DOWNLOAD_NO;


    public JDDownloadQueue() {
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

    public String getMovie_download_url() {
        return movie_download_url;
    }

    public void setMovie_download_url(String movie_download_url) {
        this.movie_download_url = movie_download_url;
    }

    public String getMovie_cover() {
        return movie_cover;
    }

    public void setMovie_cover(String movie_cover) {
        this.movie_cover = movie_cover;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "JDDownloadQueue{" +
                "movie_id=" + movie_id +
                ", movie_num_index=" + movie_num_index +
                ", movie_title='" + movie_title + '\'' +
                ", movie_num_title='" + movie_num_title + '\'' +
                ", movie_download_url='" + movie_download_url + '\'' +
                ", isSingleRate=" + isSingleRate +
                ", movie_cover='" + movie_cover + '\'' +
                ", state=" + state +
                '}';
    }
}
