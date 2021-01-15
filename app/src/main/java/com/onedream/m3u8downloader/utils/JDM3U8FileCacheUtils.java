package com.onedream.m3u8downloader.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
            createFile(file);
        return file;
    }

    public static File getM3u8File(String targetDir, long movieId, int movie_num_index) {
        File file = new File(getM3u8FilePath(targetDir, movieId, movie_num_index));
        if (!file.exists())
            createFile(file);
        return file;
    }

    //m3u8 本地播放使用
    public static File getM3u8LocalFile(String targetDir, long movieId, int movie_num_index) {
        File file = new File(getM3u8LocalFilePath(targetDir, movieId, movie_num_index));
        if (!file.exists())
            createFile(file);
        return file;
    }

    //ts文件
    public static String getTsFilePath(String targetDir, long movieId, int movie_num_index, String tsFileName) {
        return targetDir + movieId + File.separator + movie_num_index + File.separator + tsFileName;
    }

    public static File getTsFileAndIsEmptyNeedCreate(String targetDir, long movieId, int movie_num_index, String tsFileName) {
        File file = new File(getTsFilePath(targetDir, movieId, movie_num_index, tsFileName));
        if (!file.exists())
            createFile(file);
        return file;
    }


    /**
     * 创建根下载目录
     *
     * @return
     */
    public static String createRootDownloadPath(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/files
            cacheRootPath = context.getExternalFilesDir("").getPath();
        } else {
            // /data/data/<application package>/files
            cacheRootPath = context.getExternalFilesDir("").getPath();
        }
        return cacheRootPath;
    }

    /**
     * 创建根缓存目录
     *
     * @return
     */
    public static String createRootPath(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            cacheRootPath = context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = context.getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 递归创建文件夹
     *
     * @param dirPath
     * @return 创建失败返回""
     */
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                printLog("----- 创建文件夹" + file.getAbsolutePath());
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                printLog("----- 创建文件夹" + file.getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * 递归创建文件夹
     *
     * @param file
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                printLog("----- 创建文件" + file.getAbsolutePath());
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
                printLog("----- 创建文件" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            printLog("----- 创建文件出现异常" + e.toString());
        }
        return "";
    }

    /**
     * 将内容写入文件
     *
     * @param filePath eg:/mnt/sdcard/demo.txt
     * @param content  内容
     * @param isAppend 是否追加
     */
    public static void writeFile(String filePath, String content, boolean isAppend) {
        writeFile(filePath,content.getBytes(),isAppend);
    }

    public static void writeFile(String filePath, byte[] bytes, boolean isAppend) {
        printLog("save:" + filePath);
        try {
            FileOutputStream fout = new FileOutputStream(filePath, isAppend);
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            for (int n; (n = stream.read(b)) != -1; ) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }


    /**
     * 删除指定文件，如果是文件夹，则递归删除
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFileOrDirectory(File file) throws IOException {
        try {
            if (file != null && file.isFile()) {
                return file.delete();
            }
            if (file != null && file.isDirectory()) {
                File[] childFiles = file.listFiles();
                // 删除空文件夹
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }
                // 递归删除文件夹下的子文件
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFileOrDirectory(childFiles[i]);
                }
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public static void clearInfoForFile(File file) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            JDM3U8CloseUtils.close(fileWriter);
        }
    }

    private static void printLog(String errMsg) {
        JDM3U8LogHelper.printLog("JDM3U8FileCacheUtils:" + errMsg);
    }


    //m3u8多码率文件操作
    public String getM3U8TopFileContent(String targetDir, long movieId, int movie_num_index) {
        File file = JDM3U8FileCacheUtils.getM3u8TopFile(targetDir, movieId, movie_num_index);
        if (null != file) {
            byte[] data = JDM3U8FileCacheUtils.getBytesFromFile(file);
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
            byte[] data = JDM3U8FileCacheUtils.getBytesFromFile(file);
            if (null != data) {
                return new String(data);
            }
        }
        return null;
    }

    public static void saveM3U8TopFile(String targetDir, long movieId, int movie_num_index, String content) {
        File file = getM3u8TopFile(targetDir, movieId, movie_num_index);
        writeFile(file.getAbsolutePath(), content, false);
    }

    public static void saveM3U8File(String targetDir, long movieId, int movie_num_index, String content) {
        File file = getM3u8File(targetDir, movieId, movie_num_index);
        writeFile(file.getAbsolutePath(), content, false);
    }

    public static void saveM3U8LocalFile(String targetDir, long movieId, int movie_num_index, String content) {
        File file = getM3u8LocalFile(targetDir, movieId, movie_num_index);
        writeFile(file.getAbsolutePath(), content, false);
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

    //读取文件的每一行内容并存储为List<String>
    public static List<String> fileContentToStrList(File file) {
        BufferedReader in = null;
        try {
            List<String> dataList = new ArrayList<>();
            InputStream inputStream = new FileInputStream(file);
            in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                if (!TextUtils.isEmpty(line)) {
                    dataList.add(line);
                }
            }
            return dataList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDM3U8CloseUtils.close(in);
        }
        return null;
    }

}