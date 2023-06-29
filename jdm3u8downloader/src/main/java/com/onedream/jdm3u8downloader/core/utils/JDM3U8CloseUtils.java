package com.onedream.jdm3u8downloader.core.utils;

import java.io.Closeable;

/**
 * 流关闭工具
 *
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8CloseUtils {

    public static void close(Closeable closeable) {
        try {
            if (null != closeable) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
