package com.anarchy.classify.simple;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.MergeInfo;
import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;
import com.anarchy.classify.simple.widget.CanMergeView;
import com.anarchy.classify.util.L;

/**
 * 一种常用的方式，主层级与负层级使用相同的布局元素创建
 * 对于副层级定为List形式。在内部处理好移动的数据迁移
 */
public abstract class PrimitiveSimpleAdapter<Sub, VH extends PrimitiveSimpleAdapter.ViewHolder> implements BaseSimpleAdapter {
    private static final int MODE_SHIFT = 30;
    public static final int TYPE_MASK = 0x3 << MODE_SHIFT;
    public static final int TYPE_UNDEFINED = 0;
    public static final int TYPE_MAIN = 1 << MODE_SHIFT;
    public static final int TYPE_SUB = 2 << MODE_SHIFT;
    private SimpleMainAdapter mSimpleMainAdapter;
    private SimpleSubAdapter mSimpleSubAdapter;
    private SimpleHook<Sub> mSimpleHook;

    public PrimitiveSimpleAdapter() {
        mSimpleMainAdapter = new SimpleMainAdapter();
        mSimpleSubAdapter = new SimpleSubAdapter();
    }

    @Override
    public BaseMainAdapter getMainAdapter() {
        return mSimpleMainAdapter;
    }

    @Override
    public BaseSubAdapter getSubAdapter() {
        return mSimpleSubAdapter;
    }

    /**
     * 如果设置了该项可以对移动合并时的界面变化也可进行自定义的操作
     * @param simpleHook
     */
    public void setSimpleHook(SimpleHook<Sub> simpleHook) {
        mSimpleHook = simpleHook;
    }

    @Override
    public boolean isShareViewPool() {
        return true;
    }

    public void notifyItemInsert(int position) {
        mSimpleMainAdapter.notifyItemInserted(position);
    }

    /**
     * 通知数据变化
     *
     * @param position
     */
    public void notifyItemChanged(int position) {
        mSimpleMainAdapter.notifyItemChanged(position);
    }

    /**
     * 通知数据变化
     *
     * @param position
     * @param count
     */
    public void notifyItemRangeChanged(int position, int count) {
        mSimpleMainAdapter.notifyItemRangeChanged(position, count);
    }

    /**
     * 通知添加数据
     *
     * @param position
     * @param count
     */
    public void notifyItemRangeInsert(int position, int count) {
        mSimpleMainAdapter.notifyItemRangeInserted(position, count);
    }

    /**
     * 通知触发数据变动
     */
    public void notifyDataSetChanged() {
        mSimpleMainAdapter.notifyDataSetChanged();
    }


