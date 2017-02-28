package com.anarchy.classify.simple.widget;


import com.anarchy.classify.simple.ChangeInfo;
import com.anarchy.classify.simple.PrimitiveSimpleAdapter;



public interface CanMergeView {
    /**
     * 进入merge状态
     */
    void onMergeStart();

    /**
     * 离开merge状态
     */
    void onMergeCancel();

    /**
     * 结束merge事件
     */
    void onMerged();

    /**
     * 开始merge动画
     * @param duration  动画持续时间
     */
    void startMergeAnimation(int duration);

    /**
     * 准备merge
     * @return 返回新添加的view 应该放置在布局中的位置坐标
     */
    ChangeInfo prepareMerge();
    /**
     * 设置适配器
     * @param primitiveSimpleAdapter
     */
    void setAdapter(PrimitiveSimpleAdapter primitiveSimpleAdapter);

    /**
     * 初始化或更新主层级
     * @param requestCount 需要显示里面有几个子view
     */
    void initOrUpdateMain(int parentIndex, int requestCount);

    /**
     * 初始化或更新次级层级
     * @param parentIndex
     * @param subIndex
     */
    void initOrUpdateSub(int parentIndex, int subIndex);
}
