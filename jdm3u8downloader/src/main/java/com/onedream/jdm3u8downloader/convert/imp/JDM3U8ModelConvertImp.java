package com.onedream.jdm3u8downloader.convert.imp;

import androidx.annotation.NonNull;

import com.onedream.jdm3u8downloader.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;
import com.onedream.jdm3u8downloader.convert.JDM3U8ModelConvert;
import com.onedream.jdm3u8downloader.utils.JDM3U8UrlUtils;
import com.onedream.jdm3u8downloader.utils.JDM3U8LogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JDM3U8ModelConvertImp implements JDM3U8ModelConvert {

    @Override
    public String convertFullFileUrl(String baseUrl, String lineStr) {
        if (null == baseUrl || baseUrl.isEmpty()) {
            return null;
        }
        if (null == lineStr || lineStr.isEmpty()) {
            return null;
        }
        // 如果是以/开头的话，使用多码率的下载地址url主机，加该内容；
        // 如果不是以/开头的话，截取多码率的url到最后一个/杠(即相对地址)，加该内容；
        if (theUrlIsAbsoluteUrl(lineStr)) {
            return JDM3U8UrlUtils.getHostAddressByUrlStr(baseUrl) + lineStr;
        } else {
            return JDM3U8UrlUtils.getRelativePathByUrlStr(baseUrl) + lineStr;
        }
    }

    @Override
    public JDM3U8SingleRateUrlBean convertM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> dataList) {
        if (null == dataList || dataList.size() <= 0) {
            return null;
        }
        for (String lineStr : dataList) {
            //这里由之前的取第三行，更改为
            //取【不以“#”开头】并且【以".m3u8"结尾】字符串去做对应的拼接获取到完整的文件地址
            if (!lineStr.startsWith("#") && lineStr.endsWith(".m3u8")) {
                String fullUrlStr = convertFullFileUrl(m3u8MultiRateFileDownloadUrl, lineStr);
                if (null == fullUrlStr || fullUrlStr.isEmpty()) {
                    return null;
                }
                return new JDM3U8SingleRateUrlBean(fullUrlStr);
            }

        }
        return null;
    }

    @Override
    public List<String> convertTsFileDownloadShortUrlList(String content) {
        Pattern pattern = Pattern.compile(".*ts");
        Matcher ma = pattern.matcher(content);
        List<String> list = new ArrayList<String>();
        while (ma.find()) {
            String s = ma.group();
            list.add(s);
            JDM3U8LogHelper.printLog("analysisIndex获取的ts文件" + s);
        }
        return list;
    }


    @Override
    public JDM3U8TsBean convertM3U8TsBean(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String tsFileDownloadShortUrl) {
        String tsFileName = null;
        String oldString = null;
        if (null == tsFileDownloadShortUrl || tsFileDownloadShortUrl.isEmpty()) {
            return null;
        }
        if (theUrlIsAbsoluteUrl(tsFileDownloadShortUrl)) {
            //绝对路径
            String[] name = JDM3U8UrlUtils.getTsOldStrAndFileNameStr(tsFileDownloadShortUrl);
            oldString = name[0];
            tsFileName = name[1];
        } else {
            //相对路径：由于我们这里只需要一个单码率的本地播放地址，所以需要截取。
            if (tsFileDownloadShortUrl.contains("/")) {//250kbit/seq-0.ts变为seq-0.ts
                String[] name = JDM3U8UrlUtils.getTsOldStrAndFileNameStr(tsFileDownloadShortUrl);
                oldString = name[0];
                tsFileName = name[1];
            } else {
                tsFileName = tsFileDownloadShortUrl;
            }
        }
        String fullUrlString = convertFullFileUrl(m3U8SingleRateFileDownloadUrlBean.getM3u8FileDownloadUrl(), tsFileDownloadShortUrl);
        return new JDM3U8TsBean(tsFileName, oldString, fullUrlString);
    }

    // 如果是以/开头的话，使用多码率的下载地址url主机，加该内容；
    // 如果不是以/开头的话，截取多码率的url到最后一个/杠(即相对地址)，加该内容；
    public boolean theUrlIsAbsoluteUrl(@NonNull String lineStr) {
        return lineStr.startsWith("/");
    }
}
