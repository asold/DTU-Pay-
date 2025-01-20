package dk.dtu;

import dk.dtu.adapters.ReportFacade;
import dk.dtu.core.models.PaymentLog;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/Reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {

    @Inject
    private ReportFacade reportFacade;

    @GET
    @Path("{id}")
    public Response getCustomerReport(@PathParam("id") String id) throws ExecutionException, InterruptedException {
        List<PaymentLog> customerReport = reportFacade.getCustomerPaymentLogs(id);
        return Response.ok(customerReport).build();
    }

    @GET
    @Path("{id}")
    public Response getMerchantReport(@PathParam("id") String id) throws ExecutionException, InterruptedException {
        List<PaymentLog> merchantReport = reportFacade.getMerchantPaymentLogs(id);
        return Response.ok(merchantReport).build();
    }

    @GET
    public Response getManagerReport() throws ExecutionException, InterruptedException {
        List<PaymentLog> customerReport = reportFacade.getManagerPaymentLogs();
        return Response.ok(customerReport).build();
    }

}
