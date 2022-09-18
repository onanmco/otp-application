package com.cemonan.otp.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.cemonan.otp.constant.policy_document.Effect;
import com.cemonan.otp.domain.policy_document.PolicyDocument;
import com.cemonan.otp.domain.policy_document.Response;
import com.cemonan.otp.domain.policy_document.Statement;
import com.cemonan.otp.service.jwt_service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.function.Function;

@Component
public class Authorizer implements Function<APIGatewayCustomAuthorizerEvent, Response> {

    @Autowired
    private JwtService jwtService;

    @Override
    public Response apply(APIGatewayCustomAuthorizerEvent event) {
        try {
            String token = event.getHeaders()
                    .get("Authorization");

            token = token.substring(token.indexOf("Bearer "));

            if (!jwtService.isValid(token)) {
                throw new RuntimeException("Unauthorized");
            }

            Statement statement = Statement.builder()
                    .effect(Effect.Allow.name())
                    .resource(event.getMethodArn())
                    .build();

            PolicyDocument policyDocument = PolicyDocument.builder()
                    .statement(Collections.singletonList(statement))
                    .build();

            return Response.builder()
                    .principalId("user")
                    .policyDocument(policyDocument)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unauthorized");
        }
    }
}
