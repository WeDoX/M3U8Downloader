package com.onedream.m3u8downloader;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.onedream.jdm3u8downloader.JDM3U8Downloader;
import com.onedream.jdm3u8downloader.bean.JDDownloadMessage;
import com.onedream.jdm3u8downloader.bean.JDDownloadProgress;
import com.onedream.jdm3u8downloader.bean.JDDownloadQueue;
import com.onedream.jdm3u8downloader.common.JDDownloadQueueState;
import com.onedream.jdm3u8downloader.listener.JDM3U8DownloaderContract;
import com.onedream.jdm3u8downloader.utils.JDM3U8FileCacheUtils;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;
import com.onedream.m3u8downloader.okhttp_downloader.OkHttpDownloader;
import com.onedream.m3u8downloader.okhttp_downloader.OkHttpDownloaderFactory;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        initView();
        initData();
        initEvent();
    }

    private void initView() {

    }

    private void initData() {

    }

    private void initEvent() {
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 自行请求存储权限
                startDownloadM3U8();
            }
        });
    }

    private void startDownloadM3U8() {
        JDDownloadQueue downloadQueue = new JDDownloadQueue();
        downloadQueue.setMovie_id(10);
        downloadQueue.setSingleRate(false);
        downloadQueue.setMovie_download_url("http://yi.jingdianzuida.com/20190905/yM4FKbnk/index.m3u8");
        downloadQueue.setMovie_title("摩羯阿婆");
        downloadQueue.setMovie_num_index(1);
        downloadQueue.setMovie_num_title("第一集");
        downloadQueue.setState(JDDownloadQueueState.STATE_DOWNLOAD_QUEUE);//这个比较重要

        String PATH_MOVIE = JDM3U8FileCacheUtils.createRootDownloadPath(MainActivity.this) + File.separator + "download" + File.separator + "movie" + File.separator;
        //
        JDM3U8Downloader jdm3U8Downloader = new JDM3U8Downloader.Builder()
               // .targetDir(PATH_MOVIE)
                .setSaveDir(PATH_MOVIE)
               // .DownloadQueue(downloadQueue)
                .setDownloadQueue(downloadQueue)
               // .AbstractDownloader(new OkHttpDownloader())
                .setDownloaderFactory(OkHttpDownloaderFactory.create())
                //.GetM3U8FileListener()
                .setDownloaderListener(new JDM3U8DownloaderContract.GetM3U8FileListener() {
                    @Override
                    public void downloadErrorEvent(JDDownloadMessage message) {
                        JDM3U8LogHelper.printLog(message.message);
                    }

                    @Override
                    public void postEvent(JDDownloadProgress progress) {
                        JDM3U8LogHelper.printLog("进度" + progress.toString());
                    }

                    @Override
                    public void downloadSuccessEvent(JDDownloadQueue downloadQueue) {
                        JDM3U8LogHelper.printLog("下载成功事件：" + downloadQueue.getMovie_title());
                    }

                    @Override
                    public void removeDownloadQueueEvent(JDDownloadQueue downloadQueue) {
                        JDM3U8LogHelper.printLog("移除下载" + downloadQueue.getMovie_title());
                    }

                    @Override
                    public void pauseDownload(JDDownloadQueue downloadQueue) {
                        JDM3U8LogHelper.printLog("暂停下载" + downloadQueue.getMovie_title());
                    }
                })
                .build();
        jdm3U8Downloader.startDownload();
    }
}
