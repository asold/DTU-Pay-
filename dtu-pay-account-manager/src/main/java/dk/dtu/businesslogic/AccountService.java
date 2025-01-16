package dk.dtu.businesslogic;

import dk.dtu.models.Merchant;
import messaging.Event;
import messaging.MessageQueue;

import java.util.*;

/**
 * @author  Andrei Soldan 243873
 */
public class AccountService {

	private MessageQueue queue;
	private AccountRepository accountRepository;

	public AccountService(MessageQueue q) {
		this.queue = q;
		queue.addHandler("CustomerAccountRegistrationRequested", this::policyCustomerRegistrationRequested);
		queue.addHandler("MerchantAccountRegistrationRequested", this::policyMerchantRegistrationRequested);
		accountRepository = new AccountRepository();
	}
	
	/* Policies */

	public void policyCustomerRegistrationRequested(Event event) {
		System.out.println("Handling the customer reg event");
		var customer = event.getArgument(0, Customer.class);
		createCustomerAccount(customer);
	}

	public void policyMerchantRegistrationRequested(Event event) {
		var merchant = event.getArgument(0, Merchant.class);
		createMerchantAccount(merchant);
	}

	/* Commands */

	public void createCustomerAccount(Customer customer) {
		String cid = UUID.randomUUID().toString();
		customer.setId(cid);
		System.out.println("Trying to save the customer");
		accountRepository.addCustomer(customer);
		System.out.println("Customer saved, publishing back");
		queue.publish(new Event("CustomerRegistered", cid));
	}

	public void createMerchantAccount(Merchant merchant) {
		String cid = UUID.randomUUID().toString();
		merchant.setId(cid);
		accountRepository.addMerchant(merchant);
		queue.publish(new Event("MerchantRegistered", cid));
	}

}
