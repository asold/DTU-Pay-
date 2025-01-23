package dk.dtu;

import dk.dtu.adapters.MerchantFacade;
import dk.dtu.core.exceptions.AccountRegistrationException;
import dk.dtu.core.models.RegisterMerchantRequest;
import dk.dtu.core.models.Customer;
import dk.dtu.core.models.Merchant;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@Path("/merchants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MerchantResource {

    @Inject
    MerchantFacade merchantFacade;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @APIResponse(responseCode = "200", description = "Merchant Registered Successfully", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    @APIResponse(responseCode = "400", description = "Error registering the merchant due to invalid input values", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    @APIResponse(responseCode = "500", description = "Unknown error while registering the merchant", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    public Response registerCustomer(RegisterMerchantRequest merchant) {

        try {
            String merchantId = merchantFacade.registerMerchant(merchant);
            return Response.created(new URI("/merchants/" + merchantId)).entity(merchantId).build();
        } catch (AccountRegistrationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (URISyntaxException | ExecutionException | InterruptedException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

    }

    @DELETE
    @Path("{id}")
    @APIResponse(responseCode = "200", description = "Merchant Deregistered Successfully", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    @APIResponse(responseCode = "500", description = "Unknown error while deregistering the merchant", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    public Response deregisterCustomer(@PathParam("id") String id) throws URISyntaxException, ExecutionException, InterruptedException {
        String merchantId = merchantFacade.deregisterMerchant(id);
        return Response.created(new URI("/merchants/" + merchantId)).entity(merchantId).build();
    }


}
