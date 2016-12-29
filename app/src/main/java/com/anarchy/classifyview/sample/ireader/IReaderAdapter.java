package com.anarchy.classifyview.sample.ireader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anarchy.classify.simple.SimpleAdapter;
import com.anarchy.classifyview.R;
import com.anarchy.classifyview.sample.ireader.model.IReaderMockData;
import com.anarchy.classifyview.sample.ireader.widget.IReaderFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * User:  Anarchy
 * Email:  rsshinide38@163.com
 * Date: 16/12/27 14:44
 * Description:
 */
public class IReaderAdapter extends SimpleAdapter<IReaderMockData, IReaderAdapter.ViewHolder> {
    private int[] mDragPosition = new int[2];
    public static final int NORMAL = 0;
    public static final int IN_FOLDER = 1;

    public IReaderAdapter(List<List<IReaderMockData>> data) {
        super(data);

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
            List<IReaderMockData> mockDatas = mData.get(position[0]);
            return null;
        }else {
            return mData.get(position[0]).get(position[1]);
        }
    }

    /**
     * 添加书籍
     * @param data
     */
    public void addBook(IReaderMockData data){
        List<IReaderMockData> dataList = new ArrayList<>();
        dataList.add(data);
        mData.add(0,dataList);
        notifyItemInsert(0);
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_i_reader_folder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition) {
        if(convertView != null){
//            convertView.setBackgroundColor(mData.get(mainPosition).get(subPosition).color);
        }
        View view = new ImageView(parent.getContext());
//        view.setBackgroundColor(mData.get(mainPosition).get(subPosition).color);
        return view;
    }



    static class ViewHolder extends SimpleAdapter.ViewHolder {
        IReaderFolder ireaderFolder;

        ViewHolder(View itemView) {
            super(itemView);
            ireaderFolder = (IReaderFolder) itemView.findViewById(R.id.i_reader_folder);
        }
    }
}
