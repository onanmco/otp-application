package com.cemonan.otp.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cemonan.otp.constant.api_response.Errors;
import com.cemonan.otp.domain.api_gateway_proxy_response.ResponseDetail;
import com.cemonan.otp.domain.user.User;
import com.cemonan.otp.service.user_service.UserService;
import com.cemonan.otp.utils.ResponseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Register implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResponseUtils responseUtils;
    @Autowired
    private Validator validator;
    @Autowired
    private UserService userService;

    @SneakyThrows
    @Override
    public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent event) {
        try {
            User body;
            try {
                body = objectMapper.readValue(event.getBody(), User.class);
            } catch (JsonProcessingException e) {
                return responseUtils.generateErrorResponse(
                        Errors.VALIDATION_ERROR,
                        HttpStatus.BAD_REQUEST,
                        List.of(
                                ResponseDetail.builder()
                                        .message(Errors.EVENT_OBJECT_PARSING_ERROR)
                                        .build()
                        )
                );
            }

            Set<ConstraintViolation<User>> violations = validator.validate(body);
            if (!violations.isEmpty()) {
                return responseUtils.generateErrorResponse(
                        Errors.VALIDATION_ERROR,
                        HttpStatus.BAD_REQUEST,
                        violations.stream()
                                .map(
                                        v -> ResponseDetail.builder()
                                                .field(v.getPropertyPath().toString())
                                                .message(v.getMessage())
                                                .build()
                                )
                                .collect(Collectors.toList())
                );
            }

            User existingUser = userService.getByEmail(body.getEmail());

            if (existingUser != null) {
                return responseUtils.generateErrorResponse(
                        Errors.VALIDATION_ERROR,
                        HttpStatus.BAD_REQUEST,
                        List.of(
                                ResponseDetail.builder()
                                        .message("E-mail: " + body.getEmail() + " has already taken.")
                                        .build()
                        )
                );
            }

            User existingUserByPhoneNumber = userService.getByPhoneNumber(body.getPhoneNumber());

            if (existingUserByPhoneNumber != null) {
                return responseUtils.generateErrorResponse(
                        Errors.VALIDATION_ERROR,
                        HttpStatus.BAD_REQUEST,
                        List.of(
                                ResponseDetail.builder()
                                        .message("Phone number: " + body.getPhoneNumber() + " has already taken.")
                                        .build()
                        )
                );
            }

            OffsetDateTime now = Instant.now()
                    .atOffset(ZoneOffset.UTC);

            User savedUser = userService.insert(
                    User.builder()
                            .id(UUID.randomUUID())
                            .firstName(body.getFirstName())
                            .lastName(body.getLastName())
                            .email(body.getEmail())
                            .phoneNumber(body.getPhoneNumber())
                            .password(BCrypt.hashpw(body.getPassword(), BCrypt.gensalt()))
                            .createdAt(now)
                            .updatedAt(now)
                            .build()
            );

            savedUser.setPassword(null);

            return new APIGatewayProxyResponseEvent()
                    .withHeaders(ResponseUtils.DEFAULT_HEADERS)
                    .withStatusCode(HttpStatus.CREATED.value())
                    .withBody(objectMapper.writeValueAsString(
                            Map.of(
                                    "data", savedUser
                            )
                    ));
        } catch (Exception e) {
            e.printStackTrace();
            return responseUtils.generateErrorResponse(
                    Errors.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
