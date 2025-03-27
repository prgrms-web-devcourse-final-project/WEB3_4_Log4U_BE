package com.example.log4u.common.external.hanlder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.example.log4u.common.external.exception.ExternalApiRequestException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String body;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
            body = reader.lines().collect(Collectors.joining("\n"));
        }

        log.error("API 호출 중 에러 발생: HTTP 상태 코드: {}, 응답 본문: {}", response.getStatusCode().value(), body);

        throw new ExternalApiRequestException(response.getStatusCode().toString(),
                "API 호출 중 에러 발생: " + response.getStatusCode().value() + " 응답 본문: " + body);
    }
}
