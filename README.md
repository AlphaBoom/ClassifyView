# ClassifyView [![](https://jitpack.io/v/AlphaBoom/ClassifyView.svg)](https://jitpack.io/#AlphaBoom/ClassifyView)[![Codewake](https://www.codewake.com/badges/ask_question.svg)](https://www.codewake.com/p/classifyview)
实现原理 ClassifyView包裹这一个RecyclerView，当点击这个RecyclerView会弹出一个Dialog 该Dialog的布局会传入另一个RecyclerView.想详细了解，~可以查看 [博客](http://www.jianshu.com/p/a51a93366406)~
# 效果如下
![image](https://github.com/AlphaBoom/ClassifyView/blob/master/screenshot/classifyView.gif)
![image](https://github.com/AlphaBoom/ClassifyView/blob/master/screenshot/ireader.gif)
# 配置依赖
**Step one:** Add the JitPack repository to your build file

```
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	  }
```
**Step two:** Add the dependency

```
    dependencies {
	        compile 'com.github.AlphaBoom:ClassifyView:0.5.2'
	}

```
最新版本查看[Latest release](https://github.com/AlphaBoom/ClassifyView/releases)

# 最近更新
- [x] 关于仿照IReader的效果需要对原库做自定义的部分都已经更新在Sample
- [x] 在adapter中增加了拖拽开始及拖拽开始完成和次级目录弹出的回调
- [x] 增加拖拽item的可在拖拽时放大及可合并时缩小的设置
- [x] 在拖拽开始时添加动画，效果更自然
- [x] 添加了一个自定义的例子，效果大致仿IReader的书架

# 快速使用
1. 继承SimpleAdapter

```java
   public class MyAdapter extends SimpleAdapter<Bean, MyAdapter.ViewHolder> {


    public MyAdapter(List<List<Bean>> mData) {
        super(mData);
    }


    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new MyAdapter.ViewHolder(view);
    }
    //convertView是缓存的View  如何使用这个convertView 参考ListView的使用步骤
    @Override
    public View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition) {
    //返回的View作为每一个Item的布局
    /*布局内容自定义 例子中如下：
    <View xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:background="@drawable/round_shape"/>
    */
       if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner, parent, false);
        }
        return convertView;
    }

    @Override
    protected void onItemClick(View view, int parentIndex, int index) {
        Toast.makeText(view.getContext(),"parentIndex: "+parentIndex+"\nindex: "+index,Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends SimpleAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
``` 
2.找到ClassifyView 并设置Adapter

```java
mClassifyView = (ClassifyView) view.findViewById(R.id.classify_view);
List<List<Bean>> data = new ArrayList<>();
for(int i=0;i<30;i++){
    List<Bean> inner = new ArrayList<>();
        if(i>10) {
            int c = (int) (Math.random() * 15+1);
            for(int j=0;j<c;j++){
                inner.add(new Bean());
            }
        }else {
            inner.add(new Bean());
        }
        data.add(inner);
    }
mClassifyView.setAdapter(new MyAdapter(data));
```
# 添加拖动状态监听

设置监听
```java
//添加监听
ClassifyView#addDragListener(DragListener)
//移除监听    
ClassifyView#removeDragListener(DragListener)
//移除所有监听
ClassifyView#removeAllDragListener()
/**
 * 是否监听移动状态信息
 * @param enable false disable true enable default true
 */
ClassifyView#enableMoveListener(boolean)   
```
具体监听回调

```java
public interface DragListener {
        /**
         * 开始拖拽
         *
         * @param parent parent is ClassifyView
         * @param startX start touch x relative classify view
         * @param startY start touch y relative classify view
         * @param region start drag  region either  main or sub
         */
        void onDragStart(ViewGroup parent,View selectedView, float startX, float startY,@Region int region);

        /**
         * star drag animation end
         * @param parent
         * @param selectedView
         * @param region
         */
        void onDragStartAnimationEnd(ViewGroup parent,View selectedView,int region);

        /**
         * 拖拽结束(recover animation end)
         */
        void onDragEnd(ViewGroup parent,@Region int region);

        /**
         * 释放被拖拽的View
         */
        void onDragRelease(ViewGroup parent, float releaseX, float releaseY,@Region int region);

        /**
         * move callback by touch location
         *
         * @param touchX 触摸的X坐标
         * @param touchY 触摸的Y坐标
         */
        void onMove(ViewGroup parent, float touchX, float touchY,@Region int region);
    }
```

# 支持的自定义的属性
ClassifyView attr

属性  | 说明
------------- | -------------
MainSpanCount  | 主层级目录的列数
SubSpanCount  | 次级层级目录的列数
AnimationDuration | 合并动画的时间
SubRatio | 次级目录的高度占主层级的高度比例
EdgeWidth | 设置宽度用于当item到边缘时判断是否触发滑动
MainPadding | 设置主层级容器的padding值
MainPaddingLeft | 
MainPaddingTop |
MainPaddingRight |
MainPaddingBottom |
SubPadding | 设置次级层级容器的padding值
SubPaddingLeft |
SubPaddingTop |
SubPaddingRight |
SubPaddingBottom |
DragScaleX | 当item处于被拖拽状态时X轴方向缩放比例
DragScaleY | 当item处于被拖拽状态时Y轴方向缩放比例
DragInMergeScaleX | 当被拖拽的item处于可合并状态时X轴方向缩放比例
DragInMergeScaleY | 当被拖拽的item处于可合并状态时Y轴方向缩放比例

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

# 高级自定义

## 如果不喜欢`List<List<>>`的结构可以集成PrimitiveSimpleAdapter来实现其他数据源的adapter

关于如何继承PrimitiveSimpleAdapter可以参考[IReaderAdapter](https://github.com/AlphaBoom/ClassifyView/blob/master/app/src/main/java/com/anarchy/classifyview/sample/ireader/IReaderAdapter.java),如果不能满足可以考虑分别继承[BaseMainAdapter](https://github.com/AlphaBoom/ClassifyView/blob/master/classify/src/main/java/com/anarchy/classify/adapter/BaseMainAdapter.java)及[BaseSubAdapter](https://github.com/AlphaBoom/ClassifyView/blob/master/classify/src/main/java/com/anarchy/classify/adapter/BaseSubAdapter.java)

## 继承ClassifyView 重写以下方法：

1. **RecyclerView getMain(Context context, AttributeSet parentAttrs)** <br/>返回主层级使用的 RecyclerView。
2. **RecyclerView getSub(Context context, AttributeSet parentAttrs)** 返回次级层级使用的RecyclerView
3. **View chooseTarget(View selected, List<View> swapTargets, int curX, int curY)** <br/> 当拖拽的View 覆盖到子View时会通过该方法在候选View中选择一个View 为目标View 之后的交互操作都会作用于当前所选择的View 及 这个目标View<br/><font color="green">@param selected</font> 当前选择的View<br/><font color="green">@param swapTargets</font> 候选的目标View(候选的目标View 为当前选择的View 能够覆盖到所有View)<br/><font color="green">@param curX</font> 当前选中View的X轴坐标<br/><font color="green">@param curY</font> 当前选中View的Y轴坐标
4. **Drawable getDragDrawable(View view)** <br/>返回用于渲染当前拖动View的显示<br/><font color="green">@param view</font> 当前选中的View<br/><font color="green">@return drawable</font>返回Drawable 用于设置拖拽View的背景
5. 自定义次级目录的布局：
   * 自定义次级目录的Dialog：重写 **Dialog createSubDialog()**
   * 自定义次级目录布局：重写 **View getSubContent()**
     <br/> **注意：** 默认会在返回的View中查找有Tag 为 @String/sub_container 的View作为容器 如果没有 就已返回的View作为容器来添加次级目录的RecyclerView。 可以覆盖 **ViewGroup findHaveSubTagContainer(ViewGroup group)** 来重写查找容器的逻辑

**设置数据方式有两种方式：**

1. 使用 *ClassifyView.setAdapter(BaseMainAdapter mainAdapter, BaseSubAdapter subAdapter)* 用于分别设置主层级及次级层级的适配器（使用这种方式只能获取到相应状态时的回调，注意这些回调返回值的处理）
2. 使用 *setAdapter(BaseSimpleAdapter baseSimpleAdapter)* 设置一个混合了主层级及次级层级的适配器，如何自定义可以参考 [SimpleAdapter](https://github.com/AlphaBoom/ClassifyView/blob/master/classify/src/main/java/com/anarchy/classify/simple/SimpleAdapter.java)


## 主层级提供的回调
在BaseAdapter中对于mergeStart等又增加了ViewHolder形式的回调 本质是一样的。

回调方法 | 说明 | 是否有默认实现在BaseSubAdapter中
------ | ----- | ----
  setDragPosition | 设置当前被拖拽的位置 | true，默认效果为隐藏被拖拽的位置
  boolean canDragOnLongPress|是否可以长按拖拽该View | false
  boolean canDropOVer| 是否可以在对应点放下|true，默认返回true
  boolean onMergeStart|第一次处于可合并状态|false
  void onMerged|合并结束|false
  MergeInfo onPrepareMerge|当准备进行合并动画时回调，返回的MergeInfo用于做当前拖拽的View到目标位置的动画|false
  void onStartMergeAnimation|开始合并动画的回调|false
  void onMergeCancel|当脱离合并状态的回调|false
  boolean onMove|当需要触发移动时的回调|false
  void moved|移动完成的回调|false
  boolean canMergeItem|能否进行合并操作|false
  int onLeaveSubRegion|当从次级目录拖动出item到主层级时回调，返回int 为添加到主层级adapter的位置|false
  float getVelocity|只对低于这个速度的才判断能否移动(需要配合getCurrentState)|true
  int getCurrentState|判断当前处于的状态，返回三个值 Classify.STATE_NONE 无状态，Classify.STATE_MERGE 处于合并状态，Classify.STATE_MOVE 处于移动状态| true
  void onItemClick|当item被点击时的回调|false
  boolean canExplodeItem|用于判断当点击一个item时是否展开次级目录|false
  
## 次级层级的回调
次级层级与主层级相似 没有合并的相关回调：

方法|说明
---|---
void prepareExplodeItem|用于准备初始化次级层级数据
boolean canDropOver | 对于次层级的item 能否拖动到主层级
boolean canDragOut | 是否可以移出次级目录到主层级
void onDialogShow | 次级窗口显示时的回调
void onDialogCancel | 次级窗口隐藏时的回调

# 结语
**当前项目效果展现 使用[SimpleAdapter](https://github.com/AlphaBoom/ClassifyView/blob/master/classify/src/main/java/com/anarchy/classify/simple/SimpleAdapter.java)**


