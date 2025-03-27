package com.example.log4u;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Log4UApplication {

    public static void main(String[] args) {
        SpringApplication.run(Log4UApplication.class, args);
    }

}
