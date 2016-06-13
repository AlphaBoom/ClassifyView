package com.anarchy.classifyview.sample.demonstrate.logic;

/**
 * <p/>
 * Date: 16/6/12 14:03
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 */
public interface BookListener {
    void onSuccess(String result);
    void onFailure(Exception e);
}
