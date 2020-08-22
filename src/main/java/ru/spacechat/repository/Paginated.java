package ru.spacechat.repository;

public interface Paginated<T> {

    SearchResp<T> find(SearchReqt reqt);


}
