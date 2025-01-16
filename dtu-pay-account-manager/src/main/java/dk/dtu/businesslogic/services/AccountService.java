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
		logger.info("Handling the customer account registration event");
		var customer = event.getArgument(0, Customer.class);
		createCustomerAccount(customer);
	}

	/**
	 * Handles the 'MerchantAccountRegistrationRequested' event
	 * @param event the event
	 */
	public void policyMerchantRegistrationRequested(Event event) {
		logger.info("Handling the merchant account registration event");
		var merchant = event.getArgument(0, Merchant.class);
		createMerchantAccount(merchant);
	}


	/**
	 * Handles the 'TokenValidated' event
	 * @param event the event
	 */
	public void tokenValidated(Event event) {
		logger.info("Handling the token validated event");
		var customerId = event.getArgument(0, String.class);
		retrieveCustomerBankAccount(customerId);
	}


	/**
	 * Handles the 'PaymentRequested' event
	 * @param event the event
	 */
	private void paymentRequested(Event event) {
		logger.info("Handling the payment requested event");
		var merchantId = event.getArgument(2, String.class);
		retrieveMerchantBankAccount(merchantId);
	}

	/* Commands */

	/**
	 * Store the given customer with a new ID and publish a 'CustomerRegistered' event
	 * @param customer the customer
	 */
	private void createCustomerAccount(Customer customer) {
		String cid = UUID.randomUUID().toString();
		customer.setId(cid);
		logger.info("Trying to save the customer");
		accountRepository.addCustomer(customer);
		logger.info("Customer saved, publishing back");
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
	 * @throws RuntimeException if there is no customer with the given ID
	 */
	private void retrieveCustomerBankAccount(String customerId) {
		var customerAccount = accountRepository.getCustomerById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		logger.info("Customer account found, publishing back");
		queue.publish(new Event("CustomerBankAccountRetrieved", customerAccount.getBankAccountNumber()));
	}

	/**
	 * Gets a stored merchant by the given ID, if succeeds publish a 'MerchantBankAccountRetrieved' event
	 * @param merchantId the customer ID
	 * @throws RuntimeException if there is no merchant with the given ID
	 */
	private void retrieveMerchantBankAccount(String merchantId) {
		var merchantAccount = accountRepository.getMerchantById(merchantId)
				.orElseThrow(() -> new RuntimeException("Merchant not found"));
		logger.info("Merchant account found, publishing back");
		queue.publish(new Event("MerchantBankAccountRetrieved", merchantAccount.getBankAccountNumber()));
	}
}
