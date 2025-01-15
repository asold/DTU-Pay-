package dk.dtu;

import dk.dtu.core.models.Customer;
import dk.dtu.core.services.CustomerService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerController {

    private CustomerService customerService = new CustomerService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCustomer(Customer customer) throws URISyntaxException {
        String customerId = customerService.registerCustomer(customer);
        return Response.created(new URI("/customers/" + customerId)).entity(customerId).build();
    }

}
