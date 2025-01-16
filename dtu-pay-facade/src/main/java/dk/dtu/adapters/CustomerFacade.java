package dk.dtu.adapters;

import dk.dtu.core.models.Customer;
import jakarta.inject.Singleton;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Andrei Soldan s243873
 */
@Singleton
public class CustomerFacade {

    CompletableFuture<String> customerId;

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
        customerId.complete(e.getArgument(0, String.class));
    }

    public String registerCustomer(Customer customer) throws InterruptedException, ExecutionException {
        customerId = new CompletableFuture<>();
        queue.publish(new Event("CustomerAccountRegistrationRequested", customer));
        System.out.println("Publishing new event for customer reg.");
        return customerId.get();
    }
}
