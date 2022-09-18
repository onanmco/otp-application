package com.cemonan.otp.dao;

import com.cemonan.otp.domain.user.User;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Component
public class UserDaoImpl implements UserDao {

    private static DynamoDbTable<User> table;

    static {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();

        DynamoDbEnhancedClient client = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        table = client.table(System.getenv("USERS_TABLE_NAME"), TableSchema.fromBean(User.class));
    }

    @Override
    public User getByEmail(String email) {
        return table.scan()
                .items()
                .stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User getByPhoneNumber(String phoneNumber) {
        return table.scan()
                .items()
                .stream()
                .filter(user -> phoneNumber.equals(user.getPhoneNumber()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User insert(User user) {
        table.putItem(user);
        return this.getByEmail(user.getEmail());
    }
}
