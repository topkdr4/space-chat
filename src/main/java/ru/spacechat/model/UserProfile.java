package ru.spacechat.model;


import lombok.Data;





@Data
public class UserProfile  {
    /**
     *
     */
    private String login;

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

}
