package com.cemonan.otp.domain.api_gateway_proxy_response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDetail {

    private String field;
    private String message;
}
