package dk.dtu.businesslogic.services;

import dk.dtu.businesslogic.repositories.AccountRepository;
import dk.dtu.businesslogic.models.Customer;
import dk.dtu.businesslogic.models.Merchant;
import messaging.Event;
import messaging.MessageQueue;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author  Andrei Soldan 243873
 */
public class AccountService {

	private MessageQueue queue;
	private AccountRepository accountRepository;

	private static final Logger logger = Logger.getLogger(AccountService.class.getName());

	public AccountService(MessageQueue q) {
		this.queue = q;
		queue.addHandler("CustomerAccountRegistrationRequested", this::policyCustomerRegistrationRequested);
		queue.addHandler("MerchantAccountRegistrationRequested", this::policyMerchantRegistrationRequested);

		queue.addHandler("TokenValidated", this::tokenValidated);
		queue.addHandler("PaymentRequested", this::paymentRequested);

		accountRepository = new AccountRepository();
	}
	
	/* Policies */


	/**
	 * Handles the 'CustomerAccountRegistrationRequested' event
	 * @param event the event
	 */
	public void policyCustomerRegistrationRequested(Event event) {
		var customer = event.getArgument(0, Customer.class);
		createCustomerAccount(customer);
	}

	/**
	 * Handles the 'MerchantAccountRegistrationRequested' event
	 * @param event the event
	 */
	public void policyMerchantRegistrationRequested(Event event) {
		var merchant = event.getArgument(0, Merchant.class);
		createMerchantAccount(merchant);
	}


	/**
	 * Handles the 'TokenValidated' event
	 * @param event the event
	 */
	public void tokenValidated(Event event) {
		UUID correlationId = event.getArgument(0, UUID.class);
		var customerId = event.getArgument(1, String.class);
		retrieveCustomerBankAccount(customerId, correlationId);
	}


	/**
	 * Handles the 'PaymentRequested' event
	 * @param event the event
	 */
	private void paymentRequested(Event event) {
		UUID correlationId = event.getArgument(0, UUID.class);
		var merchantId = event.getArgument(2, String.class);
		retrieveMerchantBankAccount(merchantId, correlationId);
	}

	/* Commands */

	/**
	 * Store the given customer with a new ID and publish a 'CustomerRegistered' event
	 * @param customer the customer
	 */
	private void createCustomerAccount(Customer customer) {
		String cid = UUID.randomUUID().toString();
		customer.setId(cid);
		accountRepository.addCustomer(customer);
		queue.publish(new Event("CustomerRegistered", cid));
	}


	/**
	 * Store the given merchant with a new ID and publish a 'MerchantRegistered' event
	 * @param merchant the customer
	 */
	private void createMerchantAccount(Merchant merchant) {
		String cid = UUID.randomUUID().toString();
		merchant.setId(cid);
		accountRepository.addMerchant(merchant);
		queue.publish(new Event("MerchantRegistered", cid));
	}

	/**
	 * Gets a stored customer by the given ID, if succeeds publish a 'CustomerBankAccountRetrieved' event
	 * @param customerId the customer ID
	 * @param correlationId the correlation ID
	 * @throws RuntimeException if there is no customer with the given ID
	 */
	private void retrieveCustomerBankAccount(String customerId, UUID correlationId) {
		var customerAccount = accountRepository.getCustomerById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		queue.publish(new Event("CustomerBankAccountRetrieved", correlationId ,customerAccount.getBankAccountNumber()));
	}

	/**
	 * Gets a stored merchant by the given ID, if succeeds publish a 'MerchantBankAccountRetrieved' event
	 * @param merchantId the customer ID
	 * @param correlationId the correlation ID
	 * @throws RuntimeException if there is no merchant with the given ID
	 */
	private void retrieveMerchantBankAccount(String merchantId, UUID correlationId) {
		var merchantAccount = accountRepository.getMerchantById(merchantId)
				.orElseThrow(() -> new RuntimeException("Merchant not found"));
		queue.publish(new Event("MerchantBankAccountRetrieved",correlationId, merchantAccount.getBankAccountNumber()));
	}
}
