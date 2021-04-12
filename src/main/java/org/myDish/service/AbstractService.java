package org.myDish.service;


import org.myDish.pojo.Customer;

import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.HashMap;
import java.util.Map;

public class AbstractService {

    public final static String CUSTOMER_FIRSTNAME_COL = "firstName";
    public final static String CUSTOMER_LASTNAME_COL = "lastName";

    public final static String CUSTOMER_ID_COL = "customerId";

    public String getTableName() {
        return "customer";
    }

    protected ScanRequest scanRequest() {
        return ScanRequest.builder().tableName(getTableName())
                .attributesToGet(CUSTOMER_ID_COL, CUSTOMER_FIRSTNAME_COL, CUSTOMER_LASTNAME_COL).build();
    }

    protected PutItemRequest putRequest(Customer customer) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(CUSTOMER_FIRSTNAME_COL, AttributeValue.builder().s(customer.getFirstName()).build());
        item.put(CUSTOMER_LASTNAME_COL, AttributeValue.builder().s(customer.getLastName()).build());

        item.put(CUSTOMER_ID_COL, AttributeValue.builder().s(customer.getCustomerId()).build());


        return PutItemRequest.builder()
                .tableName(getTableName())
                .item(item)
                .build();
    }

    protected DeleteItemRequest deleteRequest(String userId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(CUSTOMER_ID_COL, AttributeValue.builder().s(userId).build());

        return DeleteItemRequest.builder().tableName(getTableName()).key(key).build();
    }

    protected GetItemRequest getRequest(String userId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(CUSTOMER_ID_COL, AttributeValue.builder().s(userId).build());

        return GetItemRequest.builder()
                .tableName(getTableName())
                .key(key)
                .attributesToGet(CUSTOMER_ID_COL, CUSTOMER_FIRSTNAME_COL, CUSTOMER_LASTNAME_COL)
                .build();
    }
}
