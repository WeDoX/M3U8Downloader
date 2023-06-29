package com.onedream.jdm3u8downloader.core.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * @author jdallen
 * @since 2023/06/29
 */
public class JDM3U8FileCacheBaseUtils {

    /**
     * 【清除存储空间】会清除掉该目录，【清除缓存】不会清除掉该目录，
     * 属于【用户数据】，app删除时，该目录也会被一同删除
     */
    public static String createRootFilesDir(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {//外部存储，从Android 4.4起(即SDK 19)不需要读写权限
            // /sdcard/Android/data/<application package>/files
            cacheRootPath = context.getExternalFilesDir("").getPath();
        } else {//内部存储
            // /data/data/<application package>/files
            cacheRootPath = context.getFilesDir().getPath();
        }
        return cacheRootPath;
    }

    /**
     * 清除存储空间】和【清除缓存】都会清除该目录
     * 属于【缓存】，app删除时，该目录也会被一同删除
     */
    public static String createRootCacheDir(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {//外部存储，从Android 4.4起(即SDK 19)不需要读写权限，
            // /sdcard/Android/data/<application package>/cache
            cacheRootPath = context.getExternalCacheDir().getPath();
        } else {//内部存储
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
        writeFile(filePath, content.getBytes(), isAppend);
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


    //读取文件的每一行内容并存储为List<String>
    public static List<String> fileContentToStrList(File file) {
        BufferedReader in = null;
        try {
            List<String> dataList = new ArrayList<>();
            InputStream inputStream = new FileInputStream(file);
            in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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

    private static void printLog(String errMsg) {
        JDM3U8LogHelper.printLog("JDM3U8FileCacheBaseUtils:" + errMsg);
    }
}
