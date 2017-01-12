package com.anarchy.classifyview.sample.ireader.model;

import android.graphics.Color;

public class IReaderMockData {
    private boolean isChecked;
    private int color = Color.BLUE;
    private IReaderMockDataGroup mParent;





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

    public IReaderMockDataGroup getParent() {
        return mParent;
    }

    public void setParent(IReaderMockDataGroup parent) {
        mParent = parent;
    }
}
