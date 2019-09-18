package com.yibao.music.util;

import androidx.annotation.NonNull;

import com.yibao.music.base.listener.LyricsCallBack;
import com.yibao.music.model.LyricDownBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Luoshipeng
 * @ Author: Luoshipeng
 * @ Name:   DownloadLyricsUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/10/3/ 20:18
 * @ Des:    下载网络歌词
 */
public class DownloadLyricsUtil {
    private static final String ONLINE_LYRICS_URL = "http://geci.me/api/lyric/";

    /**
     * 获取网络歌词的下载地址
     */
    public static synchronized void downloadLyricUrl(String songName, String songArtist, LyricsCallBack callBack) {
        // 实际歌词的查询条件
        String queryLrcURL = getQueryLrcURL(songName, songArtist);

        LogUtil.d("     查询歌词地址    ====    " + queryLrcURL);

        ThreadPoolProxyFactory.newInstance().execute(() -> OkHttpUtil.downFile(queryLrcURL, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callBack.lyricsUri(false, e.toString());
                LogUtil.d("歌词地址下载失败   ==  " + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream inputStream = getInputStream(response);
                if (inputStream != null) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String temp;
                    while ((temp = in.readLine()) != null) {
                        sb.append(temp);
                    }
                    try {
                        JSONObject jObject = new JSONObject(sb.toString());
                        int count = jObject.getInt("count");
                        LogUtil.d(" 歌词地址数量 : ==   " + count);
                        if (count > 0) {
                            JSONArray jArray = jObject.getJSONArray("result");
                            JSONObject obj = jArray.getJSONObject(0);
                            String lyricsUrl = obj.getString("lrc");
                            LogUtil.d("歌词下载地址   ====  AA  " + lyricsUrl);
                            callBack.lyricsUri(true, lyricsUrl);
                        } else {
                            callBack.lyricsUri(false, null);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callBack.lyricsUri(false, null);
                    }
                }
            }
        }));

    }

    private static String getQueryLrcURL(String title, String artist) {
        String unknownName = "<unknown>";
        String str = ONLINE_LYRICS_URL + encode(title);
        return unknownName.equals(artist) || "群星".equals(artist) ? str : str + "/" + encode(artist);
    }

    /**
     * 将网络歌词文件本地
     *
     * @param url 歌词缓冲地址
     */
    public static void downloadlyricsfile(String url, String songName, String artist) {
        String path = Constants.MUSIC_LYRICS_ROOT + songName + "$$" + artist + ".lrc";
        OkHttpUtil.downFile(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                sentResult(false, path, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                byte[] buf = new byte[1024 * 2];
                int len;
                int off = 0;
                FileOutputStream fos;
                InputStream inputStream = getInputStream(response);
                if (inputStream != null) {
                    try {
                        fos = new FileOutputStream(FileUtil.getLyricsFile(songName, artist));
                        while ((len = inputStream.read(buf)) != -1) {
                            fos.write(buf, off, len);
                        }
                        fos.flush();
                        fos.close();
                        inputStream.close();
                        sentResult(true, null, "OK");
                        LogUtil.d("========= 歌词下载完成 ======final =====   ");
                    } catch (IOException e) {
                        e.printStackTrace();
                        sentResult(false, path, e.getMessage());
                        LogUtil.d("========= 歌词下载失败 ======final =====   ");
                    }
                }
            }


        });
    }


    /**
     * @param b   歌词是否下载成功
     * @param msg 下载的信息，下载成功为 “OK”，下载失败或者下载异常为 Exception.getMessage()
     */
    private static void sentResult(boolean b, String path, String msg) {
        LyricDownBean lyricDownBean = new LyricDownBean(b, msg);
        RxBus.getInstance().post(Constants.MUSIC_LYRIC_OK, lyricDownBean);
        if (!b) {
            // 下载失败或者下载异常，将已经创建的歌词文件删除，以便重新下载完整的歌词。
            FileUtil.deleteFile(new File(path));
        }
    }

    private static InputStream getInputStream(Response response) {
        ResponseBody body = response.body();
        if (body != null) {
            return body.byteStream();
        }
        return null;
    }

    // 对歌名和歌手名中的空格进行转码

    private static String encode(String str) {
        try {
            return URLEncoder.encode(str.trim(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

}
