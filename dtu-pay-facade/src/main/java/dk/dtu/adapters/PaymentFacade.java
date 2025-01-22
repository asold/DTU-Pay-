package dk.dtu.adapters;

import dk.dtu.core.exceptions.AccountNotFoundException;
import dk.dtu.core.exceptions.InvalidPaymentException;
import dk.dtu.core.exceptions.InvalidTokenException;
import dk.dtu.core.models.Payment;
import dk.dtu.core.models.PaymentResponse;
import jakarta.inject.Singleton;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
/**
 * @author  Andrei Soldan 243873
 */
@Singleton
public class PaymentFacade {

    private final MessageQueue queue;
    private final Map<CorrelationId, CompletableFuture<PaymentResponse>> paymentRequests = new ConcurrentHashMap<>();

    public PaymentFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public PaymentFacade(MessageQueue q) {
        queue = q;
        q.addHandler("PaymentProcessed", this::policyPaymentProcessed);
        q.addHandler("TokenValidationFailed", this::policyTokenValidationFailed);
        q.addHandler("DebtorAccountNotFound", this::policyDebtorAccountNotFound);
        q.addHandler("CreditorAccountNotFound", this::policyCreditorAccountNotFound);
        q.addHandler("NegativeAmountRequested", this::policyNegativeAmountRequested);
        q.addHandler("MerchantAccountNotFound", this::policyMerchantNotFound);
    }

    public void policyPaymentProcessed(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        PaymentResponse paymentResponse = event.getArgument(1, PaymentResponse.class);

        CompletableFuture<PaymentResponse> future = paymentRequests.get(correlationId);
        future.complete(paymentResponse);
        paymentRequests.remove(correlationId);
    }

    public void policyTokenValidationFailed(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String message = event.getArgument(1, String.class);
        CompletableFuture<PaymentResponse> future = paymentRequests.getOrDefault(correlationId, null);
        if (future != null) {
            future.completeExceptionally(new InvalidTokenException(message));
            paymentRequests.remove(correlationId);
        }
    }

    public void policyDebtorAccountNotFound(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String message = event.getArgument(1, String.class);
        CompletableFuture<PaymentResponse> future = paymentRequests.getOrDefault(correlationId, null);
        if (future != null) {
            future.completeExceptionally(new InvalidPaymentException(message));
            paymentRequests.remove(correlationId);
        }
    }

    public void policyCreditorAccountNotFound(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String message = event.getArgument(1, String.class);
        CompletableFuture<PaymentResponse> future = paymentRequests.getOrDefault(correlationId, null);
        if (future != null) {
            future.completeExceptionally(new InvalidPaymentException(message));
            paymentRequests.remove(correlationId);
        }
    }

    public void policyNegativeAmountRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String message = event.getArgument(1, String.class);
        CompletableFuture<PaymentResponse> future = paymentRequests.getOrDefault(correlationId, null);
        if (future != null) {
            future.completeExceptionally(new InvalidPaymentException(message));
            paymentRequests.remove(correlationId);
        }
    }

    public void policyMerchantNotFound(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String message = event.getArgument(1, String.class);
        CompletableFuture<PaymentResponse> future = paymentRequests.getOrDefault(correlationId, null);
        if (future != null) {
            future.completeExceptionally(new AccountNotFoundException(message));
            paymentRequests.remove(correlationId);
        }
    }


    public PaymentResponse requestPayment(Payment payment) throws ExecutionException, InterruptedException {
        CorrelationId correlationId = new CorrelationId();
        CompletableFuture<PaymentResponse> paymentResponse = new CompletableFuture<>();

        paymentRequests.put(correlationId, paymentResponse);
        queue.publish(new Event("PaymentRequested",
                correlationId,
                payment.getToken(),
                payment.getMerchantId(),
                payment.getAmount()));
        return paymentResponse.get();
    }

    public Map<CorrelationId, CompletableFuture<PaymentResponse>> getPaymentRequests() {
        return paymentRequests;
    }

}
