package com.onedream.m3u8downloader.utils;

import android.util.Log;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8LogHelper {
    public static void printLog(String logContent) {
        printLog("ATU", logContent);
    }

    public static void printLog(String tag, String logContent) {
        Log.e(tag, logContent);
    }
}
