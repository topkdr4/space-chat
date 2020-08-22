package ru.spacechat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.spacechat.repository.UserRepository;
import ru.spacechat.repository.UserService;





@Component
public class FilterComponent {


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    @Qualifier("attrName")
    private String attrName;

    @Bean
    public FilterRegistrationBean<RequestFilter> logFilter() {
        FilterRegistrationBean<RequestFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RequestFilter(userRepository, userService, mapper, attrName));
        bean.addUrlPatterns("/api/*");
        return bean;
    }

}
