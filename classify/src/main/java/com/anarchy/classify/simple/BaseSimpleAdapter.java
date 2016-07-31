package com.anarchy.classify.simple;

import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/7 12:00
 * Author: zhendong.wu@shoufuyou.com
 * <p>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public interface BaseSimpleAdapter {
    BaseMainAdapter getMainAdapter();
    BaseSubAdapter getSubAdapter();
    boolean isShareViewPool();
}
