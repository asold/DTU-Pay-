package dk.dtu;

import dk.dtu.adapters.TokenFacade;
import dk.dtu.core.exceptions.TokenServiceException;
import dk.dtu.core.models.TokenResult;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TokenResource {

    @Inject
    TokenFacade tokenFacade;

    @GET
    @Path("/customer/{id}")
    public Response getTokens(
            @PathParam("id") String customerId,
            @QueryParam("amount") @NotNull(message = "The amount is required") int amount) throws TokenServiceException, ExecutionException, InterruptedException {
        List<TokenResult> customerTokens = tokenFacade.getTokens(customerId, amount);
        return Response.ok(customerTokens).build();
    }
}