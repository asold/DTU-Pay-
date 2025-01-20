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

    public List<TokenResult> getCustomerReport(String id) {
        Response response = client.target("http://localhost:8082/Reports/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<TokenResult> receivedTokens = response.readEntity(new GenericType<List<TokenResult>>() {});

        return receivedTokens;
    }

}
