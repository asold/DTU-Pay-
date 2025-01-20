package dk.dtu.businesslogic.services;

import dk.dtu.businesslogic.repositories.AccountRepository;
import dk.dtu.businesslogic.models.Customer;
import dk.dtu.businesslogic.models.Merchant;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author  Andrei Soldan 243873
 */
public class AccountService {

	private final MessageQueue queue;
	private final AccountRepository accountRepository;

	public AccountService(MessageQueue q) {
		this.queue = q;
		queue.addHandler("CustomerAccountRegistrationRequested", this::policyCustomerRegistrationRequested);
		queue.addHandler("MerchantAccountRegistrationRequested", this::policyMerchantRegistrationRequested);

		queue.addHandler("CustomerAccountDeregistrationRequested", this::policyCustomerDeregistrationRequested);
		queue.addHandler("MerchantAccountDeregistrationRequested", this::policyMerchantDeregistrationRequested);

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
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		var customer = event.getArgument(1, Customer.class);
		String customerCheck = checkEntity(customer.getFirstName(), customer.getLastName(), customer.getCpr(), customer.getBankAccountNumber());
		if (!"OK".equals(customerCheck)) {
			queue.publish(new Event("CustomerRegistrationFailed", correlationId, customerCheck));
			return;
		}
		var cid = createCustomerAccount(customer);
		queue.publish(new Event("CustomerRegistered", correlationId, cid));
	}

	/**
	 * Handles the 'MerchantAccountRegistrationRequested' event
	 * @param event the event
	 */
	public void policyMerchantRegistrationRequested(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		var merchant = event.getArgument(1, Merchant.class);
		String merchantCheck = checkEntity(merchant.getFirstName(), merchant.getLastName(), merchant.getCpr(), merchant.getBankAccountNumber());
		if (!"OK".equals(merchantCheck)) {
			queue.publish(new Event("MerchantRegistrationFailed", correlationId, merchantCheck));
			return;
		}
		var mid = createMerchantAccount(merchant);
		queue.publish(new Event("MerchantRegistered", correlationId, mid));
	}

	/**
	 * Handles the 'CustomerAccountDeregistrationRequested' event
	 * @param event the event
	 */
	public void policyCustomerDeregistrationRequested(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		var customer = event.getArgument(1, Customer.class);
		deregisterCustomer(customer);
		queue.publish(new Event("CustomerDeregistered", correlationId));
	}

	/**
	 * Handles the 'MerchantAccountDeregistrationRequested' event
	 * @param event the event
	 */
	public void policyMerchantDeregistrationRequested(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		var merchant = event.getArgument(1, Merchant.class);
		deregisterMerchant(merchant);
		queue.publish(new Event("MerchantDeregistered", correlationId));
	}


	/**
	 * Handles the 'TokenValidated' event
	 * @param event the event
	 */
	public void tokenValidated(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		var customerId = event.getArgument(1, String.class);
		retrieveCustomerBankAccount(customerId, correlationId);
	}


	/**
	 * Handles the 'PaymentRequested' event
	 * @param event the event
	 */
	public void paymentRequested(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		var merchantId = event.getArgument(2, String.class);
		retrieveMerchantBankAccount(merchantId, correlationId);
	}

	/* Commands */

	/**
	 * Store the given customer with a new ID and publish a 'CustomerRegistered' event
	 * @param customer the customer
	 */
	public String createCustomerAccount(Customer customer) {
		String cid = UUID.randomUUID().toString();
		customer.setId(cid);
		accountRepository.addCustomer(customer);
		return cid;
	}


	/**
	 * Store the given merchant with a new ID and publish a 'MerchantRegistered' event
	 * @param merchant the merchant
	 */
	public String createMerchantAccount(Merchant merchant) {
		String mid = UUID.randomUUID().toString();
		merchant.setId(mid);
		accountRepository.addMerchant(merchant);
		return mid;
	}

	/**
	 * Delete the given customer
	 * @param customer the customer
	 */
	public void deregisterCustomer(Customer customer) {
		accountRepository.deleteCustomer(customer);
	}

	/**
	 * Delete the give merchant
	 * @param merchant the merchant
	 */
	public void deregisterMerchant(Merchant merchant) {
		accountRepository.deleteMerchant(merchant);
	}

	/**
	 * Gets a stored customer by the given ID, if succeeds publish a 'CustomerBankAccountRetrieved' event
	 * @param customerId the customer ID
	 * @param correlationId the correlation ID
	 * @throws RuntimeException if there is no customer with the given ID
	 */
	public void retrieveCustomerBankAccount(String customerId, CorrelationId correlationId) {
		var customerAccount = accountRepository.getCustomerById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		queue.publish(new Event("CustomerBankAccountRetrieved", correlationId, customerAccount.getBankAccountNumber()));
	}

	/**
	 * Gets a stored merchant by the given ID, if succeeds publish a 'MerchantBankAccountRetrieved' event
	 * @param merchantId the customer ID
	 * @param correlationId the correlation ID
	 * @throws RuntimeException if there is no merchant with the given ID
	 */
	public void retrieveMerchantBankAccount(String merchantId, CorrelationId correlationId) {
		var merchantAccount = accountRepository.getMerchantById(merchantId)
				.orElseThrow(() -> new RuntimeException("Merchant not found"));
		queue.publish(new Event("MerchantBankAccountRetrieved", correlationId, merchantAccount.getBankAccountNumber()));
	}


	/**
	 * Checks if the given entity has invalid values
	 * @param firstName the first name of the entity
	 * @param lastName the lastName of the entity
	 * @param cpr the cpr number of the entity
	 * @param bankAccountNumber the bank account number of the entity
	 * @return the invalid value as a string. If there's no invalid fields, it returns "OK"
	 */
	private String checkEntity(String firstName, String lastName, String cpr, String bankAccountNumber) {
		List<String> invalidFields = checkStringValues(
				List.of(
					Pair.of("first name", firstName),
					Pair.of("last name", lastName),
					Pair.of("CPR", cpr),
					Pair.of("Bank account number", bankAccountNumber)
				)
		);
		if (!invalidFields.isEmpty()) {
			return "Invalid " + invalidFields.getFirst();
		}
		return "OK";
	}

	/**
	 * Check the input pairs for null or empty values
	 * @param valuePairs the values pair
	 * @return the list of keys where the value is empty
	 */
    private List<String> checkStringValues(List<Pair<String, String>> valuePairs) {
		 return valuePairs.stream()
				 .filter(p -> p.getValue() == null || p.getValue().isEmpty())
				 .map(Pair::getKey)
				 .collect(Collectors.toList());
	}
}
