package dk.dtu;

import dk.dtu.adapters.CustomerFacade;
import dk.dtu.core.models.Customer;
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

/**
 * @author Andrei Soldan s243873
 */
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    private CustomerFacade customerFacade;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCustomer(Customer customer) throws URISyntaxException, ExecutionException, InterruptedException {

        String customerId = customerFacade.registerCustomer(customer);
        return Response.created(new URI("/customers/" + customerId)).entity(customerId).build();
    }

}
