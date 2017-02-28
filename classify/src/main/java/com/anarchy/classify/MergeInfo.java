package com.anarchy.classify;

/**
 * <p>
 * Date: 16/6/22 09:44
 * Author: rsshinide38@163.com
 *
 * 执行合并动画所需要的属性
 * targetX 及 targetY 为相对于ClassifyView 的 x轴与y轴坐标
 * <p>
 */
public class MergeInfo {
    public float scaleX;
    public float scaleY;
    public float targetX;
    public float targetY;

    public MergeInfo(float scaleX, float scaleY, float targetX, float targetY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.targetX = targetX;
        this.targetY = targetY;
    }
}
