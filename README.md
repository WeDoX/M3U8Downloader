# M3U8Downloader [![](https://jitpack.io/v/WeDox/M3U8Downloader.svg)](https://jitpack.io/#WeDox/M3U8Downloader)
m3u8视频文件下载器（生成支持供本地播放的m3u8文件）
#### How to use?
Step 0.Add it in your root build.gradle at the end of repositories:
~~~~~~~~~
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
~~~~~~~~~
Step 1. Add the dependency
~~~~~~~~~
dependencies {
	        implementation 'com.github.WeDox:M3U8Downloader:1.1.0'
	}
~~~~~~~~~
Step 2.to use
~~~~~~~~~
//需要权限：

   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INTERNET" />
    
private void startDownloadM3U8() {
        JDDownloadQueue downloadQueue = new JDDownloadQueue();
        downloadQueue.setMovie_id(10);
        downloadQueue.setSingleRate(false);//多码率
        downloadQueue.setMovie_download_url("http://yi.jingdianzuida.com/20190905/yM4FKbnk/index.m3u8");//多码率地址
        downloadQueue.setMovie_title("摩羯阿婆");
        downloadQueue.setMovie_num_index(1);
        downloadQueue.setMovie_num_title("第一集");
        downloadQueue.setState(JDDownloadQueueState.STATE_DOWNLOAD_QUEUE);//这个比较重要，代表可以开始或停止下载ts文件列表

        String PATH_MOVIE = JDM3U8FileCacheUtils.createRootDownloadPath(MainActivity.this) + File.separator + "download" + File.separator + "movie" + File.separator;

        //
        JDM3U8Downloader jdm3U8Downloader = new JDM3U8Downloader.Builder()
                .targetDir(PATH_MOVIE)
                .DownloadQueue(downloadQueue)
                //.AbstractDownloader(new OkHttpDownloader())//可自定义m3u8相关文件下载器
                .GetM3U8FileListener(new JDM3U8DownloaderContract.GetM3U8FileListener() {
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
~~~~~~~~~


### 下载流程，可归纳为以下六个步骤：
0、网络请求获取到m3u8多码率的文件内容<br/>
1、得到单码率的下载地址(JDM3U8SingleRateUrlBean)<br/>
2、网络请求获取到m3u8单码率的文件内容<br/>
3、得到ts列表的下载地址<br/>
4、下载各个ts文件<br/>
5、保存一份供本地播放的单码率m3u8文件<br/>


#### （注意）从多码率文件中取出第一个指定码率的m3u8下载地址
http://yi.jingdianzuida.com/20190905/yM4FKbnk/index.m3u8
~~~~~~~~
#EXTM3U
#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=1000000,RESOLUTION=1080x608
/ppvod/30BF460930E1ABAB6E46E4AF20BF4AF4.m3u8
~~~~~~~~
JDM3U8BaseDownloader中默认取多码率文件内容中的第三行
（自己可重写public JDM3U8SingleRateUrlBean getM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> dataList)方法）

多码率中第三行内容（即单码率的m3u8的路径）<br/>
如果是以/开头的话，使用多码率的下载地址url主机加该内容；（即http://yi.jingdianzuida.com拼接上/ppvod/30BF460930E1ABAB6E46E4AF20BF4AF4.m3u8）
所以该http://yi.jingdianzuida.com/ppvod/30BF460930E1ABAB6E46E4AF20BF4AF4.m3u8<br/>

如果不是以/开头的话，截取多码率的url到最后一个/杠，再加多码率中第三行内容

从指定码率文件取出每个ts文件下载地址也是同样的规则：<br/>
// 在单码率m3u8文件ts列表的ts文件下载地址<br/>
// 如果是以/开头的话，使用单码率的下载地址url主机加该下载地址<br/>
// 如果不是以/开头的话，截取单码率的url到最后一个/杠,再加该下载地址<br/>
~~~~~
#EXTM3U
#EXT-X-VERSION:3
#EXT-X-TARGETDURATION:9
#EXT-X-MEDIA-SEQUENCE:0
#EXTINF:4.17,
/20190905/yM4FKbnk/1000kb/hls/RYHJh4414000.ts
~~~~~

#### （注意）由于我这里的存储方式是：
将这些ts文件存放在单码率文件同一级别目录下<br/>
比如视频的id为1，第2集，那ts文件存储位置就为MOVIE_DIR/1/2/RYHJh4414000.ts,<br/>
m3u8文件存储位置为MOVIE_DIR/1/2/2.m3u8,<br/>
此时如果把所有ts文件下载成功直接使用MOVIE_DIR/1/2/2.m3u8这个路径去播放，是不可播放的，<br/>
因为ts文件的路径为/20190905/yM4FKbnk/1000kb/hls/RYHJh4414000.ts。<br/>
我现在的解决方式：copy一份2.m3u8命名为2_local.m3u8，然后将/20190905/yM4FKbnk/1000kb/hls/前缀替换为空字符，只剩RYHJh4414000.ts<br/>
这样就能索引到ts文件，使用2_local.m3u8这个路径去播放就能达到正常播放的效果。<br/>

