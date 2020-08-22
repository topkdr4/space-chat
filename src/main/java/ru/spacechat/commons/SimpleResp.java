package ru.spacechat.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;





@AllArgsConstructor
public class SimpleResp<T> {

    public static final SimpleResp EMPTY = new SimpleResp<>(new HashMap<>());

    @Getter
    private final T result;

}
