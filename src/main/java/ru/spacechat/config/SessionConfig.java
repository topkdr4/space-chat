package ru.spacechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;





@Configuration
@EnableJdbcHttpSession
public class SessionConfig {


    @Bean(name = "attrName")
    public String attrName() {
        return "login";
    }


/*

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(cookieName());
        serializer.setCookiePath("/");
        serializer.setCookieMaxAge((int) TimeUnit.DAYS.toSeconds(30));
        serializer.setUseSecureCookie(false);
        return serializer;
    }
*/

}
