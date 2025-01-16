package dk.dtu.adapters;

import dk.dtu.core.models.Payment;
import dk.dtu.core.models.PaymentResponse;
import io.netty.util.concurrent.CompleteFuture;
import jakarta.inject.Singleton;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Singleton
public class PaymentFacade {

    private final MessageQueue queue;
//    private CompletableFuture<PaymentResponse> paymentResponse;
    private CompletableFuture<PaymentResponse> paymentResponse;

    public PaymentFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public PaymentFacade(MessageQueue q) {
        queue = q;
        q.addHandler("PaymentProcessed", this::policyPaymentProcessed);
    }

    private void policyPaymentProcessed(Event event) {
        event.
        paymentResponse.complete(event.getArgument(0, PaymentResponse.class));
    }


    public PaymentResponse requestPayment(Payment payment) throws ExecutionException, InterruptedException {
        paymentResponse = new CompletableFuture<>();
        //Create a new PaymentID / Correlation ID
        UUID correlationId = UUID.randomUUID();
        queue.publish(new Event("PaymentRequested",
                correlationId ,
                payment.getToken(),
                payment.getMerchantId(),
                payment.getAmount()));
        return paymentResponse.get();
    }

}
