package com.neu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
//import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


@SpringBootApplication
public class Assignment3CloudApplication  {


   /* @Override extends SpringBootServletInitializer
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Assignment3CloudApplication.class).properties(getRemoteConnection());
    }*/


    public static void main(String[] args)
    {

        SpringApplication.run(Assignment3CloudApplication.class, args);
    }


    /*private static Properties getRemoteConnection() {
        if (System.getProperty("RDS_HOSTNAME") != null) {
            try {
                Class.forName("org.postgresql.Driver");
                String dbName = System.getProperty("RDS_DB_NAME");
                String userName = System.getProperty("RDS_USERNAME");
                String password = System.getProperty("RDS_PASSWORD");
                String hostname = System.getProperty("RDS_HOSTNAME");
                String port = System.getProperty("RDS_PORT");
                String jdbcUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
                Connection con = DriverManager.getConnection(jdbcUrl);
                Properties props = new Properties();
                props.put("spring.datasource.username", userName);
                props.put("spring.datasource.password",password);
                props.put("spring.datasource.url",jdbcUrl);
                return props;
            }
            catch (ClassNotFoundException e) {
                //logger.warn(e.toString());
                }
            catch (SQLException e) {
                //logger.warn(e.toString());
            }
        }
        return null;
    }*/

}
