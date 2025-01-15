package dk.dtu.adapters;

import dk.dtu.core.models.Customer;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Driven adapter responsible for abstracting inter-process communication related to any customer use-cases and scenarios.
 */
public final class CustomerAdapter {

    private final Client client = ClientBuilder.newBuilder().build();

    /**
     * Registers the given customer to the DtuPay system
     * @param dtuPayCustomer the customer
     * @return DtuPlay customer ID
     * @author Andrei Soldan s243873
     */
    public String register(Customer dtuPayCustomer) {
        Response response = client.target("http://localhost:/8082/customers")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(dtuPayCustomer));

        return response.readEntity(String.class);
    }
}
