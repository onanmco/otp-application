package com.cemonan.otp.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cemonan.otp.constant.api_response.Errors;
import com.cemonan.otp.domain.api_gateway_proxy_response.ResponseDetail;
import com.cemonan.otp.domain.login.Body;
import com.cemonan.otp.domain.otp.Otp;
import com.cemonan.otp.domain.user.User;
import com.cemonan.otp.service.otp_service.OtpService;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Login implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResponseUtils responseUtils;
    @Autowired
    private Validator validator;
    @Autowired
    private UserService userService;
    @Autowired
    private OtpService otpService;

    @SneakyThrows
    @Override
    public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent event) {
        try {
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

            User existingUser = userService.getByEmail(body.getEmail());

            if (existingUser == null) {
                return responseUtils.generateErrorResponse(
                        Errors.NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        List.of(
                                ResponseDetail.builder()
                                        .message("User not found with email: " + body.getEmail())
                                        .build()
                        )
                );
            }

            if (!BCrypt.checkpw(body.getPassword(), existingUser.getPassword())) {
                return responseUtils.generateErrorResponse(
                        Errors.UNAUTHORIZED,
                        HttpStatus.UNAUTHORIZED,
                        List.of(
                                ResponseDetail.builder()
                                        .message(Errors.INVALID_CREDENTIALS)
                                        .build()
                        )
                );
            }

            Otp otp = otpService.createOtpAndSendSMS(existingUser);

            return new APIGatewayProxyResponseEvent()
                    .withHeaders(ResponseUtils.DEFAULT_HEADERS)
                    .withStatusCode(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(
                            Map.of(
                                    "data", Map.of(
                                            "message", "OTP has been successfully sent to the user's phone number.",
                                            "redirect_path", "/otp/" + otp.getId() + "/verify",
                                            "otp_expires_at", otp.getExpiresAt()
                                    )
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
