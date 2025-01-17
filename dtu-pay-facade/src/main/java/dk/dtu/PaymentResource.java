package dk.dtu;

import dk.dtu.adapters.PaymentFacade;
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

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    private PaymentFacade paymentFacade;


    @POST
    public Response requestPayment(Payment payment) throws ExecutionException, InterruptedException {
        try{
            PaymentResponse response = paymentFacade.requestPayment(payment);
            return Response.ok(response).build();
        }catch(InvalidTokenException e){
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }catch(ExecutionException | InterruptedException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }

}
