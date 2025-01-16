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
 * Driven adapter responsible for abstracting inter-process communication related to any payment use-cases and scenarios.
 */
public final class PaymentAdapter {

    private Client client = ClientBuilder.newBuilder().build();


    public PaymentResponse requestPayment(Payment payment) {
        Response response = client.target("http://localhost:8082/payments")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(payment));

        return response.readEntity(PaymentResponse.class);
    }
}
