package dk.dtu;

import dk.dtu.adapters.TokenFacade;
import dk.dtu.core.models.TokenResult;
import jakarta.inject.Inject;
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
    private TokenFacade tokenFacade;

    @GET
    @Path("{id}")
    public Response getTokens(@PathParam("id") String id, @QueryParam("amount") int amount) throws ExecutionException, InterruptedException {
        List<TokenResult> customerTokens = tokenFacade.getTokens(id, amount);
        System.out.println("customerTokens: " + customerTokens);
        System.out.println("customerTokens: " + customerTokens.getClass());
        for (Object token : customerTokens) {
            System.out.println("Element type: " + token.getClass());
        }
        return Response.ok(customerTokens).build();
    }
}