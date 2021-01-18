package com.onedream.jdm3u8downloader.bean;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDDownloadMessage {
    public long movie_id;
    public int movie_num_index;
    public String movie_title;
    public String movie_num_title;
    public String message;

    public JDDownloadMessage(JDDownloadQueue downloadQueue, String message) {
        this.movie_id = downloadQueue.getMovie_id();
        this.movie_num_index = downloadQueue.getMovie_num_index();
        this.movie_title = downloadQueue.getMovie_title();
        this.movie_num_title = downloadQueue.getMovie_num_title();
        this.message = message;
    }
}
