package dk.dtu.adapters;

import dk.dtu.core.models.Customer;
import dk.dtu.core.models.Merchant;
import jakarta.inject.Singleton;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
@Singleton
public class MerchantFacade {
    CompletableFuture<String> merchantId;

    MessageQueue queue;

    public MerchantFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public MerchantFacade(MessageQueue q) {
        queue = q;
        q.addHandler("MerchantRegistered", this::policyMerchantRegistered);
    }

    private void policyMerchantRegistered(Event e) {
        merchantId.complete(e.getArgument(0, String.class));
    }

    public String registerMerchant(Merchant merchant) throws InterruptedException, ExecutionException {
        merchantId = new CompletableFuture<>();
        queue.publish(new Event("MerchantAccountRegistrationRequested", merchant));
        return merchantId.get();
    }
}
