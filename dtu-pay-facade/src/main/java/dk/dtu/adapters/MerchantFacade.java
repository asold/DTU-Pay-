package dk.dtu.adapters;

import dk.dtu.core.exceptions.AccountRegistrationException;
import dk.dtu.core.models.RegisterMerchantRequest;
import jakarta.inject.Singleton;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Singleton
public class MerchantFacade {

    private final Map<CorrelationId, CompletableFuture<String>> merchantRequests = new ConcurrentHashMap<>();

    MessageQueue queue;

    public MerchantFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public MerchantFacade(MessageQueue q) {
        queue = q;
        q.addHandler("MerchantRegistered", this::policyMerchantRegistered);
        q.addHandler("MerchantDeregistered", this::policyMerchantDeregistered);
        q.addHandler("MerchantRegistrationFailed", this::policyMerchantRegistrationFailed);
    }

    private void policyMerchantRegistrationFailed(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        String error = event.getArgument(1, String.class);
        CompletableFuture<String> future = merchantRequests.getOrDefault(correlationId, null);
        if (future != null) {
            future.completeExceptionally(new AccountRegistrationException(error));
            merchantRequests.remove(correlationId);
        }
    }

    private void policyMerchantDeregistered(Event event) {
        String merchantId = event.getArgument(1, String.class);
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        merchantRequests.get(correlationId).complete(merchantId);
        merchantRequests.remove(correlationId);
    }

    private void policyMerchantRegistered(Event e) {
        String merchantId = e.getArgument(1, String.class);
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        merchantRequests.get(correlationId).complete(merchantId);
        merchantRequests.remove(correlationId);
    }

    public String registerMerchant(RegisterMerchantRequest merchant) throws InterruptedException, ExecutionException, AccountRegistrationException {
        CorrelationId correlationId = new CorrelationId();
        merchantRequests.put(correlationId, new CompletableFuture<>());
        queue.publish(new Event("MerchantAccountRegistrationRequested", correlationId, merchant));
        return merchantRequests.get(correlationId).get();
    }

    public String deregisterMerchant(String merchantId) throws InterruptedException, ExecutionException {
        CorrelationId correlationId = new CorrelationId();
        merchantRequests.put(correlationId, new CompletableFuture<>());
        queue.publish(new Event("MerchantAccountDeregistrationRequested", correlationId, merchantId));
        return merchantRequests.get(correlationId).get();
    }

    public Map<CorrelationId, CompletableFuture<String>> getFutures() {
        return this.merchantRequests;
    }
}
