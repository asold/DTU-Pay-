package dk.dtu.adapters;

import dk.dtu.core.models.Customer;
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
 * @author Andrei Soldan s243873
 */
@Singleton
public class CustomerFacade {

    private Map<CorrelationId, CompletableFuture<String>> registerCustomerRequests = new ConcurrentHashMap<>();

    MessageQueue queue;

    public CustomerFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public CustomerFacade(MessageQueue q) {
        queue = q;
        q.addHandler("CustomerRegistered", this::policyCustomerRegistered);
    }

    private void policyCustomerRegistered(Event e) {
        System.out.println("Policy customer registered");
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        String customerId = e.getArgument(1, String.class);
        registerCustomerRequests.get(correlationId).complete(customerId);
    }

    public String registerCustomer(Customer customer) throws InterruptedException, ExecutionException {
        CorrelationId correlationId = new CorrelationId();
        CompletableFuture<String> registerCustomerRequest = new CompletableFuture<>();
        registerCustomerRequests.put(correlationId, registerCustomerRequest);
        queue.publish(new Event("CustomerAccountRegistrationRequested",correlationId, customer));
        return registerCustomerRequest.get();
    }
}
