package ru.spacechat.repository;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;





public class SearchResp<T> {

    @Getter
    private final Collection<T> data = new ArrayList<>();
    @Getter
    private int pageCount;
    @Getter
    private int itemCount;
    @Getter
    @Setter
    private int currentPage;
    @Getter
    @Setter
    private int showPerPage;


    public void setItemCount(int count) {
        this.itemCount = count;

        if (showPerPage == 0) {
            this.pageCount = count;
            return;
        }

        int result = count / showPerPage;

        result = result == 0 ? 1 : ++result;


        this.pageCount = result;
    }

}
