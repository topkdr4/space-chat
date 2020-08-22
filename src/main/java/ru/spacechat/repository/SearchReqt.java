package ru.spacechat.repository;

import lombok.Data;





@Data
public class SearchReqt {
    private int currentPage = 0;
    private int showPerPage = 20;


    public int getLimit() {
        return showPerPage;
    }


    public int getOffset() {
        return currentPage * showPerPage;
    }
}
