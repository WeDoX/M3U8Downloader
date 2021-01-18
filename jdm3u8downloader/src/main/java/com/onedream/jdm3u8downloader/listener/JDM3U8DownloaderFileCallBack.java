package com.onedream.jdm3u8downloader.listener;


import com.onedream.jdm3u8downloader.utils.JDM3U8CloseUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public abstract class JDM3U8DownloaderFileCallBack extends Callback<File> {

    private File file;

    public JDM3U8DownloaderFileCallBack(File file) {
        this.file = file;
    }

    @Override
    public File parseNetworkResponse(Response response, int id) throws Exception {
        return saveFile(response, id);
    }


    public File saveFile(Response response, final int id) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                    @Override
                    public void run() {

                        inProgress(finalSum * 1.0f / total, total, id);
                    }
                });
            }
            fos.flush();

            return file;

        } finally {
            JDM3U8CloseUtils.close(response.body());
            JDM3U8CloseUtils.close(fos);
        }
    }


}
