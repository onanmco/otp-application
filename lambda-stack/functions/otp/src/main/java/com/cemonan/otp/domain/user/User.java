package com.cemonan.otp.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Getter(onMethod = @__({@DynamoDbPartitionKey}))
    private UUID id;
    @NotEmpty(message = "firstName is required.")
    private String firstName;
    @NotEmpty(message = "lastName is required.")
    private String lastName;
    @NotEmpty(message = "email is required.")
    @Email(message = "email field should be a valid e-mail address.")
    private String email;
    @NotEmpty(message = "phoneNumber is required.")
    @Pattern(regexp = "^\\+\\d+$", message = "phoneNumber must be a valid phone number starting with country code.")
    private String phoneNumber;
    @NotEmpty(message = "password is required.")
    @Size(min = 8, message = "password should be at least 8 characters long.")
    @Size(max = 32, message = "password should be at most 32 characters long.")
    @Pattern(regexp = "^.*\\d.*$", message = "password should contain at least 1 digit.")
    @Pattern(regexp = "^.*[a-z].*$", message = "password should contain at least 1 lowercase letter.")
    @Pattern(regexp = "^.*[A-Z].*$", message = "password should contain at least 1 uppercase letter.")
    private String password;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
