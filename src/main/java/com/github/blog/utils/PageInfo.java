package com.github.blog.utils;

import java.util.List;

public class PageInfo<T> {

    private int pageNo;
    private int pageSize;
    private int size;
    private int pages;
    private long total;
    private boolean hasNext = false;
    private List<T> list;

    public PageInfo(List<T> list, PageQuery page){
        this(list, page, list.size());
    }

    public PageInfo(List<T> list, PageQuery page, int total){
        this.list = list;
        this.total = total;
        this.pageNo = page.getPageNo();
        this.pageSize = page.getPageSize();
        this.size = list.size();
        if(this.size > this.pageSize) {
            this.size = this.pageSize;
            this.list = this.getList().subList(0, this.size);
            hasNext = true;
        }
        double totalPage = (double)total/(double)pageSize;
        this.pages = (int)Math.ceil(totalPage);
    }

    public static <T> PageInfo<T> of(List<T> list, PageQuery page){
        return new PageInfo<>(list, page);
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
