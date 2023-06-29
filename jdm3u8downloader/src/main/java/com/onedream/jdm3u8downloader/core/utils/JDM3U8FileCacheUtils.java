package com.onedream.jdm3u8downloader.core.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * @author jdallen
 * @since 2020/4/3
 */
public class JDM3U8FileCacheUtils {
    public static final String SUFFIX_M3U8 = ".m3u8";

    //m3u8Top文件（多码率）
    public static String getM3u8TopFilePath(String targetDir, long movieId, int movie_num_index) {
        return targetDir + movieId + File.separator + movie_num_index + File.separator + movie_num_index + "_top" + SUFFIX_M3U8;
    }

    //m3u8
    public static String getM3u8FilePath(String targetDir, long movieId, int movie_num_index) {
        return targetDir + movieId + File.separator + movie_num_index + File.separator + movie_num_index + SUFFIX_M3U8;
    }

    //m3u8 本地播放使用
    public static String getM3u8LocalFilePath(String targetDir, long movieId, int movie_num_index) {
        return targetDir + movieId + File.separator + movie_num_index + File.separator + movie_num_index + "_local" + SUFFIX_M3U8;
    }

    //m3u8多码率
    public static File getM3u8TopFile(String targetDir, long movieId, int movie_num_index) {
        File file = new File(getM3u8TopFilePath(targetDir, movieId, movie_num_index));
        if (!file.exists())
            JDM3U8FileCacheBaseUtils.createFile(file);
        return file;
    }

    public static File getM3u8File(String targetDir, long movieId, int movie_num_index) {
        File file = new File(getM3u8FilePath(targetDir, movieId, movie_num_index));
        if (!file.exists())
            JDM3U8FileCacheBaseUtils.createFile(file);
        return file;
    }

    //m3u8 本地播放使用
    public static File getM3u8LocalFile(String targetDir, long movieId, int movie_num_index) {
        File file = new File(getM3u8LocalFilePath(targetDir, movieId, movie_num_index));
        if (!file.exists())
            JDM3U8FileCacheBaseUtils.createFile(file);
        return file;
    }

    //ts文件
    public static String getTsFilePath(String targetDir, long movieId, int movie_num_index, String tsFileName) {
        return targetDir + movieId + File.separator + movie_num_index + File.separator + tsFileName;
    }

    public static File getTsFileAndIsEmptyNeedCreate(String targetDir, long movieId, int movie_num_index, String tsFileName) {
        File file = new File(getTsFilePath(targetDir, movieId, movie_num_index, tsFileName));
        if (!file.exists())
            JDM3U8FileCacheBaseUtils.createFile(file);
        return file;
    }


    /**
     * 创建根下载目录
     */
    public static String createRootDownloadPath(Context context) {
        return JDM3U8FileCacheBaseUtils.createRootFilesDir(context);
    }

    //m3u8多码率文件操作
    public String getM3U8TopFileContent(String targetDir, long movieId, int movie_num_index) {
        File file = JDM3U8FileCacheUtils.getM3u8TopFile(targetDir, movieId, movie_num_index);
        if (null != file) {
            byte[] data = JDM3U8FileCacheBaseUtils.getBytesFromFile(file);
            if (null != data) {
                return new String(data);
            }
        }
        return null;
    }

    //m3u8文件操作
    public static String getM3U8FileContent(String targetDir, long movieId, int movie_num_index) {
        File file = JDM3U8FileCacheUtils.getM3u8File(targetDir, movieId, movie_num_index);
        if (null != file) {
            byte[] data = JDM3U8FileCacheBaseUtils.getBytesFromFile(file);
            if (null != data) {
                return new String(data);
            }
        }
        return null;
    }

    public static void saveM3U8TopFile(String targetDir, long movieId, int movie_num_index, String content) {
        File file = getM3u8TopFile(targetDir, movieId, movie_num_index);
        JDM3U8FileCacheBaseUtils.writeFile(file.getAbsolutePath(), content, false);
    }

    public static void saveM3U8File(String targetDir, long movieId, int movie_num_index, String content) {
        File file = getM3u8File(targetDir, movieId, movie_num_index);
        JDM3U8FileCacheBaseUtils.writeFile(file.getAbsolutePath(), content, false);
    }

    public static void saveM3U8LocalFile(String targetDir, long movieId, int movie_num_index, String content) {
        File file = getM3u8LocalFile(targetDir, movieId, movie_num_index);
        JDM3U8FileCacheBaseUtils.writeFile(file.getAbsolutePath(), content, false);
    }

    public static File getTsFile(String targetDir, long movieId, int movie_num_index, String tsFileName) {
        File file = getTsFileAndIsEmptyNeedCreate(targetDir, movieId, movie_num_index, tsFileName);
        if (file != null && file.length() > 9)//50)
            return file;
        return null;
    }


    public static void alterStringToCreateNewFile(File m3u8File, File m3u8LocalFile, String oldString, String newString) {
        try {
            long start = System.currentTimeMillis(); //开始时间
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(m3u8File))); //创建对目标文件读取流
            //创建对临时文件输出流，并追加
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(m3u8LocalFile, true)));
            String string = null; //存储对目标文件读取的内容
            int sum = 0; //替换次数
            while ((string = br.readLine()) != null) {
                //判断读取的内容是否包含原字符串
                if (string.contains(oldString)) {
                    //替换读取内容中的原字符串为新字符串
                    string = string.replace(oldString, newString);
                    sum++;
                }
                bw.write(string);
                bw.newLine(); //添加换行
            }
            br.close(); //关闭流，对文件进行删除等操作需先关闭文件流操作
            bw.close();
            long time = System.currentTimeMillis() - start; //整个操作所用时间;
            JDM3U8LogHelper.printLog("ATU AlterStringInFile", sum + "个" + oldString + "替换成" + newString + "耗费时间:" + time);
        } catch (Exception e) {
            JDM3U8LogHelper.printLog("ATU AlterStringInFile", e.getMessage());
        }
    }

    public static void clearInfoForFile(File file) {
        JDM3U8FileCacheBaseUtils.clearInfoForFile(file);
    }

    //读取文件的每一行内容并存储为List<String>
    public static List<String> fileContentToStrList(File file) {
        return JDM3U8FileCacheBaseUtils.fileContentToStrList(file);
    }
}