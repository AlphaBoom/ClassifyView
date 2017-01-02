package com.anarchy.classifyview.sample.normalfolder.bean;

import java.util.List;

/**
 * Created by lizhiming211223 on 2016/12/29.
 */
public class BaseBean {

    List<BookBean> bookList;
    public boolean isGroup;


    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }


    public BaseBean(List<BookBean> bookList) {
        this.bookList = bookList;
    }
    public List<BookBean> getBookList() {
        return bookList;
    }
    public BaseBean( ) {
    }
    public void setBookList(List<BookBean> bookList) {
        this.bookList = bookList;
    }

}
