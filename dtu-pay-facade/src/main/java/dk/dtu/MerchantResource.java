package dk.dtu;

import dk.dtu.adapters.MerchantFacade;
import dk.dtu.core.exceptions.AccountRegistrationException;
import dk.dtu.core.models.RegisterMerchantRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@Path("/merchants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MerchantResource {

    @Inject
    private MerchantFacade merchantFacade;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
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
    public Response deregisterCustomer(@PathParam("id") String id) throws URISyntaxException, ExecutionException, InterruptedException {

        String merchantId = merchantFacade.deregisterMerchant(id);
        return Response.created(new URI("/merchants/" + merchantId)).entity(merchantId).build();
    }


}
