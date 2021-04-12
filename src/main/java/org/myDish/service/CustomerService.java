package org.myDish.service;

import org.myDish.pojo.Customer;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerService extends AbstractService{
    @Inject
    DynamoDbClient dynamoDB;

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
