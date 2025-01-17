package dk.dtu.adapters;

import dk.dtu.core.models.Payment;
import dk.dtu.core.models.PaymentResponse;
import io.netty.util.concurrent.CompleteFuture;
import jakarta.inject.Singleton;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Singleton
public class PaymentFacade {

    private final MessageQueue queue;
    private Map<CorrelationId, CompletableFuture<PaymentResponse>> paymentRequests = new ConcurrentHashMap<>();

    public PaymentFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public PaymentFacade(MessageQueue q) {
        queue = q;
        q.addHandler("PaymentProcessed", this::policyPaymentProcessed);
        q.addHandler("TokenValidationFailed", this::policyTokenValidationFailed);
    }

    private void policyPaymentProcessed(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        PaymentResponse paymentResponse = event.getArgument(1, PaymentResponse.class);

        CompletableFuture<PaymentResponse> future = paymentRequests.get(correlationId);
        future.complete(paymentResponse);
        paymentRequests.remove(correlationId);
    }

    private void policyTokenValidationFailed(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String message = event.getArgument(1, String.class);

        CompletableFuture<PaymentResponse> future = paymentRequests.get(correlationId);
        future.completeExceptionally(new RuntimeException(message));
        paymentRequests.remove(correlationId);
    }


    public PaymentResponse requestPayment(Payment payment) throws ExecutionException, InterruptedException {
        CorrelationId correlationId = new CorrelationId();
        CompletableFuture<PaymentResponse> paymentResponse = new CompletableFuture<>();

        paymentRequests.put(correlationId, paymentResponse);
        //Create a new PaymentID / Correlation ID
        queue.publish(new Event("PaymentRequested",
                correlationId ,
                payment.getToken(),
                payment.getMerchantId(),
                payment.getAmount()));
        return paymentResponse.get();
    }

}
