package dk.dtu.adapters;

import dk.dtu.core.models.Customer;
import dk.dtu.core.models.TokenResult;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

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
    public String register(Customer dtuPayCustomer) throws Exception {
        Response response = client.target("http://localhost:8082/customers")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(dtuPayCustomer));

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(String.class);
    }

    public String deregister(String  id) {
        Response response = client.target("http://localhost:8082/customers/" + id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.readEntity(String.class);
    }

    public List<TokenResult> getTokens(String id, int amount) throws Exception {
        Response response = client.target("http://localhost:8082/tokens/customer/" + id)
                .queryParam("amount", amount)
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(new GenericType<List<TokenResult>>() {
        });
    }

}
