package com.anarchy.classifyview.sample.demonstrate.logic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anarchy.classifyview.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * <p/>
 * Date: 16/6/12 14:20
 * Author: rsshinide38@163.com
 * <p/>
 */
public class SelectBookListAdapter extends RecyclerView.Adapter<SelectBookListAdapter.ViewHolder> {
    private List<Book> mBookList;
    private ItemClickListener mItemClickListener;



    public void setItemClickListener(ItemClickListener listener){
        mItemClickListener = listener;
    }


    public void setBookList(List<Book> bookList){
        mBookList = bookList;
        notifyDataSetChanged();
    }

    public List<Book> getBookList() {
        return mBookList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Book book = mBookList.get(position);
        holder.title.setText(book.name);
        holder.summary.setText(book.summary);
        Picasso.with(holder.itemView.getContext()).load(book.imageUrl).into(holder.cover);
        if(mItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        mItemClickListener.onItemClick(v,position,book);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mBookList != null) return mBookList.size();
        return 0;
    }

    public interface ItemClickListener{
        void onItemClick(View parent,int position,Book book);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView cover;
        private TextView title;
        private TextView summary;
        public ViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.book_cover);
            title = (TextView) itemView.findViewById(R.id.text_title);
            summary = (TextView) itemView.findViewById(R.id.text_summary);
        }
    }
}
