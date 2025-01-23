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

/**
 * RESTful resource for managing customer tokens.
 *
 * This class provides an endpoint to retrieve tokens for a specific customer.
 * It uses dependency injection to interact with the token business logic.
 * @author Maxim Zavidei
 */
@Path("/tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TokenResource {

    /**
     * Facade for token-related operations.
     * Injected by the dependency injection framework.
     */
    @Inject
    TokenFacade tokenFacade;

    /**
     * Retrieves a specified number of tokens for a given customer.
     *
     * @param customerId The unique identifier of the customer
     * @param amount The number of tokens to retrieve
     * @return A response containing the list of tokens
     * @throws TokenServiceException If there's an error in token service
     * @throws ExecutionException If an execution error occurs
     * @throws InterruptedException If the token retrieval is interrupted
     */
    @GET
    @Path("/customer/{id}")
    public Response getTokens(
            @PathParam("id") String customerId,
            @QueryParam("amount") @NotNull(message = "The amount is required") int amount)
            throws TokenServiceException, ExecutionException, InterruptedException {

        List<TokenResult> customerTokens = tokenFacade.getTokens(customerId, amount);

        return Response.ok(customerTokens).build();
    }
}