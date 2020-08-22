package ru.spacechat.commons;


import lombok.Data;
import lombok.ToString;





@Data
@ToString
public class HikariConfig {
    private String jdbcUrl;
    private String driver;
    private String username;
    private String password;
    private int capacity;
}
