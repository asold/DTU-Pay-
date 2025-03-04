package dk.dtu.adapters;

import dk.dtu.core.models.Merchant;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 */
public final class MerchantAdapter {

    private final Client client = ClientBuilder.newBuilder().build();

    public String register(Merchant dtuPayMerchant) throws Exception {
        Response response = client.target("http://localhost:8082/merchants")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(dtuPayMerchant));

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(String.class);
    }

    public String deregister(String  id) {
        Response response = client.target("http://localhost:8082/merchants/" + id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.readEntity(String.class);
    }
}