    /**
     * 创建view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract VH onCreateViewHolder(ViewGroup parent, int viewType);


    /**
     * 用于显示{@link com.anarchy.classify.simple.widget.InsertAbleGridView} 的item布局
     *
     * @param parent       父View
     * @param convertView  缓存的View 可能为null
     * @param mainPosition 主层级位置
     * @param subPosition  副层级位置
     * @return
     */
    public abstract View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition);

    /**
     * 返回主层级数量
     *
     * @return
     */
    protected abstract int getItemCount();

    /**
     * 副层级的数量，用于主层级上的显示效果
     *
     * @return
     */
    protected abstract int getSubItemCount(int parentPosition);

    /**
     * 返回副层级的数据源
     *
     * @param parentPosition
     * @return
     */
    protected abstract Sub getSubSource(int parentPosition);

    /**
     * 能否弹出次级窗口
     *
     * @param position    主层级点击的位置
     * @param pressedView 点击的view
     * @return
     */
    protected abstract boolean canExplodeItem(int position, View pressedView);


    /**
     * 在主层级触发move事件 在这里进行数据改变
     *
     * @param selectedPosition 当前选择的item位置
     * @param targetPosition   要移动到的位置
     */
    protected abstract void onMove(int selectedPosition, int targetPosition);



    /**
     * 副层级数据移动处理
     *
     * @param sub              副层级数据源
     * @param selectedPosition 当前选择的item位置
     * @param targetPosition   要移动到的位置
     */
    protected abstract void onSubMove(Sub sub, int selectedPosition, int targetPosition);

    /**
     * 两个选项能否合并
     *
     * @param selectPosition
     * @param targetPosition
     * @return
     */
    protected abstract boolean canMergeItem(int selectPosition, int targetPosition);

    /**
     * 合并数据处理
     *
     * @param selectedPosition
     * @param targetPosition
     */
    protected abstract void onMerged(int selectedPosition, int targetPosition);

    /**
     * 从副层级移除的元素
     *
     * @param sub              副层级数据源
     * @param selectedPosition 将要冲副层级移除的数据
     * @return 返回的数为添加到主层级的位置
     */
    protected abstract int onLeaveSubRegion(int parentPosition, Sub sub, int selectedPosition);

    /**
     * 主层级数据绑定
     *
     * @param holder
     * @param position
     */
    protected abstract void onBindMainViewHolder(VH holder, int position);

    /**
     * 副层级数据绑定
     *
     * @param holder
     * @param mainPosition
     * @param subPosition
     */
    protected abstract void onBindSubViewHolder(VH holder, int mainPosition, int subPosition);

    /**
     * 获取主副层级标记
     *
     * @param type
     * @return {@link #TYPE_UNDEFINED,#TYPE_MAIN,#TYPE_SUB}
     */
    public static int getSpecialType(int type) {
        return type & TYPE_MASK;
    }

    /**
     * 获取原始的view type
     *
     * @param type
     * @return
     */
    public static int getOriginTyoe(int type) {
        return type & (~TYPE_MASK);
    }

    /**
     * 默认返回false 不在view type上添加主副层级标记
     * 如果返回true 会在原有view type 上添加主副层级标记
     *
     * @return
     */
    protected boolean haveSpecialType() {
        return false;
    }

    /**
     * @param parentIndex
     * @param index       if -1  in main region
     */
    protected void onItemClick(VH viewHolder, int parentIndex, int index) {
        onItemClick(viewHolder.itemView,parentIndex,index);
    }

    /**
     * 当拖拽开始发生时的回调
     * @param viewHolder
     * @param parentIndex
     * @param index  如果为-1 则为主层级上的拖拽
     */
    protected void onDragStart(VH viewHolder,int parentIndex,int index){

    }

    /**
     * 当开始拖拽动画结束时的回调
     * @param viewHolder
     * @param parentIndex
     * @param index 如果为-1 则为主层级上的是拖拽
     */
    protected void onDragAnimationEnd(VH viewHolder,int parentIndex,int index){

    }

    /**
     * 当副层级弹窗显示时的回调
     * @param dialog
     * @param parentPosition
     */
    protected void onSubDialogShow(Dialog dialog,int parentPosition){

    }


    protected void onSubDialogCancel(Dialog dialog,int parentPosition){

    }
    /**
     * @deprecated {@link #onItemClick(ViewHolder, int, int)}
     */
    protected void onItemClick(View pressedView,int parentIndex,int index){

    }

    /**
     * 返回ItemType
     *
     * @param parentPosition
     * @param subPosition
     * @return
     */
    protected int getItemType(int parentPosition, int subPosition) {
        return 0;
    }

    /**
     * 能否支持长按拖拽
     *
     * @param mainPosition
     * @param subPosition
     * @return
     */
    protected boolean canDragOnLongPress(int mainPosition, int subPosition) {
        return true;
    }

    /**
     * 简单的实现主层级的Adapter
     */
    private class SimpleMainAdapter extends BaseMainAdapter<VH, SimpleSubAdapter> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = PrimitiveSimpleAdapter.this.onCreateViewHolder(parent, viewType);
            CanMergeView canMergeView = vh.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.setAdapter(PrimitiveSimpleAdapter.this);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            CanMergeView canMergeView = holder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.initOrUpdateMain(position, PrimitiveSimpleAdapter.this.getSubItemCount(position));
            }
            PrimitiveSimpleAdapter.this.onBindMainViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return PrimitiveSimpleAdapter.this.getItemCount();
        }


        @Override
        public int getItemViewType(int position) {
            int originType = PrimitiveSimpleAdapter.this.getItemType(position, -1);
            return PrimitiveSimpleAdapter.this.haveSpecialType() ? TYPE_MAIN | (originType & (~TYPE_MASK)) : originType;
        }


        @Override
        public void onDragStart(VH selectedViewHolder, int selectedPosition) {
           PrimitiveSimpleAdapter.this.onDragStart(selectedViewHolder,selectedPosition,-1);
        }

        @Override
        public void onDragAnimationEnd(VH selectedViewHolder, int selectedPosition) {
            PrimitiveSimpleAdapter.this.onDragAnimationEnd(selectedViewHolder,selectedPosition,-1);
        }

        @Override
        public boolean onMove(int selectedPosition, int targetPosition) {
            if(mSimpleHook != null){
                return mSimpleHook.onMove(this,selectedPosition,targetPosition);
            }
            notifyItemMoved(selectedPosition, targetPosition);
            PrimitiveSimpleAdapter.this.onMove(selectedPosition, targetPosition);
            return true;
        }

        @Override
        public boolean canMergeItem(int selectedPosition, int targetPosition) {
            return PrimitiveSimpleAdapter.this.canMergeItem(selectedPosition, targetPosition);
        }

        /**
         * 当从副层级离开时必须要删除掉副层级中的数据，只针对这种情况
         * @param selectedPosition
         * @param simpleSubAdapter
         * @return
         */
        @Override
        public int onLeaveSubRegion(int selectedPosition, SimpleSubAdapter simpleSubAdapter) {
            if(mSimpleHook != null){
                return mSimpleHook.onLeaveSubRegion(this,simpleSubAdapter.getParentPosition(),simpleSubAdapter.getData(),selectedPosition);
            }
            int originSize = simpleSubAdapter.getItemCount();//副层级数据变动之前的大小
            int parentTargetPosition = PrimitiveSimpleAdapter.this.onLeaveSubRegion(simpleSubAdapter.getParentPosition(), simpleSubAdapter.getData(), selectedPosition);
            if (simpleSubAdapter.getParentPosition() != -1) {
                if (parentTargetPosition >= 0 && parentTargetPosition < getItemCount())
                    notifyItemInserted(parentTargetPosition);
                int offset = parentTargetPosition <= simpleSubAdapter.getParentPosition()?1:0;
                int newParentPosition = simpleSubAdapter.getParentPosition() + offset;
                if(originSize <= 1){
                    notifyItemRemoved(newParentPosition);
                }else {
                    notifyItemChanged(newParentPosition);
                }
            }
            return parentTargetPosition;
        }


        @Override
        public boolean canExplodeItem(int position, View pressedView) {
            return PrimitiveSimpleAdapter.this.canExplodeItem(position, pressedView);
        }

        @Override
        public boolean canDragOnLongPress(int position, View pressedView) {
            return PrimitiveSimpleAdapter.this.canDragOnLongPress(position, -1);
        }
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(RecyclerView recyclerView, int position, View pressedView) {
            VH viewHolder = (VH) recyclerView.findViewHolderForAdapterPosition(position);
            if(viewHolder != null)
                PrimitiveSimpleAdapter.this.onItemClick(viewHolder,position,-1);
        }

        @Override
        public boolean onMergeStart(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.onMergeStart();
            }
            return true;
        }

        @Override
        public void onMergeCancel(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.onMergeCancel();
            }
        }

        @Override
        public void onMerged(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.onMerged();
            }
            if(mSimpleHook != null){
                mSimpleHook.onMerged(this,selectedPosition,targetPosition);
                return;
            }
            PrimitiveSimpleAdapter.this.onMerged(selectedPosition, targetPosition);
            notifyItemRemoved(selectedPosition);
            if (selectedPosition < targetPosition) {
                notifyItemChanged(targetPosition - 1);
            } else {
                notifyItemChanged(targetPosition);
            }
        }

        @Override
        public MergeInfo onPrePareMerge(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition) {
            if (selectedViewHolder == null || targetViewHolder == null) return null;
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                ChangeInfo info = canMergeView.prepareMerge();
                float scaleX = info.targetWidth/info.sourceWidth;
                float scaleY = info.targetHeight/info.sourceHeight;
                float targetX = targetViewHolder.itemView.getLeft() + info.targetLeft - info.sourceLeft*scaleX;
                float targetY = targetViewHolder.itemView.getTop() + info.targetTop - info.sourceTop*scaleY;
                return new MergeInfo(scaleX, scaleY, targetX, targetY);
            }
            return null;
        }

        @Override
        public void onStartMergeAnimation(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition, int duration) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.startMergeAnimation(duration);
            }
        }
    }

    /**
     * 简单实现副层级的Adapter
     */
    private class SimpleSubAdapter extends BaseSubAdapter<VH> {
        private int mParentPosition = -1;
        private Sub mData;

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = PrimitiveSimpleAdapter.this.onCreateViewHolder(parent, viewType);
            CanMergeView canMergeView = vh.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.setAdapter(PrimitiveSimpleAdapter.this);
            }
            return vh;
        }


        @Override
        public void onBindViewHolder(VH holder, int position) {
            CanMergeView canMergeView = holder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.initOrUpdateSub(mParentPosition, position);
            }
            PrimitiveSimpleAdapter.this.onBindSubViewHolder(holder, mParentPosition, position);
        }

        @Override
        public void onDragStart(VH selectedViewHolder, int selectedPosition) {
            PrimitiveSimpleAdapter.this.onDragStart(selectedViewHolder, mParentPosition, selectedPosition);
        }

        @Override
        public void onDragAnimationEnd(VH selectedViewHolder, int selectedPosition) {
            PrimitiveSimpleAdapter.this.onDragAnimationEnd(selectedViewHolder, mParentPosition, selectedPosition);
        }

        @Override
        public void onDialogShow(Dialog subDialog, int parentPosition) {
            PrimitiveSimpleAdapter.this.onSubDialogShow(subDialog, parentPosition);
        }

        @Override
        public void onDialogCancel(Dialog subDialog, int parentPosition) {
            PrimitiveSimpleAdapter.this.onSubDialogCancel(subDialog, parentPosition);
        }

        @Override
        public int getItemCount() {
            return getSubItemCount(mParentPosition);
        }


        @Override
        public void prepareExplodeItem(int parentPosition) {
            mParentPosition = parentPosition;
            mData = PrimitiveSimpleAdapter.this.getSubSource(parentPosition);
            notifyDataSetChanged();
        }

        public Sub getData() {
            return mData;
        }


        public int getParentPosition() {
            return mParentPosition;
        }

        @Override
        public boolean onMove(int selectedPosition, int targetPosition) {
            if(mSimpleHook != null){
                return mSimpleHook.onSubMove(this,mData,selectedPosition,targetPosition);
            }
            notifyItemMoved(selectedPosition, targetPosition);
            PrimitiveSimpleAdapter.this.onSubMove(mData, selectedPosition, targetPosition);
            if (mParentPosition != -1) {
                mSimpleMainAdapter.notifyItemChanged(mParentPosition);
            }
            return true;
        }


        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(RecyclerView recyclerView, int position, View pressedView) {
            VH viewHolder = (VH) recyclerView.findViewHolderForAdapterPosition(position);
            if(viewHolder != null)
                PrimitiveSimpleAdapter.this.onItemClick(viewHolder,mParentPosition,position);
        }

        @Override
        public int getItemViewType(int position) {
            int originType = PrimitiveSimpleAdapter.this.getItemType(position, -1);
            return PrimitiveSimpleAdapter.this.haveSpecialType() ? TYPE_SUB | (originType & (~TYPE_MASK)) : originType;
        }
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected CanMergeView mCanMergeView;
        private int paddingLeft;
        private int paddingRight;
        private int paddingTop;
        private int paddingBottom;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof CanMergeView) {
                mCanMergeView = (CanMergeView) itemView;
            } else if (itemView instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) itemView;
                paddingLeft = group.getPaddingLeft();
                paddingRight = group.getPaddingRight();
                paddingTop = group.getPaddingTop();
                paddingBottom = group.getPaddingBottom();
                //只遍历一层 寻找第一个符合条件的view
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    if (child instanceof CanMergeView) {
                        mCanMergeView = (CanMergeView) child;
                        break;
                    }
                }
            }
        }

        public CanMergeView getCanMergeView() {
            return mCanMergeView;
        }

        public int getPaddingLeft() {
            return paddingLeft;
        }

        public int getPaddingRight() {
            return paddingRight;
        }

        public int getPaddingTop() {
            return paddingTop;
        }

        public int getPaddingBottom() {
            return paddingBottom;
        }
    }

    /**
     * {@link PrimitiveSimpleAdapter}默认的移动等回调只支持数据处理，如果
     * @param <Sub>
     */
    public interface SimpleHook<Sub>{

        /**
         * 在主层级触发move事件 在这里进行数据改变
         *
         * @param selectedPosition 当前选择的item位置
         * @param targetPosition   要移动到的位置
         */
        boolean onMove(BaseMainAdapter mainAdapter ,int selectedPosition, int targetPosition);



        /**
         * 副层级数据移动处理
         *
         * @param sub              副层级数据源
         * @param selectedPosition 当前选择的item位置
         * @param targetPosition   要移动到的位置
         */
        boolean onSubMove(BaseSubAdapter subAdapter,Sub sub, int selectedPosition, int targetPosition);
        /**
         * 合并数据处理
         *
         * @param selectedPosition
         * @param targetPosition
         */
        void onMerged(BaseMainAdapter mainAdapter,int selectedPosition, int targetPosition);

        /**
         * 从副层级移除的元素
         *
         * @param sub              副层级数据源
         * @param selectedPosition 将要冲副层级移除的数据
         * @return 返回的数为添加到主层级的位置
         */
        int onLeaveSubRegion(BaseMainAdapter mainAdapter,int parentPosition, Sub sub, int selectedPosition);

    }

}
