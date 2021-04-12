package org.myDish.pojo;


import io.quarkus.runtime.annotations.RegisterForReflection;
import org.myDish.service.AbstractService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.Map;
import java.util.Objects;

@RegisterForReflection
public class Customer {
    private String customerId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private String firstName;

    public java.lang.String getLastName() {
        return lastName;
    }

    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }

    private String lastName;

    public Customer(){}
    public Customer(String customerId,  String firstName, String lastName) {
        this.customerId = customerId;

        this.firstName = firstName;
        this.lastName = lastName;

    }
    public static Customer from(Map<String, AttributeValue> item) {
        Customer customer = new Customer();
        if (item != null && !item.isEmpty()) {

            customer.setCustomerId(item.get(AbstractService.CUSTOMER_ID_COL).s());

            customer.setFirstName(item.get(AbstractService.CUSTOMER_FIRSTNAME_COL).s());
            customer.setLastName(item.get(AbstractService.CUSTOMER_LASTNAME_COL).s());

        }
        return customer;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customer.equals(customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return "UserPojo{" +
                "userId=" + customerId +

                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +

                '}';
    }
}
