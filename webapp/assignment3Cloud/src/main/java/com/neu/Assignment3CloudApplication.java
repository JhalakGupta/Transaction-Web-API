package com.neu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;


import javax.sql.DataSource;


@SpringBootApplication
public class Assignment3CloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(Assignment3CloudApplication.class, args);
    }


}
