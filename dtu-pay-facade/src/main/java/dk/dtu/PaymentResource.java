package dk.dtu;

import dk.dtu.adapters.PaymentFacade;
import dk.dtu.core.exceptions.AccountNotFoundException;
import dk.dtu.core.exceptions.InvalidPaymentException;
import dk.dtu.core.exceptions.InvalidTokenException;
import dk.dtu.core.models.Payment;
import dk.dtu.core.models.PaymentResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.concurrent.ExecutionException;
/**
 * @author  Andrei Soldan 243873
 */
@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    private PaymentFacade paymentFacade;


    @POST
    public Response requestPayment(Payment payment) throws ExecutionException, InterruptedException {
        try {
            PaymentResponse response = paymentFacade.requestPayment(payment);
            return Response.ok(response).build();
        } catch (ExecutionException e) {
            // Unwrap the cause of the exception
            Throwable cause = e.getCause();
            if (cause instanceof InvalidTokenException) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(cause.getMessage()).build();
            } else if (cause instanceof InvalidPaymentException) {
                return Response.status(Response.Status.BAD_REQUEST).entity(cause.getMessage()).build();
            }else if (cause instanceof AccountNotFoundException) {
                return Response.status(Response.Status.NOT_FOUND).entity(cause.getMessage()).build();
            }
            else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(cause.getMessage()).build();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }

}
