package dk.dtu.adapters;

import dk.dtu.core.models.Payment;
import dk.dtu.core.models.PaymentResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

/**
 * @author Andrei Soldan s243873
 */
public final class PaymentAdapter {

    private Client client = ClientBuilder.newBuilder().build();

    public PaymentResponse requestPayment(Payment payment) throws Exception {
        Response response = client.target("http://localhost:8082/payments")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(payment));

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(PaymentResponse.class);
    }
}
