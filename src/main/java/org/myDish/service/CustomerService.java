package org.myDish.service;

import org.myDish.pojo.Customer;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.stream.Collectors;


public class CustomerService extends AbstractService{

    SdkHttpClient httpClient = UrlConnectionHttpClient.builder().
            build();
    Region region = Region.US_WEST_2;
    DynamoDbClient dynamoDB = DynamoDbClient.builder()
            .region(region).httpClient(httpClient)
                .build();


    public List<Customer> findAll() {
        return dynamoDB.scanPaginator(scanRequest()).items().stream()
                .map(Customer::from)
                .collect(Collectors.toList());
    }

    public String add(Customer customer) {
        dynamoDB.putItem(putRequest(customer));

        return customer.getCustomerId();
    }

    public Customer get(String userId) {
        return Customer.from(dynamoDB.getItem(getRequest(userId)).item());
    }

    public String delete(String userId) {
        dynamoDB.deleteItem(deleteRequest(userId));

        return userId;
    }
}
