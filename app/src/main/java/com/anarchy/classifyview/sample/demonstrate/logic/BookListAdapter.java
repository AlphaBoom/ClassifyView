package com.anarchy.classifyview.sample.demonstrate.logic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anarchy.classify.simple.SimpleAdapter;
import com.anarchy.classifyview.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * <p/>
 * Date: 16/6/12 14:38
 * Author: rsshinide38@163.com
 * <p/>
 */
public class BookListAdapter extends SimpleAdapter<Book,BookListAdapter.ViewHolder> {


    public BookListAdapter(List<List<Book>> mData) {
        super(mData);
    }


    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public View getView(ViewGroup parent,View convertView, int mainPosition, int subPosition) {
        ItemViewHolder itemViewHolder;
        if(convertView == null){
            itemViewHolder = new ItemViewHolder();
           convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_inner,parent,false);
            itemViewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(itemViewHolder);
        }else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }
        String url = mData.get(mainPosition).get(subPosition).imageUrl;
        Picasso.with(parent.getContext()).load(url).into(itemViewHolder.imageView);
        return convertView;
    }

    @Override
    protected void onBindMainViewHolder(ViewHolder holder, int position) {
        List<Book> books = mData.get(position);
        if(books.size()>1){
            holder.name.setText("");
        }else {
            holder.name.setText(books.get(0).name);
        }
    }

    @Override
    protected void onBindSubViewHolder(ViewHolder holder, int mainPosition, int subPosition) {
        holder.name.setText(mData.get(mainPosition).get(subPosition).name+"");
    }

    public static class ViewHolder extends SimpleAdapter.ViewHolder {
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_name);
        }
    }


    static class ItemViewHolder{
        ImageView imageView;
    }
}
