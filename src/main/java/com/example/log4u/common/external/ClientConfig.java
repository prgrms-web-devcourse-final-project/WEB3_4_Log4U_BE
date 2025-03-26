package com.example.log4u.common.external;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.example.log4u.common.external.hanlder.ApiResponseErrorHandler;

@Configuration
public class ClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ApiResponseErrorHandler());
        return restTemplate;
    }
}
