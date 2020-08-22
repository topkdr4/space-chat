package ru.spacechat.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ru.spacechat.commons.Hikari;
import ru.spacechat.commons.HikariConfig;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;





@Slf4j
@EnableWebMvc
@Configuration
@EnableSwagger2
@ComponentScan(basePackages = "ru.spacechat")
public class WebConfigurer implements WebMvcConfigurer {

    private static final String APP_PROPERTIES = "application.properties";

    private static final String PERSON_ICON = "person-icon.png";
    @Autowired
    private ResourceLoader resourceLoader;


    @Bean
    public HikariConfig hikariConfig() {
        Resource file = new ClassPathResource(APP_PROPERTIES);
        Properties properties = new Properties();
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HikariConfig result = new HikariConfig();
        result.setDriver(properties.getProperty("main.driver"));
        result.setJdbcUrl(properties.getProperty("main.jdbcUrl"));
        result.setUsername(properties.getProperty("main.username"));
        result.setPassword(properties.getProperty("main.password"));
        result.setCapacity(Integer.parseInt(properties.getProperty("main.capacity")));

        return result;
    }


    @Primary
    @Bean(name = "main", destroyMethod = "close")
    public DataSource mainDataSource() {
        return Hikari.getDataSource(hikariConfig());
    }


    @Bean
    public NamedParameterJdbcOperations operations() {
        return new NamedParameterJdbcTemplate(mainDataSource());
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        return mapper;
    }


    @Bean(name = "personIcon")
    public byte[] personIcon() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(PERSON_ICON);

        return ByteStreams.toByteArray(is);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resource/**").addResourceLocations("WEB-INF/resources/");
    }


    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/view/");
        resolver.setSuffix(".html");
        return resolver;
    }


    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

}
