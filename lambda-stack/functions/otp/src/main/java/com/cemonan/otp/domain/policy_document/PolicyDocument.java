package com.cemonan.otp.domain.policy_document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDocument {

    @JsonProperty("Version")
    private static String version = "2012-10-17";
    @JsonProperty("Statement")
    List<Statement> statement;
}
