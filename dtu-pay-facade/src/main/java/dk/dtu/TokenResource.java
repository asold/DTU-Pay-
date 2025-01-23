package dk.dtu;

import dk.dtu.adapters.TokenFacade;
import dk.dtu.core.exceptions.TokenServiceException;
import dk.dtu.core.models.TokenResult;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * RESTful resource for managing customer tokens.
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
    @APIResponse(responseCode = "200", description = "Tokens Retrieved Successfully", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = TokenResult.class)))
    @APIResponse(responseCode = "400", description = "The number of tokens requested is not between 1 and 5 or Invalid Customer Account or The number of tokens requested exceeds customer limit ", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    @APIResponse(responseCode = "500", description = "Unknown error while retrieving the tokens", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    public Response getTokens(
            @PathParam("id") String customerId,
            @QueryParam("amount") @NotNull(message = "The amount is required") int amount)
            throws TokenServiceException, ExecutionException, InterruptedException {

        List<TokenResult> customerTokens = tokenFacade.getTokens(customerId, amount);

        return Response.ok(customerTokens).build();
    }
}