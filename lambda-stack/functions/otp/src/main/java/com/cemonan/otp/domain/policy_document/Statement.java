package com.cemonan.otp.domain.policy_document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statement {

    @JsonProperty("Effect")
    private String effect;
    @JsonProperty("Action")
    private final String action = "execute-api:Invoke";
    @JsonProperty("Resource")
    private String resource;
}
