package dk.dtu.businesslogic;

import messaging.Event;
import messaging.MessageQueue;

import java.util.*;

public class AccountService {

	private MessageQueue queue;
	private Set<Customer> customers = new HashSet<>();

	public AccountService(MessageQueue q) {
		this.queue = q;
		queue.addHandler("CustomerAccountRegistrationRequested", this::policyCustomerRegistrationRequested);
	}
	
	/* Policies */
	
	public void policyCustomerRegistrationRequested(Event event) {
		var customer = event.getArgument(0, Customer.class);
		createCustomer(customer);
	}

	/* Commands */

	public void createCustomer(Customer customer) {
		String cid = UUID.randomUUID().toString();
		customer.setId(cid);
		customers.add(customer);
		queue.publish(new Event("CustomerRegistered", cid));
	}

}
