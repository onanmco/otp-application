package com.cemonan.otp.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cemonan.otp.constant.api_response.Errors;
import com.cemonan.otp.domain.api_gateway_proxy_response.ResponseDetail;
import com.cemonan.otp.domain.otp.Otp;
import com.cemonan.otp.domain.verify_otp.Body;
import com.cemonan.otp.domain.verify_otp.PathParameters;
import com.cemonan.otp.service.jwt_service.JwtService;
import com.cemonan.otp.service.otp_service.OtpService;
import com.cemonan.otp.utils.ResponseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
public class VerifyOtp implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResponseUtils responseUtils;
    @Autowired
    private Validator validator;
    @Autowired
    private OtpService otpService;
    @Autowired
    private JwtService jwtService;

    @SneakyThrows
    @Override
    public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent event) {
        try {
            PathParameters pathParameters = objectMapper.convertValue(event.getPathParameters(), PathParameters.class);
            Set<ConstraintViolation<PathParameters>> pathParametersViolations = validator.validate(pathParameters);
            if (!pathParametersViolations.isEmpty()) {
                return responseUtils.generateErrorResponse(
                        Errors.VALIDATION_ERROR,
                        HttpStatus.BAD_REQUEST,
                        pathParametersViolations.stream()
                                .map(
                                        v -> ResponseDetail.builder()
                                                .field(v.getPropertyPath().toString())
                                                .message(v.getMessage())
                                                .build()
                                )
                                .collect(Collectors.toList())
                );
            }

            Body body;
            try {
                body = objectMapper.readValue(event.getBody(), Body.class);
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

            Set<ConstraintViolation<Body>> violations = validator.validate(body);
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

            Otp existingOtp = otpService.getById(UUID.fromString(pathParameters.getOtpId()));

            if (existingOtp == null) {
                return responseUtils.generateErrorResponse(
                        Errors.NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        List.of(
                                ResponseDetail.builder()
                                        .message("OTP with id: " + pathParameters.getOtpId() + " not found.")
                                        .build()
                        )
                );
            }

            if (existingOtp.getOtp() != Integer.parseInt(body.getOtp())) {
                return responseUtils.generateErrorResponse(
                        Errors.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        List.of(
                                ResponseDetail.builder()
                                        .message(Errors.INVALID_OTP)
                                        .build()
                        )
                );
            }

            if (existingOtp.isUsed()) {
                return responseUtils.generateErrorResponse(
                        Errors.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        List.of(
                                ResponseDetail.builder()
                                        .message(Errors.EXPIRED_OTP)
                                        .build()
                        )
                );
            }

            OffsetDateTime now = Instant.now()
                    .atOffset(ZoneOffset.UTC);

            if (!now.isBefore(existingOtp.getExpiresAt())) {
                return responseUtils.generateErrorResponse(
                        Errors.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        List.of(
                                ResponseDetail.builder()
                                        .message(Errors.EXPIRED_OTP)
                                        .build()
                        )
                );
            }

            existingOtp.setUsed(true);
            otpService.update(existingOtp);

            String jwt = jwtService.create(existingOtp.getUserId());

            return new APIGatewayProxyResponseEvent()
                    .withHeaders(ResponseUtils.DEFAULT_HEADERS)
                    .withStatusCode(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(
                            Map.of(
                                    "data", jwt
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
