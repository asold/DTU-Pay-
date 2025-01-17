package dk.dtu.adapters;

import dk.dtu.core.models.Customer;
import dk.dtu.core.models.Merchant;
import jakarta.inject.Singleton;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
@Singleton
public class MerchantFacade {
    private Map<CorrelationId, CompletableFuture<String>> registerMerchantRequests = new ConcurrentHashMap<>();

    MessageQueue queue;

    public MerchantFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public MerchantFacade(MessageQueue q) {
        queue = q;
        q.addHandler("MerchantRegistered", this::policyMerchantRegistered);
    }

    private void policyMerchantRegistered(Event e) {
        String merchantId = e.getArgument(1, String.class);
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);

        registerMerchantRequests.get(correlationId).complete(merchantId);
        registerMerchantRequests.remove(correlationId);
    }

    public String registerMerchant(Merchant merchant) throws InterruptedException, ExecutionException {
        CorrelationId correlationId = new CorrelationId();
        registerMerchantRequests.put(correlationId, new CompletableFuture<>());
        queue.publish(new Event("MerchantAccountRegistrationRequested",correlationId, merchant));
        return registerMerchantRequests.get(correlationId).get();
    }
}
