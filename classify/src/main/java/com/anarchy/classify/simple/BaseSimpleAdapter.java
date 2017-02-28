package com.anarchy.classify.simple;

import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;

import java.util.List;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/7 12:00
 * Author: rsshinide38@163.com
 */
public interface BaseSimpleAdapter {
    BaseMainAdapter getMainAdapter();
    BaseSubAdapter getSubAdapter();
    boolean isShareViewPool();
}
