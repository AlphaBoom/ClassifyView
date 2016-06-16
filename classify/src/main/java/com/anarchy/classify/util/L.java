package com.anarchy.classify.util;

import android.util.Log;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/1 16:06
 * Author: zhendong.wu@shoufuyou.com
 * <p>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public class L {
    private static final String TAG = "ClassifyView";
    private static  boolean DEBUG = false;
    public static void setDebugAble(boolean debugAble){
        DEBUG = debugAble;
    }
    public static void d(String msg){
        if(DEBUG){
            Log.d(TAG,msg);
        }
    }
    public static void d(String msg,Object... objects){
        if(DEBUG){
            Log.d(TAG,String.format(msg,objects));
        }
    }
}
