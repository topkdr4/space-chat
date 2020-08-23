package ru.spacechat.model;


import lombok.Getter;
import lombok.Setter;





@Getter
@Setter
public class UserProfile {
    /**
     * Имя пользователя
     */
    private String name;

    /**
     * Дата рождения
     */
    private Long birth;

    /**
     * Статус
     */
    private String status;

    /**
     * Пол
     */
    private boolean sex;

    /**
     * Город
     */
    private String city;

    /**
     * Телефон
     */
    private String phone;

    /**
     * О себе
     */
    private String aboutMe;

}
