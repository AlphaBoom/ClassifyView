package com.anarchy.classifyview.sample.ireader;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.ClassifyView;
import com.anarchy.classify.simple.PrimitiveSimpleAdapter;
import com.anarchy.classify.simple.widget.InsertAbleGridView;
import com.anarchy.classifyview.R;
import com.anarchy.classifyview.databinding.ItemIReaderFolderBinding;
import com.anarchy.classifyview.sample.ireader.model.IReaderMockData;
import com.anarchy.classifyview.sample.ireader.model.IReaderMockDataGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * Date: 16/12/27 14:44
 * Description:
 */
public class IReaderAdapter extends PrimitiveSimpleAdapter<IReaderMockDataGroup, IReaderAdapter.ViewHolder> {
    private List<IReaderMockData> mMockSource;
    private boolean mMockSourceChanged;
    private List<IReaderMockDataGroup> mLastMockGroup;
    private boolean mEditMode;
    private int[] mDragPosition = new int[2];


    public List<IReaderMockData> getMockSource() {
        return mMockSource;
    }

    public void setMockSource(List<IReaderMockData> mockSource) {
        mMockSource = mockSource;
        notifyDataSetChanged();
    }


    /**
     * 返回当前拖拽的view 在adapter中的位置
     * @return 返回int[0] 主层级位置 如果为 -1 则当前没有拖拽的item
     *            int[1] 副层级位置 如果为 -1 则当前没有拖拽副层级的item
     */
    @NonNull
    public int[] getCurrentDragAdapterPosition(){
        mDragPosition[0] = getMainAdapter().getDragPosition();
        mDragPosition[1] = getSubAdapter().getDragPosition();
        return mDragPosition;
    }

    /**
     *
     * @return 如果当前拖拽的为单个书籍 则返回 其他情况返回null
     */
    @Nullable
    IReaderMockData getCurrentSingleDragData(){
        int[] position = getCurrentDragAdapterPosition();
        if(position[0] == -1) return null;
        if(position[1] == -1){
            IReaderMockData mockData = mMockSource.get(position[0]);
            if(mockData instanceof IReaderMockDataGroup) return null;
            return mockData;
        }else {
            return ((IReaderMockDataGroup)mMockSource.get(position[0])).getChild().get(position[1]);
        }
    }

    /**
     * 添加数据
     * @param mockData
     */
    public void addBook(IReaderMockData mockData){
        if(mMockSource == null) mMockSource = new ArrayList<>();
        mMockSource.add(0,mockData);
        notifyItemInsert(0);
    }

    /**
     * 设置是否在可编辑状态下
     * @param editMode
     */
    public void setEditMode(boolean editMode){
        mEditMode = editMode;

        notifyDataSetChanged();
        getSubAdapter().notifyDataSetChanged();
    }


    public List<IReaderMockDataGroup> getMockGroup(){
        if(mMockSource == null) return null;
        if(mLastMockGroup != null && !mMockSourceChanged){
            return mLastMockGroup;
        }else {
            List<IReaderMockDataGroup> result = new ArrayList<>();
            for(IReaderMockData mockData:mMockSource){
                if(mockData instanceof IReaderMockDataGroup){
                    result.add((IReaderMockDataGroup) mockData);
                }
            }
            mMockSourceChanged = false;
            mLastMockGroup = result;
            return result;
        }
    }


