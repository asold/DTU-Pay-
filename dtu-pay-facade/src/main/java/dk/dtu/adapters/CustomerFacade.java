package dk.dtu.adapters;

import dk.dtu.core.exceptions.AccountRegistrationException;
import dk.dtu.core.models.RegisterCustomerRequest;
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

    private final Map<CorrelationId, CompletableFuture<String>> customerRequests = new ConcurrentHashMap<>();

    MessageQueue queue;

    public CustomerFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public CustomerFacade(MessageQueue q) {
        queue = q;
        q.addHandler("CustomerRegistered", this::policyCustomerRegistered);
        q.addHandler("CustomerDeregistered", this::policyCustomerDeregistered);
        q.addHandler("CustomerRegistrationFailed", this::policyCustomerRegistrationFailed);
    }

    private void policyCustomerRegistered(Event e) {
        System.out.println("Policy customer registered");
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        String customerId = e.getArgument(1, String.class);
        customerRequests.get(correlationId).complete(customerId);
        customerRequests.remove(correlationId);
    }

    private void policyCustomerRegistrationFailed(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        String error = e.getArgument(1, String.class);
        System.out.println("Policy customer registration failed: " + error);
        CompletableFuture<String> future = customerRequests.getOrDefault(correlationId, null);
        if (future != null) {
            future.completeExceptionally(new AccountRegistrationException(error));
            customerRequests.remove(correlationId);
        }
    }

    private void policyCustomerDeregistered(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        String customerId = e.getArgument(1, String.class);
        customerRequests.get(correlationId).complete(customerId);
        customerRequests.remove(correlationId);
    }

    public String registerCustomer(RegisterCustomerRequest customer) throws InterruptedException, ExecutionException, AccountRegistrationException {
        CorrelationId correlationId = new CorrelationId();
        CompletableFuture<String> registerCustomerRequest = new CompletableFuture<>();
        customerRequests.put(correlationId, registerCustomerRequest);
        queue.publish(new Event("CustomerAccountRegistrationRequested", correlationId, customer));
        return registerCustomerRequest.get();
    }

    public String deregisterCustomer(String customerId) throws InterruptedException, ExecutionException {
        CorrelationId correlationId = new CorrelationId();
        CompletableFuture<String> deregisterCustomerRequest = new CompletableFuture<>();
        customerRequests.put(correlationId, deregisterCustomerRequest);
        queue.publish(new Event("CustomerAccountDeregistrationRequested", correlationId, customerId));
        return deregisterCustomerRequest.get();
    }
}
