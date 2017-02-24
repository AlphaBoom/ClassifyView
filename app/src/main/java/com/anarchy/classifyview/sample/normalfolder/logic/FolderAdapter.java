package com.anarchy.classifyview.sample.normalfolder.logic;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.R;
import com.anarchy.classify.simple.PrimitiveSimpleAdapter;
import com.anarchy.classifyview.sample.normalfolder.Constants;
import com.anarchy.classifyview.sample.normalfolder.bean.BaseBean;
import com.anarchy.classifyview.sample.normalfolder.bean.BookBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Date: 16/12/29 11:55
 * Author: lizhiming
 * <p/>
 */
public abstract class FolderAdapter<VH extends FolderAdapter.ViewHolder> extends PrimitiveSimpleAdapter<List<BookBean>, VH> {
    protected List<BaseBean> mData;

    public FolderAdapter(List<BaseBean> data) {
        mData = data;
    }

    @SuppressWarnings("unchecked")
    protected VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item, parent, false);
        return (VH) new ViewHolder(view);
    }


    protected void onBindMainViewHolder(VH holder, int position) {
    }

    protected void onBindSubViewHolder(VH holder, int mainPosition, int subPosition) {
    }

    /**
     * @param parentIndex
     * @param index       if -1  in main region
     */
    protected void onItemClick(View view, int parentIndex, int index) {
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 副层级的数量，用于主层级上的显示效果
     *
     * @param parentPosition
     * @return
     */
    @Override
    protected int getSubItemCount(int parentPosition) {
        return mData.get(parentPosition).getBookList().size();
    }

    @Override
    protected boolean canMergeItem(int selectPosition, int targetPosition) {
        if (selectPosition < 0) {
            return false;
        }
        BaseBean currentSelected = mData.get(selectPosition);
        List<BookBean> books = currentSelected.getBookList();
        return books.size() < 2;
    }

    /**
     * 合并数据处理
     *
     * @param selectedPosition
     * @param targetPosition
     */
    @Override
    protected void onMerged(int selectedPosition, int targetPosition) {
        List<BookBean> tarBookBeans = mData.get(targetPosition).getBookList();
        BookBean bookBean = mData.get(selectedPosition).getBookList().get(0);
        tarBookBeans.add(bookBean);
        mData.get(targetPosition).setBookList(tarBookBeans);
        mData.get(targetPosition).isGroup = true;
        mData.remove(selectedPosition);
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
        if (position < mData.size() && mData.get(position).getBookList().size() > 1) {
            return true;
        }
        if(position < mData.size()&&(mData.get(position).getBookList().size()==1)&& mData.get(position).isGroup){
            return true;
        }
        return false;
    }

    /**
     * 返回副层级的数据源
     *
     * @param parentPosition
     * @return
     */
    @NonNull
    @Override
    protected List<BookBean> getSubSource(int parentPosition) {
        return mData.get(parentPosition).getBookList();
    }

    @Override
    protected void onMove(int selectedPosition, int targetPosition) {
        BaseBean list = mData.remove(selectedPosition);
        mData.add(targetPosition, list);
    }

    /**
     * 副层级数据移动处理
     *
     * @param bookBeen         副层级数据源
     * @param selectedPosition 当前选择的item位置
     * @param targetPosition   要移动到的位置
     */
    @Override
    protected void onSubMove(List<BookBean> bookBeen, int selectedPosition, int targetPosition) {
        bookBeen.add(targetPosition, bookBeen.remove(selectedPosition));
    }

    /**
     * 从副层级移除的元素
     *
     * @param bookBeen         副层级数据源
     * @param selectedPosition 将要冲副层级移除的数据
     * @return 返回的数为添加到主层级的位置
     */
    @Override
    protected int onLeaveSubRegion(int parentPosition,List<BookBean> bookBeen, int selectedPosition) {
        BookBean bookBean = bookBeen.remove(selectedPosition);
        if(bookBeen.size() == 0){
            mData.remove(parentPosition);
        }
        BaseBean baseBean = new BaseBean();
        List<BookBean> bookBeanList = new ArrayList<>();
        bookBeanList.add(bookBean);
        baseBean.setBookList(bookBeanList);
        mData.add(baseBean);
        return mData.size() - 1;
    }


    public static class ViewHolder extends PrimitiveSimpleAdapter.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
