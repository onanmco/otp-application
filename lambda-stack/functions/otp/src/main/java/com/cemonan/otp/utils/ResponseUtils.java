package com.cemonan.otp.utils;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cemonan.otp.domain.api_gateway_proxy_response.ResponseDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResponseUtils {

    @Autowired
    private ObjectMapper objectMapper;

    public final static Map<String, String> DEFAULT_HEADERS;

    static {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        DEFAULT_HEADERS = headers.toSingleValueMap();
    }

    @SneakyThrows
    public APIGatewayProxyResponseEvent generateErrorResponse(String error, HttpStatus statusCode) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", Instant.now().getEpochSecond());
        responseBody.put("error", error);
        responseBody.put("details", new ArrayList<>());
        return new APIGatewayProxyResponseEvent()
                .withHeaders(DEFAULT_HEADERS)
                .withStatusCode(statusCode.value())
                .withBody(objectMapper.writeValueAsString(responseBody));
    }

    @SneakyThrows
    public APIGatewayProxyResponseEvent generateErrorResponse(String error, HttpStatus statusCode, List<ResponseDetail> details) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", Instant.now().getEpochSecond());
        responseBody.put("error", error);
        if (details == null) {
            details = new ArrayList<>();
        }
        responseBody.put("details", details);
        return new APIGatewayProxyResponseEvent()
                .withHeaders(DEFAULT_HEADERS)
                .withStatusCode(statusCode.value())
                .withBody(objectMapper.writeValueAsString(responseBody));
    }
}
