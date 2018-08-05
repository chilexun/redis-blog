package com.github.blog.utils;

public class PageQuery {
    private int pageNo;
    private int pageSize;

    public PageQuery(int pageNo, int pageSize){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
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

    public int getStartIndex(){
        if (this.pageNo > 0)
            return (pageNo-1) * this.pageSize;
        else
            return 0;
    }
}
