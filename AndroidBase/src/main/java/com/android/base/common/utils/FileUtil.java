package com.android.base.common.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.util.IOUtils;
import com.apkfuns.logutils.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * @author lujianzhao
 * @date 2014-08-10
 */
public class FileUtil {

//    private static final String TAG = FileUtil.class.getSimpleName();

    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_ADUIO = 2;
    private static final int TYPE_VIDEO = 3;

    public static final String ROOT_DIR = "Android/data/"+ UIUtil.getPackageName();
    public static final String DOWNLOAD_DIR = "download";
    public static final String CACHE_DIR = "cache";
    public static final String ICON_DIR = "icon";

    /** * 清除本应用所有的数据 * * @param context * @param filepath */
    public static void cleanApplicationData() {
        delAllFile(UIUtil.getContext().getCacheDir().getAbsolutePath());
        delAllFile(UIUtil.getContext().mAppDir);
        //清理Webview缓存数据库
        try {
            UIUtil.getContext().deleteDatabase("webview.db");
            UIUtil.getContext().deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 判断SD卡是否挂载 */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /** 获取下载目录 */
    public static String getDownloadDir() {
        return getDir(DOWNLOAD_DIR);
    }

    /** 获取缓存目录 */
    public static String getCacheDir() {
        return getDir(CACHE_DIR);
    }

    /** 获取icon目录 */
    public static String getIconDir() {
        return getDir(ICON_DIR);
    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path
     *            String 文件夹路径 如 /sdcard/data/
     */
    public static void delAllFile(String path) {
        LogUtils.d("删除:" +path);
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            System.out.println(path + tempList[i]);
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]); // 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]); // 再删除空文件夹
            }
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath
     *            String 文件夹路径及名称 如/sdcard/data/
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            System.out.println("删除文件夹操作出错");
            e.printStackTrace();
        }
    }

    /** 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录 */
    public static String getDir(String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());
        } else {
            sb.append(getCachePath());
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return null;
        }
    }

    /** 获取SD下的应用目录 */
    public static String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(ROOT_DIR);
        sb.append(File.separator);
        return sb.toString();
    }

    /** 获取应用的cache目录 */
    public static String getCachePath() {
        File f = UIUtil.getContext().getCacheDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /** 创建文件夹 */
    public static boolean createDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }

    /** 复制文件，可以选择是否删除源文件 */
    public static boolean copyFile(String srcPath, String destPath, boolean deleteSrc) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        return copyFile(srcFile, destFile, deleteSrc);
    }

    /** 复制文件，可以选择是否删除源文件 */
    public static boolean copyFile(File srcFile, File destFile, boolean deleteSrc) {
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = in.read(buffer)) > 0) {
                out.write(buffer, 0, i);
                out.flush();
            }
            if (deleteSrc) {
                srcFile.delete();
            }
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        } finally {
            IOUtils.close(out);
            IOUtils.close(in);
        }
        return true;
    }

    /** 判断文件是否可写 */
    public static boolean isWriteable(String path) {
        try {
            if (TextUtils.isEmpty(path)) {
                return false;
            }
            File f = new File(path);
            return f.exists() && f.canWrite();
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        }
    }

    /** 修改文件的权限,例如"777"等 */
    public static void chmod(String path, String mode) {
        try {
            String command = "chmod " + mode + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 把数据写入文件
     * @param is       数据流
     * @param path     文件路径
     * @param recreate 如果文件存在，是否需要删除重建
     * @return 是否写入成功
     */
    public static boolean writeFile(InputStream is, String path, boolean recreate) {
        boolean res = false;
        File f = new File(path);
        FileOutputStream fos = null;
        try {
            if (recreate && f.exists()) {
                f.delete();
            }
            if (!f.exists() && null != is) {
                File parentFile = new File(f.getParent());
                parentFile.mkdirs();
                int count = -1;
                byte[] buffer = new byte[1024];
                fos = new FileOutputStream(f);
                while ((count = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                    LogUtils.d("下载中");
                }
                res = true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fos);
            IOUtils.close(is);
        }
        return res;
    }

    /**
     * 把字符串数据写入文件
     * @param content 需要写入的字符串
     * @param path    文件路径名称
     * @param append  是否以添加的模式写入
     * @return 是否写入成功
     */
    public static boolean writeFile(byte[] content, String path, boolean append) {
        boolean res = false;
        File f = new File(path);
        RandomAccessFile raf = null;
        try {
            if (f.exists()) {
                if (!append) {
                    f.delete();
                    f.createNewFile();
                }
            } else {
                f.createNewFile();
            }
            if (f.canWrite()) {
                raf = new RandomAccessFile(f, "rw");
                raf.seek(raf.length());
                raf.write(content);
                res = true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(raf);
        }
        return res;
    }

    /**
     * 把字符串数据写入文件
     * @param content 需要写入的字符串
     * @param path    文件路径名称
     * @param append  是否以添加的模式写入
     * @return 是否写入成功
     */
    public static boolean writeFile(String content, String path, boolean append) {
        return writeFile(content.getBytes(), path, append);
    }

    /**
     * 把键值对写入文件
     * @param filePath 文件路径
     * @param key      键
     * @param value    值
     * @param comment  该键值对的注释
     */
    public static void writeProperties(String filePath, String key, String value, String comment) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(filePath)) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);// 先读取文件，再把键值对追加到后面
            p.setProperty(key, value);
            fos = new FileOutputStream(f);
            p.store(fos, comment);
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
            IOUtils.close(fos);
        }
    }

    /** 根据值读取 */
    public static String readProperties(String filePath, String key, String defaultValue) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(filePath)) {
            return null;
        }
        String value = null;
        FileInputStream fis = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);
            value = p.getProperty(key, defaultValue);
        } catch (IOException e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
        }
        return value;
    }

    /** 把字符串键值对的map写入文件 */
    public static void writeMap(String filePath, Map<String, String> map, boolean append, String comment) {
        if (map == null || map.size() == 0 || TextUtils.isEmpty(filePath)) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            Properties p = new Properties();
            if (append) {
                fis = new FileInputStream(f);
                p.load(fis);// 先读取文件，再把键值对追加到后面
            }
            p.putAll(map);
            fos = new FileOutputStream(f);
            p.store(fos, comment);
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
            IOUtils.close(fos);
        }
    }

    /** 把字符串键值对的文件读入map */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<String, String> readMap(String filePath, String defaultValue) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        Map<String, String> map = null;
        FileInputStream fis = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);
            map = new HashMap<String, String>((Map) p);// 因为properties继承了map，所以直接通过p来构造一个map
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
        }
        return map;
    }

    /** 改名 */
    public static boolean copy(String src, String des, boolean delete) {
        File file = new File(src);
        if (!file.exists()) {
            return false;
        }
        File desFile = new File(des);
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(desFile);
            byte[] buffer = new byte[1024];
            int count = -1;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
                out.flush();
            }
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
        if (delete) {
            file.delete();
        }
        return true;
    }

    public static String getFileName(String url) {
        File file = new File(url);
        String name=file.getName();
        name = name.substring(0,name.lastIndexOf("."));
        return name;
    }

    /**
     * {@link #TYPE_IMAGE}<br/>
     * {@link #TYPE_ADUIO}<br/>
     * {@link #TYPE_VIDEO} <br/>
     *
     * @param type
     * @return
     */
    private static String getPublicFilePath(int type) {
        String fileDir = null;
        String fileSuffix = null;
        switch (type) {
            case TYPE_ADUIO:
                fileDir = UIUtil.getContext().mVoicesDir;
                fileSuffix = ".mp3";
                break;
            case TYPE_VIDEO:
                fileDir = UIUtil.getContext().mVideosDir;
                fileSuffix = ".mp4";
                break;
            case TYPE_IMAGE:
                fileDir = UIUtil.getContext().mPicturesDir;
                fileSuffix = ".jpg";
                break;
        }
        if (fileDir == null) {
            return null;
        }
        File file = new File(fileDir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
        }
        return fileDir + File.separator + UUID.randomUUID().toString().replaceAll("-", "") + fileSuffix;
    }

    /**
     * {@link #TYPE_ADUIO}<br/>
     * {@link #TYPE_VIDEO} <br/>
     *
     * @param type
     * @return
     */
    private static String getPrivateFilePath(int type, String userId) {
        String fileDir = null;
        String fileSuffix = null;
        switch (type) {
            case TYPE_ADUIO:
                fileDir = UIUtil.getContext().mAppDir + File.separator + userId + File.separator + Environment.DIRECTORY_MUSIC;
                fileSuffix = ".mp3";
                break;
            case TYPE_VIDEO:
                fileDir = UIUtil.getContext().mAppDir + File.separator + userId + File.separator + Environment.DIRECTORY_MOVIES;
                fileSuffix = ".mp4";
                break;
        }
        if (fileDir == null) {
            return null;
        }
        File file = new File(fileDir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
        }
        return fileDir + File.separator + UUID.randomUUID().toString().replaceAll("-", "") + fileSuffix;
    }

    public static String getImageFilePath() {
        return getPublicFilePath(TYPE_IMAGE);
    }

    public static String getAudioFilePath() {
        return getPublicFilePath(TYPE_ADUIO);

    }

    public static String getAudioAmrFilePath() {
        String filePath = null;
        filePath = getPublicFilePath(TYPE_ADUIO);
        if (!TextUtils.isEmpty(filePath)) {
            return filePath.replace(".mp3", ".amr");
        } else {
            return null;
        }
    }

    public static String getRandomVideoFilePath() {
        return getPublicFilePath(TYPE_VIDEO);
    }




    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            FileChannel in = fi.getChannel();//得到对应的文件通道
            FileChannel out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null) fo.close();
                if (fi != null) fi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static String formatFileSizeToString(long fileLen) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileLen < 1024) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576) {
            fileSizeString = df.format((double) fileLen / 1024) + "K";
        } else if (fileLen < 1073741824) {
            fileSizeString = df.format((double) fileLen / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /***
     * 根据路径删除图片
     */
    public static boolean deleteFile(File file)throws IOException{
        return file != null && file.delete();
    }

    /***
     * 获取文件扩展名
     * @param filename
     * @return 返回文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }


    /**
     * 读取指定文件的输出
     */
    public static String getFileOutputString(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path), 8192);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append("\n").append(line);
            }
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}