package com.cemonan.otp.domain.otp;


import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Otp {

    @Getter(onMethod = @__({@DynamoDbPartitionKey}))
    private UUID id;
    private UUID userId;
    private int otp;
    private boolean isUsed;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;
}
