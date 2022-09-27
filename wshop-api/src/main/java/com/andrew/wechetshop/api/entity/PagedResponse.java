package com.andrew.wechetshop.api.entity;

import java.io.Serializable;
import java.util.List;

public class PagedResponse<T> implements Serializable {
    Integer pageNum;
    Integer pageSize;
    Integer totalPage;
    List<T> data;

    public PagedResponse() {
    }

    public static <T> PagedResponse<T> pagedDataByParam(Integer pageNum, Integer pageSize, Integer totalPage, List<T> data) {
        PagedResponse<T> pagedResponse = new PagedResponse<T>();
        pagedResponse.setPageNum(pageNum);
        pagedResponse.setPageSize(pageSize);
        pagedResponse.setTotalPage(totalPage);
        pagedResponse.setData(data);
        return pagedResponse;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
