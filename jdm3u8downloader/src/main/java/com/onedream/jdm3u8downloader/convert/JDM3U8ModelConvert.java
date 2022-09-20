package com.onedream.jdm3u8downloader.convert;

import com.onedream.jdm3u8downloader.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.bean.JDM3U8TsBean;

import java.util.List;

/**
 * Bean类转换器
 * 主要功能：
 * 0、获取【文件完整网址】，供下面两个功能使用
 * 1、获取【m3u8单码率网址对象】
 * 2、获取【ts网址对象】
 */
public interface JDM3U8ModelConvert {
    /**
     * 获取【文件完整网址】
     *
     * @param m3u8MultiRateFileDownloadUrl 多码率文件网络地址
     * @param lineStr                      文件内容的某一行文字（比如：m3u8单码率短地址、ts文件短地址）
     * @return 完整的文件网络地址
     */
    String convertFullFileUrl(String m3u8MultiRateFileDownloadUrl, String lineStr);

    /**
     * 获取【m3u8单码率网址对象】
     *
     * @param m3u8MultiRateFileDownloadUrl 多码率文件网络地址
     * @param dataList                     多码率文件内容中每一行文字的集合
     * @return {@link JDM3U8SingleRateUrlBean} m3u8单码率网址对象
     */
    JDM3U8SingleRateUrlBean convertM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> dataList);


    /**
     * 获取【ts网址对象】
     *
     * @param m3U8SingleRateFileDownloadUrlBean m3u8单码率网址对象
     * @param lineStr                           m3u8单码率文件中的某一行以ts结尾的
     * @return ts网址对象
     */
    JDM3U8TsBean convertM3U8TsBean(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String lineStr);
}
