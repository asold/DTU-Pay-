package dk.dtu.adapters;

import dk.dtu.core.models.PaymentLog;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

public class ReportAdapter {

    private final Client client = ClientBuilder.newBuilder().build();

    /** Author Karrar Adam s230432
     */

    public List<PaymentLog> getCustomerReport(String id) {
        Response response = client.target("http://localhost:8082/reports/customer/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<PaymentLog> receivedPaymentLogs = response.readEntity(new GenericType<List<PaymentLog>>() {});

        return receivedPaymentLogs;
    }
    public List<PaymentLog> getMerchantReport(String id) {
        Response response = client.target("http://localhost:8082/reports/merchant/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<PaymentLog> receivedPaymentLogs = response.readEntity(new GenericType<List<PaymentLog>>() {});

        return receivedPaymentLogs;
    }

    public List<PaymentLog> getManagerReport() {
        Response response = client.target("http://localhost:8082/reports/manager" )
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<PaymentLog> receivedPaymentLogs = response.readEntity(new GenericType<List<PaymentLog>>() {});

        return receivedPaymentLogs;
    }

}
