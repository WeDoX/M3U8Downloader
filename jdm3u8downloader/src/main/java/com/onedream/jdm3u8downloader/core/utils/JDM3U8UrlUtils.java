package com.onedream.jdm3u8downloader.core.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8UrlUtils {
    /**
     * 将"/a/d/1.ts"分成"/a/d/"和"1.ts"
     * 由于类似/a/d/1.ts在本地无法播放，需将单码率文件的ts列表的/a/d/1.ts更改为1.ts，并将这些ts文件存放在单码率文件同一级别目录下
     *
     * @param tsFileUrl 单码率文件ts列表的单个ts字符串
     * @return [0] -旧名字，供后面保存本地的单码率的m3u8文件提供替换字符串。 [1]-用来作为ts文件的名称
     */

    public static String[] getTsOldStrAndFileNameStr(String tsFileUrl) {//isHostAddress为true时才调用
        String[] name = new String[2];
        name[0] = tsFileUrl.substring(0, tsFileUrl.lastIndexOf("/") + 1);
        name[1] = tsFileUrl.substring(tsFileUrl.lastIndexOf("/") + 1);
        return name;
    }

    //http://mei.huazuida.com/20191025/12127_6302fa66/index.m3u8 ===> http://mei.huazuida.com
    public static String getHostAddressByUrlStr(String urlStr) {
        if (!TextUtils.isEmpty(urlStr) && urlStr.length() > 10) {
            String realHostAddress = urlStr.substring(0, urlStr.indexOf("/", "http://".length()));//从http://开始算第一个斜杆的前面的既是主机域名
            JDM3U8LogHelper.printLog(urlStr + "的主机域名为:" + realHostAddress);
            return realHostAddress;
        }
        return urlStr;
    }

    //http://mei.huazuida.com/20191025/12127_6302fa66/index.m3u8 ===>http://mei.huazuida.com/20191025/12127_6302fa66/
    public static String getRelativePathByUrlStr(String urlStr) {
        if (!TextUtils.isEmpty(urlStr) && urlStr.length() > 1) {
            String relativePath = urlStr.substring(0, urlStr.lastIndexOf("/") + 1);//这里加1的原因是，substring(int beginIndex, int endIndex)截取不包含endIndex对应的字符
            JDM3U8LogHelper.printLog(urlStr + "的相对路径为:" + relativePath);
            return relativePath;
        }
        return urlStr;
    }

    public static String convertFullFileUrl(String baseUrl, String lineStr) {
        if (null == baseUrl || baseUrl.isEmpty()) {
            return null;
        }
        if (null == lineStr || lineStr.isEmpty()) {
            return null;
        }
        // 如果是以/开头的话，使用多码率的下载地址url主机，加该内容；
        // 如果不是以/开头的话，截取多码率的url到最后一个/杠(即相对地址)，加该内容；
        if (theUrlIsAbsoluteUrl(lineStr)) {
            return getHostAddressByUrlStr(baseUrl) + lineStr;
        } else {
            return getRelativePathByUrlStr(baseUrl) + lineStr;
        }
    }

    // 如果是以/开头的话，使用多码率的下载地址url主机，加该内容；
    // 如果不是以/开头的话，截取多码率的url到最后一个/杠(即相对地址)，加该内容；
    public static boolean theUrlIsAbsoluteUrl(@NonNull String lineStr) {
        return lineStr.startsWith("/");
    }
}
