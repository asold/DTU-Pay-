package dk.dtu.adapters;

/*
 * @author Karrar Adam s230432
 */

import dk.dtu.core.models.PaymentLog;
import dk.dtu.core.models.TokenResult;
//import io.cucumber.messages.internal.com.google.common.reflect.TypeToken;
import io.cucumber.messages.internal.com.google.common.reflect.TypeToken;
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

public class ReportFacade {



        private Map<CorrelationId, CompletableFuture<List<PaymentLog>>> ReportRequests = new ConcurrentHashMap<>();

        private final MessageQueue queue;

        public ReportFacade() {
            this(new RabbitMqQueue("rabbitMq"));
        }

        public ReportFacade(RabbitMqQueue q) {
            queue = q;
            q.addHandler("ReportGenerated", this::policyReportGenerated);
        }

        private void policyReportGenerated(Event e){
            // This is done for serialization
            Type listType = new TypeToken<List<PaymentLog>>() {}.getType();
            List<PaymentLog> paymentLog = e.getArgument(1, listType);
            CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
            CompletableFuture<List<PaymentLog>> future = ReportRequests.get(correlationId);
            future.complete(paymentLog);
            ReportRequests.remove(correlationId);
        }

        public List<PaymentLog> getPaymentLog(String id) throws ExecutionException, InterruptedException {
            CorrelationId correlationId = new CorrelationId();
            CompletableFuture<List<PaymentLog>> paymentLogs = new CompletableFuture<>();
            ReportRequests.put(correlationId, paymentLogs);
            queue.publish(new Event("ReportRequested",correlationId, id ));
            return paymentLogs.get();
        }
}

