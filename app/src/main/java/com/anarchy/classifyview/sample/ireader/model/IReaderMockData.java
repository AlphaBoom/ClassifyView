package com.anarchy.classifyview.sample.ireader.model;

import android.graphics.Color;

/**
 * Version 2.1.1
 * <p>
 * Date: 16/12/27 16:40
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 * Copyright © 2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */

public class IReaderMockData {
    private boolean isChecked;
    private int color = Color.BLUE;
    private String category;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;

    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
