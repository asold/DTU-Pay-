package dk.dtu;

import dk.dtu.adapters.CustomerFacade;
import dk.dtu.adapters.MerchantFacade;
import dk.dtu.core.models.Customer;
import dk.dtu.core.models.Merchant;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
    public Response registerCustomer(Merchant merchant) throws URISyntaxException, ExecutionException, InterruptedException {

        String merchantId = merchantFacade.registerMerchant(merchant);
        return Response.created(new URI("/customers/" + merchantId)).entity(merchantId).build();
    }
}
