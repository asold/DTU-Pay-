package dk.dtu;

import dk.dtu.adapters.PaymentFacade;
import dk.dtu.core.models.Payment;
import dk.dtu.core.models.PaymentResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    private PaymentFacade paymentFacade;


    @POST
    public void requestPayment(Payment payment) throws ExecutionException, InterruptedException {
        PaymentResponse response = paymentFacade.requestPayment(payment);
    }

}
