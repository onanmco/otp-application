package com.cemonan.otp.dao;

import com.cemonan.otp.domain.otp.Otp;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.UUID;

@Component
public class OtpDaoImpl implements OtpDao {

    private static DynamoDbTable<Otp> table;

    static {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();

        DynamoDbEnhancedClient client = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        table = client.table(System.getenv("OTP_TABLE_NAME"), TableSchema.fromBean(Otp.class));
    }

    @Override
    public Otp insert(Otp otp) {
        table.putItem(otp);
        return this.getById(otp.getId());
    }

    @Override
    public Otp getById(UUID id) {
        Key key = Key.builder()
                .partitionValue(id.toString())
                .build();
        return table.getItem(item -> item.key(key));
    }

    @Override
    public Otp update(Otp otp) {
        table.updateItem(otp);
        return this.getById(otp.getId());
    }
}
