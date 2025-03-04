package dk.dtu.adapters;


/**
 * @author Jeppe Jensen 233488
 */
import dk.dtu.core.models.PaymentLog;
import com.google.gson.reflect.TypeToken;
import jakarta.inject.Singleton;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;


@Singleton
public class ReportFacade {

        private final Map<CorrelationId, CompletableFuture<List<PaymentLog>>> reportRequests = new ConcurrentHashMap<>();

        private final MessageQueue queue;

        public ReportFacade() {
            this(new RabbitMqQueue("rabbitMq"));
        }

        public ReportFacade(MessageQueue q) {
            queue = q;
            q.addHandler("ReportGenerated", this::policyReportGenerated);
        }

        private void policyReportGenerated(Event e){
            // This is done for serialization
            Type listType = new TypeToken<List<PaymentLog>>() {}.getType();
            List<PaymentLog> paymentLogs = e.getArgument(1, listType);
            CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
            CompletableFuture<List<PaymentLog>> future = reportRequests.get(correlationId);
            future.complete(paymentLogs);
            reportRequests.remove(correlationId);
        }

        public List<PaymentLog> getCustomerPaymentLogs(String id) throws ExecutionException, InterruptedException {
            CorrelationId correlationId = new CorrelationId();
            CompletableFuture<List<PaymentLog>> paymentLogs = new CompletableFuture<>();
            reportRequests.put(correlationId, paymentLogs);
            queue.publish(new Event("CustomerReportRequested", correlationId, id));
            return paymentLogs.get();
        }

        public List<PaymentLog> getMerchantPaymentLogs(String id) throws ExecutionException, InterruptedException {
            CorrelationId correlationId = new CorrelationId();
            CompletableFuture<List<PaymentLog>> paymentLogs = new CompletableFuture<>();
            reportRequests.put(correlationId, paymentLogs);
            queue.publish(new Event("MerchantReportRequested", correlationId, id));
            return paymentLogs.get();
        }

        public List<PaymentLog> getManagerPaymentLogs() throws ExecutionException, InterruptedException {
            CorrelationId correlationId = new CorrelationId();
            CompletableFuture<List<PaymentLog>> paymentLogs = new CompletableFuture<>();
            reportRequests.put(correlationId, paymentLogs);
            queue.publish(new Event("ManagerReportRequested", correlationId));
            return paymentLogs.get();
        }

        public Map<CorrelationId, CompletableFuture<List<PaymentLog>>> getReportRequests() {
            return reportRequests;
        }
}

