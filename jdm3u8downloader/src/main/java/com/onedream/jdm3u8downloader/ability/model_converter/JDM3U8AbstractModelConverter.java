package com.onedream.jdm3u8downloader.ability.model_converter;

import com.onedream.jdm3u8downloader.core.bean.JDM3U8SingleRateUrlBean;
import com.onedream.jdm3u8downloader.core.bean.JDM3U8TsDownloadUrlBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean类转换器抽象类，
 * 将【处理转换【m3u8单码率的文件内容】得到ts列表的下载地址】功能
 * 分给为两个小功能：
 * 1、从单码率m3u8文件内容中，获取到ts文件下载短链地址列表
 * 2、将每条ts文件下载短链地址，处理转换得到ts文件下载对象: JDM3U8TsDownloadUrlBean
 */
public abstract class JDM3U8AbstractModelConverter implements JDM3U8BaseModelConverter{

    @Override
    public List<JDM3U8TsDownloadUrlBean> convertToM3U8TsDownloadUrlBeanList(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String M3U8SingleRateFileContent) {
        //3-1、获取到ts文件下载短链地址列表
        List<String> tsUrlPathList = convertTsFileDownloadShortUrlList(M3U8SingleRateFileContent);
        if (null == tsUrlPathList || tsUrlPathList.isEmpty()) {
            return null;
        }
        List<JDM3U8TsDownloadUrlBean> JDM3U8TsBeanList = new ArrayList<>();
        for (String tsShortUrl : tsUrlPathList) {
            //3-2、构建单个ts文件对象JDM3U8TsBean
            JDM3U8TsDownloadUrlBean jdm3U8TsBean = convertToM3U8TsDownloadUrlBean(m3U8SingleRateFileDownloadUrlBean, tsShortUrl);
            if (null != jdm3U8TsBean) {
                JDM3U8TsBeanList.add(jdm3U8TsBean);
            }
        }
        return JDM3U8TsBeanList;
    }

    /**
     * 从单码率m3u8文件内容中，获取到ts文件下载短链地址列表
     * @param m3u8SingleRateFileContent  单码率m3u8文件内容
     * @return ts文件下载短链地址列表
     */

    public abstract List<String> convertTsFileDownloadShortUrlList(String m3u8SingleRateFileContent);


    /**
     * 将每条ts文件下载短链地址，处理转换得到ts文件下载对象: JDM3U8TsDownloadUrlBean
     *
     * @param m3U8SingleRateFileDownloadUrlBean m3u8单码率文件下载网址对象
     * @param tsFileDownloadShortUrl          ts文件下载短链地址
     * @return ts网址对象
     */
    public abstract JDM3U8TsDownloadUrlBean convertToM3U8TsDownloadUrlBean(JDM3U8SingleRateUrlBean m3U8SingleRateFileDownloadUrlBean, String tsFileDownloadShortUrl);
}
