package ru.spacechat.commons;

import java.util.Objects;





public class Util {


    private static final String EMPTY_STRING = "";


    public static boolean isEmpty(String source) {
        return source == null || Objects.equals(source, EMPTY_STRING);
    }

}
