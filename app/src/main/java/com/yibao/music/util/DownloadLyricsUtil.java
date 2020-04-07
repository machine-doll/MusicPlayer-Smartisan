package com.yibao.music.util;

import android.util.Log;

import com.yibao.music.MusicApplication;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author Luoshipeng
 * @ Author: Luoshipeng
 * @ Name:   DownloadLyricsUtil
 * @ Time:   2018/10/3/ 20:18
 * @ Des:    下载网络歌词
 */
public class DownloadLyricsUtil {

    /**
     * 字符串写入本地txt
     *
     * @param strcontent 文件内容
     * @param songName   文件地址
     * @param artist     文件名
     * @return 写入结果
     */
    public static boolean saveLyrics(String strcontent, String songName, String artist) {
        boolean isSavaFile;
        String path = null;
        if (!CheckBuildVersionUtil.checkAndroidVersionQ()) {

            File lyricsFile = new File(Constants.MUSIC_LYRICS_ROOT);
            if (!lyricsFile.exists()) {
                lyricsFile.mkdirs();
            }
            path = Constants.MUSIC_LYRICS_ROOT + songName + "$$" + artist + ".lrc";
        }
        String strContent = strcontent + "\r\n";
        try {
            File file = CheckBuildVersionUtil.checkAndroidVersionQ() ?
                    FileUtil.createFile(MusicApplication.getInstance(), songName + "$$" + artist + ".lrc", Constants.SONG_LYRICS)
                    : new File(path);

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
            isSavaFile = true;
        } catch (Exception e) {
            isSavaFile = false;
            Log.e("TestFile", "Error on write File:" + e);
        }
        return isSavaFile;
    }

}
