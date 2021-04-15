package org.myDish;
import software.amazon.awssdk.http.SdkHttpService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.myDish.pojo.Customer;
import org.myDish.service.CustomerService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ProcessingLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = Logger.getLogger(ProcessingLambda.class);


    private ObjectMapper mapper = new ObjectMapper();


    CustomerService customerService = new CustomerService();

    @Override
     public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {

        Map<String, String> query = request.getQueryStringParameters();

        LOGGER.info(String.format("[%s] Processed data", request));

        Customer customer;
        String result = "";
        List<Customer> customerList;

        String httpMethod = request.getHttpMethod();
        LOGGER.info(String.format("http method is:", httpMethod));
        Map<String, String> pathParameters = request.getPathParameters();

        switch (request.getHttpMethod()) {

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
                catch (java.io.IOException exc) {
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

    private static String createUserId() {
        return UUID.randomUUID().toString();
    }


}
