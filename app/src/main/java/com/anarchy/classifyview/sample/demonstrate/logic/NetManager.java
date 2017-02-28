package com.anarchy.classifyview.sample.demonstrate.logic;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p/>
 * Date: 16/6/12 11:37
 * Author: rsshinide38@163.com
 * <p/>
 */
public class NetManager {
    private static final String BASE_URL = "http://www.tngou.net/api/book";
    private static final int SUCCESS = 1;
    private static final int FAILURE = 0;

    private String get(String path) throws Exception {
        String result = "";
        StringBuilder sb = new StringBuilder();
        URL url = new URL(BASE_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == 200) {
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            reader.close();
            result = sb.toString();
        }
        return result;
    }


    public String getClassify() throws Exception {
        return get("/classify");
    }

    public String getList(String page, String rows, String id) throws Exception {
        Uri uri = Uri.parse("/list");
        Uri.Builder builder = uri.buildUpon();
        if(!TextUtils.isEmpty(page)) {
            builder.appendQueryParameter("page", page);
        }
        if(!TextUtils.isEmpty(rows)) {
            builder.appendQueryParameter("rows", rows);
        }
        if(!TextUtils.isEmpty(id)) {
            builder.appendQueryParameter("id", id);
        }
        uri = builder.build();
        return get(uri.toString());
    }

    public String getDetail(String id) throws Exception {
        Uri uri = Uri.parse("/show");
        uri = uri.buildUpon().appendQueryParameter("id", id).build();
        return get(uri.toString());
    }


    public void getBookList(final BookListener bookListener) {
        final Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                int state = msg.what;
                if(state == SUCCESS){
                    bookListener.onSuccess((String) msg.obj);
                }else {
                    bookListener.onFailure((Exception) msg.obj);
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = getList(null,40+"",null);
                    Message message = Message.obtain();
                    message.what = SUCCESS;
                    message.obj = result;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = FAILURE;
                    message.obj = e;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
}
