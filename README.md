# ClassifyView
类似Launcher效果的拖拽合并的RecyclerView 
#效果如下
![image](https://github.com/AlphaBoom/ClassifyView/blob/master/screenshot/classifyView.gif)

#支持的自定义的属性
ClassifyView attr

属性  | 说明
------------- | -------------
MainSpanCount  | 主层级目录的列数
SubSpanCount  | 次级层级目录的列数
ShadowColor | 展开次级目录时阴影的颜色
AnimationDuration | 打开次级目录的动画及合并动画的时间
SubRatio | 次级目录的高度占主层级的高度比例

InsertAbleGridView(显示合并布局的View)

属性 | 说明
------- | -------
RowCount | 行数（默认 2）
ColumnCount | 列数（默认 2）
RowGap | 横向每列中的间隙距离
ColumnGap | 纵向每行之间的间隙距离
OutLinePadding | 处于可以合并状态及非合并状态 外围框的距离
OutlineWidth | 外边框的宽度
OutlineColor | 外边框的颜色
InnerPadding | 当内部有多个子View 时 与周围的边距

#高级自定义

##继承ClassifyView 重写以下方法：

1. *RecyclerView getMain(Context context, AttributeSet parentAttrs)*<br/>返回主层级使用的 RecyclerView。
2. *RecyclerView getSub(Context context, AttributeSet parentAttrs)*返回次级层级使用的RecyclerView
3. *View chooseTarget(View selected, List<View> swapTargets, int curX, int curY)*<br/> 当拖拽的View 覆盖到子View时会通过该方法在候选View中选择一个View 为目标View 之后的交互操作都会作用于当前所选择的View 及 这个目标View<br/>@param selected 当前选择的View<br/>@param swapTargets 候选的目标View(候选的目标View 为当前选择的View 能够覆盖到所有View)<br/>@param curX 当前选中View的X轴坐标<br/>@param curY 当前选中View的Y轴坐标
4. `Drawable getDragDrawable(View view)`<br/>返回用于渲染当前拖动View的显示<br/>@param 当前选中的View<br/>@return 返回Drawable 用于设置拖拽View的背景

设置数据方式有两种方式：

1. 使用 *ClassifyView.setAdapter(BaseMainAdapter mainAdapter, BaseSubAdapter subAdapter)*用于分别设置主层级及次级层级的适配器
2. 使用 *setAdapter(BaseSimpleAdapter baseSimpleAdapter)*设置一个混合了主层级及次级层级的适配器，如何自定义可以参考 [SimpleAdapter](https://github.com/AlphaBoom/ClassifyView/blob/master/classify/src/main/java/com/anarchy/classify/simple/SimpleAdapter.java)


##主层级提供的回调
在BaseAdapter中对于mergeStart等又增加了ViewHolder形式的回调 本质是一样的。

回调方法 | 说明 | 是否有默认实现在BaseSubAdapter中
------ | ----- | ----
  setDragPosition | 设置当前被拖拽的位置 | true，默认效果为隐藏被拖拽的位置
  boolean canDragOnLongPress|是否可以长按拖拽该View | false
  boolean canDropOVer| 是否可以在对应点放下|true，默认返回true
  boolean onMergeStart|第一次处于可合并状态|false
  void onMerged|合并结束|false
  ChangeInfo onPrepareMerge|当准备进行合并动画时回调，返回的ChangeInfo用于做当前拖拽的View到目标位置的动画|false
  void onStartMergeAnimation|开始合并动画的回调|false
  void onMergeCancel|当脱离合并状态的回调|false
  boolean onMove|当需要触发移动时的回调|false
  void moved|移动完成的回调|false
  boolean canMergeItem|能否进行合并操作|false
  int onLeaveSubRegion|当从次级目录拖动出item到主层级时回调，返回int 为添加到主层级adapter的位置|false
  float getVelocity|只对低于这个速度的才判断能否移动(需要配合getCurrentState)|true
  int getCurrentState|判断当前处于的状态，返回三个值 Classify.STATE_NONE 无状态，Classify.STATE_MERGE 处于合并状态，Classify.STATE_MOVE 处于移动状态| true
  void onItemClick|当item被点击时的回调|false
  List explodeItem|用于是否展开次级目录，返回一个List 用于初始化次级目录的数据，对于List size 小于2的不展开次级目录而调用onItemClick|false
  
##次级层级的回调
次级层级与主层级相似 没有合并的相关回调 单独有两个回调：

方法|说明
---|---
void initData|用于初始化次级层级数据，初始化的数据来自于主层级的 explodeItem
boolean canDropOver | 对于次层级的item 能否拖动到主层级

#结语
**当前项目效果展现 使用[SimpleAdapter](https://github.com/AlphaBoom/ClassifyView/blob/master/classify/src/main/java/com/anarchy/classify/simple/SimpleAdapter.java)，InsertAbleGridView 是配合SimpleAdapter的控件所写，所以本质是一个有两个RecyclerView的自定义View，支持拖拽item并提供相应回调。其他效果自行书写**