    /**
     * 创建view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_i_reader_folder,parent,false);
        return new ViewHolder(view);
    }

    /**
     * 用于显示{@link InsertAbleGridView} 的item布局
     *
     * @param parent       父View
     * @param convertView  缓存的View 可能为null
     * @param mainPosition 主层级位置
     * @param subPosition  副层级位置
     * @return
     */
    @Override
    public View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition) {
        View result;
        if(convertView != null){
            result = convertView;
        }else {
            result = new View(parent.getContext());
        }
        try {
            int color = ((IReaderMockDataGroup)mMockSource.get(mainPosition)).getChild().get(subPosition).getColor();
            result.setBackgroundColor(color);
        }catch (Exception e){
            //something wrong
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 返回主层级数量
     *
     * @return
     */
    @Override
    protected int getItemCount() {
        return mMockSource == null?0:mMockSource.size();
    }

    /**
     * 副层级的数量，用于主层级上的显示效果
     *
     * @param parentPosition
     * @return
     */
    @Override
    protected int getSubItemCount(int parentPosition) {
        IReaderMockData mockData = mMockSource.get(parentPosition);
        if(mockData instanceof IReaderMockDataGroup){
            IReaderMockDataGroup mockDataGroup = (IReaderMockDataGroup) mockData;
            return mockDataGroup.getChild() == null?0:mockDataGroup.getChild().size();
        }
        return 0;
    }

    /**
     * 返回副层级的数据源
     *
     * @param parentPosition
     * @return
     */
    @Override
    protected IReaderMockDataGroup getSubSource(int parentPosition) {
        IReaderMockData mockData = mMockSource.get(parentPosition);
        if(mockData instanceof IReaderMockDataGroup) return (IReaderMockDataGroup) mockData;
        return null;
    }

    /**
     * 能否弹出次级窗口
     *
     * @param position    主层级点击的位置
     * @param pressedView 点击的view
     * @return
     */
    @Override
    protected boolean canExplodeItem(int position, View pressedView) {
        return mMockSource.get(position) instanceof IReaderMockDataGroup;
    }

    /**
     * 在主层级触发move事件 在这里进行数据改变
     *
     * @param selectedPosition 当前选择的item位置
     * @param targetPosition   要移动到的位置
     */
    @Override
    protected void onMove(int selectedPosition, int targetPosition) {
        mMockSource.add(targetPosition,mMockSource.remove(selectedPosition));
        mMockSourceChanged = true;
    }

    /**
     * 副层级数据移动处理
     *
     * @param iReaderMockDataGroup 副层级数据源
     * @param selectedPosition     当前选择的item位置
     * @param targetPosition       要移动到的位置
     */
    @Override
    protected void onSubMove(IReaderMockDataGroup iReaderMockDataGroup, int selectedPosition, int targetPosition) {
        List<IReaderMockData> child = iReaderMockDataGroup.getChild();
        child.add(targetPosition,child.remove(selectedPosition));
    }


    /**
     * 两个选项能否合并
     *
     * @param selectPosition
     * @param targetPosition
     * @return
     */
    @Override
    protected boolean canMergeItem(int selectPosition, int targetPosition) {
        IReaderMockData select = mMockSource.get(selectPosition);
        return !(select instanceof IReaderMockDataGroup);
    }

    /**
     * 合并数据处理
     *
     * @param selectedPosition
     * @param targetPosition
     */
    @Override
    protected void onMerged(int selectedPosition, int targetPosition) {
        IReaderMockData target = mMockSource.get(targetPosition);
        IReaderMockData select = mMockSource.remove(selectedPosition);
        if(target instanceof IReaderMockDataGroup){
            List<IReaderMockData> child = ((IReaderMockDataGroup) target).getChild();
            child.add(0,select);
        }else {
            //合并成为文件夹状态
            IReaderMockDataGroup group = new IReaderMockDataGroup();
            List<IReaderMockData> child = new ArrayList<>();
            child.add(target);
            child.add(select);
            group.setChild(child);
            group.setCategory(generateNewCategoryTag());
            targetPosition = mMockSource.indexOf(target);
            mMockSource.remove(targetPosition);
            mMockSource.add(targetPosition,group);
        }
        mMockSourceChanged = true;
    }

    /**
     * 生成新的分类标签
     * @return 新的分类标签
     */
    private String generateNewCategoryTag(){
        //生成默认分类标签
        List<IReaderMockDataGroup> mockDataGroups = getMockGroup();
        if(mockDataGroups.size() > 0){
            int serialNumber = 1;
            int[] mHoldNumber = null;
            for(IReaderMockDataGroup temp:mockDataGroups){
                if(temp.getCategory().startsWith("分类")){
                    //可能是自动生成的标签
                    String pendingStr = temp.getCategory().substring(2);
                    if(!TextUtils.isEmpty(pendingStr)&&TextUtils.isDigitsOnly(pendingStr)){
                        //尝试转换为整数
                        try {
                            int serialCategory = Integer.parseInt(pendingStr);
                            if(mHoldNumber == null){
                                mHoldNumber = new int[1];
                                mHoldNumber[0] = serialCategory;
                            }else {
                                mHoldNumber = Arrays.copyOf(mHoldNumber,mHoldNumber.length + 1);
                                mHoldNumber[mHoldNumber.length -1 ] = serialCategory;
                            }
                        }catch (NumberFormatException e){
                            //nope
                        }
                    }
                }
            }
            if(mHoldNumber != null){
                //有自动生成的标签
                Arrays.sort(mHoldNumber);
                for(int serial : mHoldNumber){
                    if(serial < serialNumber) continue;
                    if(serial == serialNumber){
                        //已经被占用 自增1
                        serialNumber ++;
                    }else {
                        break;
                    }
                }
            }
            return "分类" + serialNumber;
        }else {
            return "分类1";
        }
    }

    /**
     * 从副层级移除的元素
     *
     * @param iReaderMockDataGroup 副层级数据源
     * @param selectedPosition     将要冲副层级移除的数据
     * @return 返回的数为添加到主层级的位置
     */
    @Override
    protected int onLeaveSubRegion(int parentPosition,IReaderMockDataGroup iReaderMockDataGroup, int selectedPosition) {
        //从副层级移除并添加到主层级第一个位置上
        IReaderMockData mockData = iReaderMockDataGroup.getChild().remove(selectedPosition);
        mMockSource.add(0,mockData);
        if(iReaderMockDataGroup.getChild().size() == 0){
            int p = mMockSource.indexOf(iReaderMockDataGroup);
            mMockSource.remove(p);
        }
        mMockSourceChanged = true;
        return 0;
    }


    /**
     * 主层级数据绑定
     *
     * @param holder
     * @param position
     */
    @Override
    protected void onBindMainViewHolder(ViewHolder holder, int position) {
        holder.bind(mMockSource.get(position),mEditMode);
    }

    /**
     * 副层级数据绑定
     *
     * @param holder
     * @param mainPosition
     * @param subPosition
     */
    @Override
    protected void onBindSubViewHolder(ViewHolder holder, int mainPosition, int subPosition) {
        holder.bind(((IReaderMockDataGroup)mMockSource.get(mainPosition)).getChild().get(subPosition),mEditMode);
    }


    @Override
    protected void onItemClick(View view, int parentIndex, int index) {
        if(mEditMode){
            if(index == -1){
                mMockSource.get(parentIndex).setChecked(!mMockSource.get(parentIndex).isChecked());
            }
        }
    }

    static class ViewHolder extends PrimitiveSimpleAdapter.ViewHolder{
        private ItemIReaderFolderBinding mBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            mBinding = ItemIReaderFolderBinding.bind(itemView);
        }

        public void bind(IReaderMockData iReaderMockData,boolean inEditMode){
            if(inEditMode){
                if(iReaderMockData instanceof IReaderMockDataGroup){
                    int count = ((IReaderMockDataGroup) iReaderMockData).getCheckedCount();
                    if(count > 0) {
                        mBinding.iReaderFolderCheckBox.setVisibility(View.VISIBLE);
                        mBinding.iReaderFolderCheckBox.setText(count+"");
                        mBinding.iReaderFolderCheckBox.setBackgroundDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_number_bg));
                    }else {
                        mBinding.iReaderFolderCheckBox.setVisibility(View.GONE);
                    }
                }else {
                    Drawable drawable = ContextCompat.getDrawable(itemView.getContext(),iReaderMockData.isChecked()?R.drawable.ic_checked:R.drawable.ic_unchecked);
                    mBinding.iReaderFolderCheckBox.setText("");
                    mBinding.iReaderFolderCheckBox.setVisibility(View.VISIBLE);
                    mBinding.iReaderFolderCheckBox.setBackgroundDrawable(drawable);
                }
            }else {
                mBinding.iReaderFolderCheckBox.setVisibility(View.GONE);
            }
            if(iReaderMockData instanceof IReaderMockDataGroup){
                mBinding.iReaderFolderTag.setVisibility(View.VISIBLE);
                mBinding.iReaderFolderTag.setText(((IReaderMockDataGroup) iReaderMockData).getCategory());
                mBinding.iReaderFolderContent.setVisibility(View.GONE);
            }else {
                mBinding.iReaderFolderTag.setVisibility(View.GONE);
                mBinding.iReaderFolderContent.setBackgroundColor(iReaderMockData.getColor());
                mBinding.iReaderFolderContent.setVisibility(View.VISIBLE);
            }
        }
    }
}
