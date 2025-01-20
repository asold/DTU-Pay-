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

public class ReportAdapter {

    private final Client client = ClientBuilder.newBuilder().build();

    /** Author Karrar Adam s230432
     */
    public String register(Customer dtuPayCustomer) {
        Response response = client.target("http://localhost:8082/customers")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(dtuPayCustomer));

        return response.readEntity(String.class);
    }

    public List<TokenResult> getTokens(String id, int amount) {
        Response response = client.target("http://localhost:8082/tokens/" + id)
                .queryParam("amount", amount)
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<TokenResult> receivedTokens = response.readEntity(new GenericType<List<TokenResult>>() {});

        return receivedTokens;
    }


}
