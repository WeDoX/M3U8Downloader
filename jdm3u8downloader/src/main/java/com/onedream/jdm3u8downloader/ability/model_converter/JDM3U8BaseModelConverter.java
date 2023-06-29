package com.onedream.jdm3u8downloader.ability.model_converter;

import com.onedream.jdm3u8downloader.core.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.core.bean.JDM3U8TsDownloadUrlBean;

import java.util.List;


/**
 * Bean类转换器
 * 主要功能：
 * 1、处理转换【m3u8多码率的文件内容】得到单码率的下载地址(JDM3U8SingleRateUrlBean)
 * 2、处理转换【m3u8单码率的文件内容】得到ts列表的下载地址
 */

public interface JDM3U8BaseModelConverter {
    /**
     * 处理转换【m3u8多码率的文件内容】得到单码率的下载地址(JDM3U8SingleRateUrlBean)
     *
     * @param m3u8MultiRateFileDownloadUrl        多码率文件网络地址
     * @param m3U8MultiRateFileContentLineStrList 多码率文件内容中每一行文字的集合
     * @return {@link JDM3U8SingleRateUrlBean}              m3u8单码率文件下载网址对象
     */
    JDM3U8SingleRateUrlBean covertToM3U8SingleRateUrlBean(String m3u8MultiRateFileDownloadUrl, List<String> m3U8MultiRateFileContentLineStrList);

    /**
     * 处理转换【m3u8单码率的文件内容】得到ts列表的下载地址
     *
     * @param m3U8SingleRateFileDownloadUrlBean m3u8单码率文件下载网址对象
     * @param m3U8SingleRateFileContent         m3u8单码率文件内容
     * @return {@link  List<JDM3U8TsDownloadUrlBean>} m3u8单码率文件中ts下载网址对象列表
     */
    List<JDM3U8TsDownloadUrlBean> convertToM3U8TsDownloadUrlBeanList(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String m3U8SingleRateFileContent);
}
