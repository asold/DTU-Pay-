package dk.dtu;

import dk.dtu.adapters.ReportFacade;
import dk.dtu.core.models.PaymentLog;
import jakarta.inject.Inject;
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
 * @author Karrar Adam s230432
 */

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {

    @Inject
    private ReportFacade reportFacade;

    @GET
    @Path("customer/{id}")
    @APIResponse(responseCode = "200", description = "Report retrieved successfully", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = PaymentLog.class)))
    @APIResponse(responseCode = "500", description = "Unknown error while retrieving the report", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    public Response getCustomerReport(@PathParam("id") String id) throws ExecutionException, InterruptedException {
        List<PaymentLog> customerReport = reportFacade.getCustomerPaymentLogs(id);
        return Response.ok(customerReport).build();
    }

    @GET
    @Path("merchant/{id}")
    @APIResponse(responseCode = "200", description = "Report retrieved successfully", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = PaymentLog.class)))
    @APIResponse(responseCode = "500", description = "Unknown error while retrieving the report", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    public Response getMerchantReport(@PathParam("id") String id) throws ExecutionException, InterruptedException {
        List<PaymentLog> merchantReport = reportFacade.getMerchantPaymentLogs(id);
        return Response.ok(merchantReport).build();
    }

    @GET
    @Path("manager")
    @APIResponse(responseCode = "200", description = "Report retrieved successfully", content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = PaymentLog.class)))
    @APIResponse(responseCode = "500", description = "Unknown error while retrieving the report", content = @Content(schema = @Schema(type = SchemaType.STRING)))
    public Response getManagerReport() throws ExecutionException, InterruptedException {
        List<PaymentLog> customerReport = reportFacade.getManagerPaymentLogs();
        return Response.ok(customerReport).build();
    }



}
