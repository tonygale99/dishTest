package org.myDish;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.myDish.pojo.Customer;
import org.myDish.service.CustomerService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jboss.logging.Logger;

@Named("processing")
public class ProcessingLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = Logger.getLogger(ProcessingLambda.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Inject
    CustomerService customerService;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {

        Map<String, String> query = request.getQueryStringParameters();

        LOGGER.info(String.format("[%s] Processed data", request));

        Customer customer;
        String result = "";
        List<Customer> customerList;

        String httpMethod = request.getHttpMethod();

        Map<String, String> pathParameters = request.getPathParameters();

        switch (httpMethod) {

            case "GET":
                Map<String, String> queryStringParameters = request.getQueryStringParameters();

                String customerId = null;

                if (pathParameters != null)
                    customerId = pathParameters.get("customerId");
                else if (queryStringParameters != null)
                    customerId = queryStringParameters.get("customerId");

                if (customerId == null || customerId.length() == 0) {
                    LOGGER.info("Getting all users");
                    customerList = customerService.findAll();
                    LOGGER.info("GET: " + customerList);
                    try {
                        result = mapper.writeValueAsString(customerList);
                    } catch (JsonProcessingException exc) {
                        LOGGER.error(exc);
                    }
                } else {
                    customer = customerService.get(customerId);
                    LOGGER.info("GET: " + customer);

                    if (customer.getCustomerId() == null)
                        result = "";
                    else {
                        try {
                            result = mapper.writeValueAsString(customer);
                        } catch (JsonProcessingException exc) {
                            LOGGER.error(exc);
                        }
                    }
                }
                break;
            case "POST":
                String body = request.getBody();
                try {
                    Customer tmpCustomer = mapper.readValue(body, Customer.class);
                    tmpCustomer.setCustomerId(createUserId());

                    LOGGER.info("POST: " + tmpCustomer);
                    String tmpId = customerService.add(tmpCustomer);

                    result = tmpId;
                }
                catch (JsonProcessingException exc) {
                    LOGGER.error(exc);
                }
                break;
            case "DELETE":
                if (pathParameters != null) {
                    String id = pathParameters.get("userId");
                    String tmpId = customerService.delete(id);

                    LOGGER.info("DELETE: " + tmpId);

                    result = tmpId;
                }
                break;
        }

        return new APIGatewayProxyResponseEvent().withBody(result).withStatusCode(200);
    }

    private String createUserId() {
        return UUID.randomUUID().toString();
    }


}
