package com.example.technology_forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
public class TechnologyForumApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechnologyForumApplication.class, args);
    }

}
