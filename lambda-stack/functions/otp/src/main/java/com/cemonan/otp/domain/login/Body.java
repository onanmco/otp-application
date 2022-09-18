package com.cemonan.otp.domain.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Body {

    @NotEmpty(message = "email is required.")
    @Email(message = "email field must be a valid E-mail address.")
    private String email;
    @NotEmpty(message = "password is required.")
    private String password;
}
