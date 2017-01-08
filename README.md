# ClassifyView [![](https://jitpack.io/v/AlphaBoom/ClassifyView.svg)](https://jitpack.io/#AlphaBoom/ClassifyView)[![Codewake](https://www.codewake.com/badges/ask_question.svg)](https://www.codewake.com/p/classifyview)
实现原理 ClassifyView包裹这一个RecyclerView，当点击这个RecyclerView会弹出一个Dialog 该Dialog的布局会传入另一个RecyclerView.想详细了解，可以查看[博客](http://www.jianshu.com/p/a51a93366406)
#效果如下
![image](https://github.com/AlphaBoom/ClassifyView/blob/master/screenshot/classifyView.gif)
![image](https://github.com/AlphaBoom/ClassifyView/blob/master/screenshot/ireader.gif)
#配置依赖
**Step one:**Add the JitPack repository to your build file

```
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	  }
```
**Step two:**Add the dependency

```
    dependencies {
	        compile 'com.github.AlphaBoom:ClassifyView:0.5.0'
	}

```
最新版本查看[Latest release](https://github.com/AlphaBoom/ClassifyView/releases)

#最近更新

- [ ]  继续增加可自定义模块，和效果。现在一些效果还是需要修改源码部分
- [x] 在拖拽开始时添加动画，效果更自然
- [x] 添加了一个自定义的例子，效果大致仿IReader的书架

#快速使用
1. 继承SimpleAdapter

```
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

```
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
#添加拖动状态监听

设置监听
```
//添加监听
public void addDragListener(DragListener listener){
        mDragListeners.add(listener);
    }
//移除监听    
public void removeDragListener(DragListener listener){
        mDragListeners.remove(listener);
    }
//移除所有监听
public void removeAllDragListener(){
        mDragListeners.clear();
    }
/**
 * 是否监听移动状态信息
 * @param enable false disable true enable default true
 */
public void enableMoveListener(boolean enable){
    mMoveListenerEnable = enable;
 }    
```
具体监听回调

```
public interface DragListener{
        /**
         * 开始拖拽
         * @param parent parent is ClassifyView
         * @param startX start touch x 相对于 classify view
         * @param startY start touch y 相对于 classify view
         * @param region 拖动的区域   IN_MAIN_REGION  或 IN_SUB_REGION 
         */
        void onDragStart(ViewGroup parent,float startX,float startY,int region);

        /**
         * 拖拽结束(recover animation end)
         */
        void onDragEnd(ViewGroup parent,int region);

        /**
         * 释放被拖拽的View
         */
        void onDragRelease(ViewGroup parent,float releaseX,float releaseY,int region);

        /**
         * 拖动移动时的回调 
         * @param touchX 触摸的X坐标
         * @param touchY 触摸的Y坐标
         */
        void onMove(ViewGroup parent,float touchX,float touchY,int region);
    }
```

#支持的自定义的属性
ClassifyView attr

属性  | 说明
------------- | -------------
MainSpanCount  | 主层级目录的列数
SubSpanCount  | 次级层级目录的列数
AnimationDuration | 合并动画的时间
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

1. **RecyclerView getMain(Context context, AttributeSet parentAttrs)**<br/>返回主层级使用的 RecyclerView。
2. **RecyclerView getSub(Context context, AttributeSet parentAttrs)**返回次级层级使用的RecyclerView
3. **View chooseTarget(View selected, List<View> swapTargets, int curX, int curY)**<br/> 当拖拽的View 覆盖到子View时会通过该方法在候选View中选择一个View 为目标View 之后的交互操作都会作用于当前所选择的View 及 这个目标View<br/><font color="green">@param selected</font> 当前选择的View<br/><font color="green">@param swapTargets</font> 候选的目标View(候选的目标View 为当前选择的View 能够覆盖到所有View)<br/><font color="green">@param curX</font> 当前选中View的X轴坐标<br/><font color="green">@param curY</font> 当前选中View的Y轴坐标
4. **Drawable getDragDrawable(View view)**<br/>返回用于渲染当前拖动View的显示<br/><font color="green">@param view</font> 当前选中的View<br/><font color="green">@return drawable</font>返回Drawable 用于设置拖拽View的背景
5. 自定义次级目录的布局：
   * 自定义次级目录的Dialog：重写 **Dialog createSubDialog()**
   * 自定义次级目录布局：重写**View getSubContent()**
     <br/>**注意：**默认会在返回的View中查找有Tag 为 @String/sub_container 的View作为容器 如果没有 就已返回的View作为容器来添加次级目录的RecyclerView。 可以覆盖**ViewGroup findHaveSubTagContainer(ViewGroup group)**来重写查找容器的逻辑

**设置数据方式有两种方式：**

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
**当前项目效果展现 使用[SimpleAdapter](https://github.com/AlphaBoom/ClassifyView/blob/master/classify/src/main/java/com/anarchy/classify/simple/SimpleAdapter.java)，InsertAbleGridView 是配合SimpleAdapter的控件所写，所以本质是一个有两个RecyclerView的自定义View，支持拖拽item并提供相应回调。**

#License
Copyright 2016 AlphaBoom

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

