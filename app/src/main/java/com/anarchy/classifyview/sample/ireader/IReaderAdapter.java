package com.anarchy.classifyview.sample.ireader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Observable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;
import com.anarchy.classify.simple.PrimitiveSimpleAdapter;
import com.anarchy.classify.simple.widget.InsertAbleGridView;
import com.anarchy.classifyview.R;
import com.anarchy.classifyview.databinding.ItemIReaderFolderBinding;
import com.anarchy.classifyview.sample.ireader.model.IReaderMockData;
import com.anarchy.classifyview.sample.ireader.model.IReaderMockDataGroup;
import com.anarchy.classifyview.sample.normalfolder.Constants;

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
    private final static String IReaderAdapterLog="IReaderAdapterLog";
    private List<IReaderMockData> mMockSource;
    private boolean mMockSourceChanged;
    private List<IReaderMockDataGroup> mLastMockGroup;
    private List<IReaderMockData> mCheckedData = new ArrayList<>();
    private boolean mEditMode;
    private boolean mSubEditMode;
    private int[] mDragPosition = new int[2];
    private IReaderObservable mObservable = new IReaderObservable();
    private SubObserver mSubObserver = new SubObserver(mObservable);
    private DialogInterface.OnDismissListener mDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if(mObservable.isRegister(mSubObserver)) mObservable.unregisterObserver(mSubObserver);
            mSubEditMode = false;
        }
    };


    public void registerObserver(IReaderObserver observer) {
        mObservable.registerObserver(observer);
    }


    public List<IReaderMockData> getMockSource() {
        return mMockSource;
    }

    public void setMockSource(List<IReaderMockData> mockSource) {
        mMockSource = mockSource;
        notifyDataSetChanged();
    }


    @Override
    protected void onDragStart(ViewHolder viewHolder, int parentIndex, int index) {
        if (!mEditMode) {
            //如果当前不为可编辑状态
            IReaderMockData mockData = index == -1 ? mMockSource.get(parentIndex) : ((IReaderMockDataGroup) mMockSource.get(parentIndex)).getChild(index);
            if (mockData != null) {
                mockData.setChecked(true);
                mCheckedData.add(mockData);
                mObservable.notifyItemCheckChanged(true);
                viewHolder.getBinding().iReaderFolderCheckBox.setVisibility(View.VISIBLE);
                viewHolder.getBinding().iReaderFolderCheckBox.setBackgroundResource(R.drawable.ic_checked);
            }
        }
    }

    @Override
    protected void onDragAnimationEnd(ViewHolder viewHolder, int parentIndex, int index) {
        if (!mEditMode) {
            setEditMode(true);
        }
    }

    @Override
    protected void onSubDialogShow(Dialog dialog, int parentPosition) {
        dialog.setOnDismissListener(mDismissListener);
        //当次级窗口显示时需要修改标题
        final ViewGroup contentView = (ViewGroup) dialog.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        final TextView selectAll = (TextView) contentView.findViewById(R.id.text_select_all);
        TextView title = (TextView) contentView.findViewById(R.id.text_title);
        final EditText editText = (EditText) contentView.findViewById(R.id.edit_title);
        FrameLayout subContainer = (FrameLayout) contentView.findViewById(R.id.sub_container);
        final IReaderMockDataGroup mockDataGroup = (IReaderMockDataGroup) mMockSource.get(parentPosition);
        mSubObserver.setBindResource(mockDataGroup, selectAll, getMainAdapter(),getSubAdapter(),parentPosition);
        if(!mObservable.isRegister(mSubObserver)) mObservable.registerObserver(mSubObserver);
        selectAll.setVisibility(mEditMode ? mSubEditMode ? View.GONE : View.VISIBLE : View.GONE);
        title.setText(String.valueOf(mockDataGroup.getCategory()));
        /*if(Build.VERSION.SDK_INT >= 19) {
            title.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    mSubEditMode = true;
                    selectAll.setVisibility(View.GONE);
                    editText.setText(String.valueOf(mockDataGroup.getCategory()));
                    editText.setSelection(0,editText.getText().toString().length());
                    int originWidth = editText.getWidth();
                    editText.setWidth(0);
                    TransitionManager.beginDelayedTransition(contentView);
                    editText.setWidth(originWidth);
                }
            });
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId){
                        case KeyEvent.KEYCODE_ENTER:
                            break;
                    }
                    return false;
                }
            });
        }*/

    }

    /**
     * 判断前后文件夹名是否一致：
     * 不一致，刷新Adapter，modify DB
     * 一致：hidden SubDialog
     * @param dialog
     * @param parentPosition
     */
    @Override
    protected void onSubDialogCancel(Dialog dialog, int parentPosition) {
        Log.i(IReaderAdapterLog,"onSubDialogCancel");
        super.onSubDialogCancel(dialog, parentPosition);
    }

    static class SubObserver extends IReaderObserver {
        final IReaderObservable mObservable;
        IReaderMockDataGroup mGroup;
        TextView selectAll;
        BaseSubAdapter mSubAdapter;
        BaseMainAdapter mMainAdapter;
        int parentPosition;
        boolean mLastIsAllSelect;

        SubObserver(@NonNull IReaderObservable observable) {
            mObservable = observable;
        }

        View.OnClickListener allSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childCount = mGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    IReaderMockData child = mGroup.getChild(i);
                    if(!child.isChecked()){
                        child.setChecked(true);
                        mObservable.notifyItemCheckChanged(true);
                    }
                }
                mSubAdapter.notifyDataSetChanged();
                mMainAdapter.notifyItemChanged(parentPosition);
            }
        };
        View.OnClickListener cancelSelectListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int childCount = mGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    IReaderMockData child = mGroup.getChild(i);
                    if(child.isChecked()){
                        child.setChecked(false);
                        mObservable.notifyItemCheckChanged(false);
                    }
                }
                mSubAdapter.notifyDataSetChanged();
                mMainAdapter.notifyItemChanged(parentPosition);
            }
        };

        void setBindResource(IReaderMockDataGroup source, TextView bindView,BaseMainAdapter mainAdapter ,BaseSubAdapter subAdapter,int parentPosition) {
            mGroup = source;
            selectAll = bindView;
            mSubAdapter = subAdapter;
            mMainAdapter = mainAdapter;
            this.parentPosition = parentPosition;
            updateBind(true);
        }


        @Override
        public void onChecked(boolean isChecked) {
            updateBind(false);
        }

        private void updateBind(boolean force) {
            boolean isAllSelect = mGroup.getChildCount() == mGroup.getCheckedCount();
            if(force){
                updateBindInternal(isAllSelect);
                return;
            }
            if (mLastIsAllSelect != isAllSelect) {
                updateBindInternal(isAllSelect);
            }
        }

        private void updateBindInternal(boolean isAllSelect){
            mLastIsAllSelect = isAllSelect;
            selectAll.setText(isAllSelect ? "取消" : "全选");
            selectAll.setOnClickListener(isAllSelect? cancelSelectListener : allSelectListener);
        }

    }

    /**
     * 返回当前拖拽的view 在adapter中的位置
     *
     * @return 返回int[0] 主层级位置 如果为 -1 则当前没有拖拽的item
     * int[1] 副层级位置 如果为 -1 则当前没有拖拽副层级的item
     */
    @NonNull
    public int[] getCurrentDragAdapterPosition() {
        mDragPosition[0] = getMainAdapter().getDragPosition();
        mDragPosition[1] = getSubAdapter().getDragPosition();
        return mDragPosition;
    }

    /**
     * @return 如果当前拖拽的为单个书籍 则返回 其他情况返回null
     */
    @Nullable
    IReaderMockData getCurrentSingleDragData() {
        int[] position = getCurrentDragAdapterPosition();
        if (position[0] == -1) return null;
        if (position[1] == -1) {
            IReaderMockData mockData = mMockSource.get(position[0]);
            if (mockData instanceof IReaderMockDataGroup) return null;
            return mockData;
        } else {
            return ((IReaderMockDataGroup) mMockSource.get(position[0])).getChild(position[1]);
        }
    }

    public void removeAllCheckedBook() {
        if (mCheckedData.size() == 0) return;
        for (IReaderMockData data : mCheckedData) {
            if (data.getParent() != null) {
                IReaderMockDataGroup parent = data.getParent();
                parent.removeChild(data);
                if (parent.getChildCount() == 0) {
                    mMockSource.remove(parent);
                }
            } else {
                mMockSource.remove(data);
            }
        }
        notifyDataSetChanged();
        getSubAdapter().notifyDataSetChanged();
        mObservable.notifyItemRestore();
        mObservable.notifyItemHideSubDialog();
    }

    /**
     * 添加数据
     *
     * @param mockData
     */
    public void addBook(IReaderMockData mockData) {
        if (mMockSource == null) mMockSource = new ArrayList<>();
        mMockSource.add(0, mockData);
        notifyItemInsert(0);
    }

    /**
     * 设置是否在可编辑状态下
     *
     * @param editMode
     */
    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
        if (!editMode) {
            if (mCheckedData.size() > 0) {
                for (IReaderMockData data : mCheckedData) {
                    data.setChecked(false);
                }
                mCheckedData.clear();
            }
            mObservable.notifyItemRestore();
        }
        notifyDataSetChanged();
        getSubAdapter().notifyDataSetChanged();
        mObservable.notifyItemEditModeChanged(editMode);
    }

    public boolean isEditMode() {
        return mEditMode;
    }

    public List<IReaderMockDataGroup> getMockGroup() {
        if (mMockSource == null) return null;
        if (mLastMockGroup != null && !mMockSourceChanged) {
            return mLastMockGroup;
        } else {
            List<IReaderMockDataGroup> result = new ArrayList<>();
            for (IReaderMockData mockData : mMockSource) {
                if (mockData instanceof IReaderMockDataGroup) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_i_reader_folder, parent, false);
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
        if (convertView != null) {
            result = convertView;
        } else {
            result = new View(parent.getContext());
        }
        try {
            int color = ((IReaderMockDataGroup) mMockSource.get(mainPosition)).getChild(subPosition).getColor();
            result.setBackgroundColor(color);
        } catch (Exception e) {
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
        return mMockSource == null ? 0 : mMockSource.size();
    }

    /**
     * 副层级的数量，用于主层级上的显示效果
     *
     * @param parentPosition
     * @return
     */
    @Override
    protected int getSubItemCount(int parentPosition) {
        if(parentPosition < mMockSource.size()) {
            IReaderMockData mockData = mMockSource.get(parentPosition);
            if (mockData instanceof IReaderMockDataGroup) {
                int subCount = ((IReaderMockDataGroup) mockData).getChildCount();
                return subCount;
            }
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
        if (mockData instanceof IReaderMockDataGroup) return (IReaderMockDataGroup) mockData;
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
        mMockSource.add(targetPosition, mMockSource.remove(selectedPosition));
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
        iReaderMockDataGroup.addChild(targetPosition, iReaderMockDataGroup.removeChild(selectedPosition));
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
        if (target instanceof IReaderMockDataGroup) {
            ((IReaderMockDataGroup) target).addChild(0, select);
        } else {
            //合并成为文件夹状态
            IReaderMockDataGroup group = new IReaderMockDataGroup();
            group.addChild(select);
            group.addChild(target);
            group.setCategory(generateNewCategoryTag());
            targetPosition = mMockSource.indexOf(target);
            mMockSource.remove(targetPosition);
            mMockSource.add(targetPosition, group);
        }
        mMockSourceChanged = true;
    }

    /**
     * 生成新的分类标签
     *
     * @return 新的分类标签
     */
    private String generateNewCategoryTag() {
        //生成默认分类标签
        List<IReaderMockDataGroup> mockDataGroups = getMockGroup();
        if (mockDataGroups.size() > 0) {
            int serialNumber = 1;
            int[] mHoldNumber = null;
            for (IReaderMockDataGroup temp : mockDataGroups) {
                if (temp.getCategory().startsWith("分类")) {
                    //可能是自动生成的标签
                    String pendingStr = temp.getCategory().substring(2);
                    if (!TextUtils.isEmpty(pendingStr) && TextUtils.isDigitsOnly(pendingStr)) {
                        //尝试转换为整数
                        try {
                            int serialCategory = Integer.parseInt(pendingStr);
                            if (mHoldNumber == null) {
                                mHoldNumber = new int[1];
                                mHoldNumber[0] = serialCategory;
                            } else {
                                mHoldNumber = Arrays.copyOf(mHoldNumber, mHoldNumber.length + 1);
                                mHoldNumber[mHoldNumber.length - 1] = serialCategory;
                            }
                        } catch (NumberFormatException e) {
                            //nope
                        }
                    }
                }
            }
            if (mHoldNumber != null) {
                //有自动生成的标签
                Arrays.sort(mHoldNumber);
                for (int serial : mHoldNumber) {
                    if (serial < serialNumber) continue;
                    if (serial == serialNumber) {
                        //已经被占用 自增1
                        serialNumber++;
                    } else {
                        break;
                    }
                }
            }
            return "分类" + serialNumber;
        } else {
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
    protected int onLeaveSubRegion(int parentPosition, IReaderMockDataGroup iReaderMockDataGroup, int selectedPosition) {
        if(mObservable.isRegister(mSubObserver)) mObservable.unregisterObserver(mSubObserver);
        //从副层级移除并添加到主层级第一个位置上
        IReaderMockData mockData = iReaderMockDataGroup.removeChild(selectedPosition);
        mMockSource.add(0, mockData);
        if (iReaderMockDataGroup.getChildCount() == 0) {
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
        holder.bind(mMockSource.get(position), mEditMode);
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
        holder.bind(((IReaderMockDataGroup) mMockSource.get(mainPosition)).getChild(subPosition), mEditMode);
    }


    @Override
    protected void onItemClick(ViewHolder viewHolder, int parentIndex, int index) {
        if (mEditMode) {
            final IReaderMockData mockData = index == -1 ? mMockSource.get(parentIndex) : ((IReaderMockDataGroup) mMockSource.get(parentIndex)).getChild(index);
            if (!(mockData instanceof IReaderMockDataGroup)) {
                //执行check动画
                mockData.setChecked(!mockData.isChecked());
                mCheckedData.add(mockData);
                //通知
                mObservable.notifyItemCheckChanged(mockData.isChecked());
                if (index != -1) {
                    notifyItemChanged(parentIndex);
                }
                final ItemIReaderFolderBinding binding = viewHolder.getBinding();
                binding.iReaderFolderCheckBox.setScaleX(0f);
                binding.iReaderFolderCheckBox.setScaleY(0f);
                binding.iReaderFolderCheckBox.animate().scaleX(1f).scaleY(1f).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        binding.iReaderFolderCheckBox.setScaleX(1f);
                        binding.iReaderFolderCheckBox.setScaleY(1f);
                        binding.iReaderFolderCheckBox.animate().setListener(null);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.iReaderFolderCheckBox.setScaleX(1f);
                        binding.iReaderFolderCheckBox.setScaleY(1f);
                        binding.iReaderFolderCheckBox.animate().setListener(null);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        binding.iReaderFolderCheckBox.setBackgroundResource(mockData.isChecked() ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                    }
                });
            }
        }
    }

    static class ViewHolder extends PrimitiveSimpleAdapter.ViewHolder {
        private ItemIReaderFolderBinding mBinding;

        ViewHolder(View itemView) {
            super(itemView);
            mBinding = ItemIReaderFolderBinding.bind(itemView);
        }





        ItemIReaderFolderBinding getBinding() {
            return mBinding;
        }

        void bind(IReaderMockData iReaderMockData, boolean inEditMode) {
            if (inEditMode) {
                if (iReaderMockData instanceof IReaderMockDataGroup) {
                    Log.i(Constants.CLASSIFY_VIEW_INIT,"IReaderMockDataGroup");
                    int count = ((IReaderMockDataGroup) iReaderMockData).getCheckedCount();
                    if (count > 0) {
                        mBinding.iReaderFolderCheckBox.setVisibility(View.VISIBLE);
                        mBinding.iReaderFolderCheckBox.setText(count + "");
                        mBinding.iReaderFolderCheckBox.setBackgroundDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_number_bg));
                    } else {
                        mBinding.iReaderFolderCheckBox.setVisibility(View.GONE);
                    }
                } else {
                    Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), iReaderMockData.isChecked() ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                    mBinding.iReaderFolderCheckBox.setText("");
                    mBinding.iReaderFolderCheckBox.setVisibility(View.VISIBLE);
                    mBinding.iReaderFolderCheckBox.setBackgroundDrawable(drawable);
                }
            } else {
                mBinding.iReaderFolderCheckBox.setVisibility(View.GONE);
            }
            if (iReaderMockData instanceof IReaderMockDataGroup) {
                mBinding.iReaderFolderGrid.setVisibility(View.VISIBLE);
                mBinding.iReaderFolderTag.setVisibility(View.VISIBLE);
                mBinding.iReaderFolderTag.setText(((IReaderMockDataGroup) iReaderMockData).getCategory());
                mBinding.iReaderFolderContent.setVisibility(View.GONE);
            } else {
                mBinding.iReaderFolderGrid.setVisibility(View.INVISIBLE);
                mBinding.iReaderFolderTag.setVisibility(View.GONE);
                mBinding.iReaderFolderContent.setBackgroundColor(iReaderMockData.getColor());
                mBinding.iReaderFolderContent.setVisibility(View.VISIBLE);
            }
        }
    }

    static class IReaderObservable extends Observable<IReaderObserver> {

        public boolean isRegister(IReaderObserver observer){
            return mObservers.contains(observer);
        }


        public void notifyItemCheckChanged(boolean isChecked) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChecked(isChecked);
            }
        }

        public void notifyItemEditModeChanged(boolean editMode) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onEditChanged(editMode);
            }
        }

        public void notifyItemRestore() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onRestore();
            }
        }

        public void notifyItemHideSubDialog(){
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onHideSubDialog();
            }
        }
    }

    public static abstract class IReaderObserver {
        public void onChecked(boolean isChecked) {

        }


        public void onEditChanged(boolean inEdit) {

        }

        public void onRestore() {

        }

        public void onHideSubDialog(){

        }
    }
}
