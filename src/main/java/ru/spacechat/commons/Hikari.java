package ru.spacechat.commons;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;





@Slf4j
public class Hikari {


    public static HikariDataSource getDataSource(HikariConfig config) {
        HikariDataSource result = new HikariDataSource();

        result.setDriverClassName(config.getDriver());
        result.setJdbcUrl(config.getJdbcUrl());
        result.setUsername(config.getUsername());
        result.setPassword(config.getPassword());
        result.setMaximumPoolSize(config.getCapacity());

        result.setMinimumIdle(1);

        result.setMaxLifetime(TimeUnit.MINUTES.toMillis(5));
        result.setIdleTimeout(0);

        return result;
    }

}
