package dk.dtu;

import dk.dtu.businesslogic.models.Customer;
import dk.dtu.businesslogic.models.Merchant;
import dk.dtu.businesslogic.services.AccountService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AccountSteps {

    private final MessageQueue queue = mock(MessageQueue.class);
    private final CorrelationId correlationId = new CorrelationId();
    private final AccountService accountService = new AccountService(queue);

    private Customer customer;
    private Merchant merchant;

    private Event registerCustomerEvent;
    private Event registerMerchantEvent;
    private Event deregisterCustomerEvent;
    private Event deregisterMerchantEvent;
    private Event tokenValidatedEvent;

    @Given("a customer first name is {string} and the last name {string} and the cpr is {string} and the account number is {string}")
    public void aCustomerFirstNameIsAndTheLastNameAndTheCprIsAndTheAccountNumberIs(String firstName, String lastName, String cpr, String accountNumber) {
        this.customer = new Customer();
        this.customer.setFirstName(firstName);
        this.customer.setLastName(lastName);
        this.customer.setCpr(cpr);
        this.customer.setBankAccountNumber(accountNumber);
    }

    @Given("an event called {string}")
    public void anEventCalled(String eventName) {
        switch (eventName) {
            case "CustomerAccountRegistrationRequested":
                registerCustomerEvent = new Event(eventName, correlationId, customer);
                break;
            case "MerchantAccountRegistrationRequested":
                registerMerchantEvent = new Event(eventName, correlationId, merchant);
                break;
            case "CustomerAccountDeregistrationRequested":
                deregisterCustomerEvent = new Event(eventName, correlationId, customer);
                break;
            case "MerchantAccountDeregistrationRequested":
                deregisterMerchantEvent = new Event(eventName, correlationId, merchant);
                break;
            case "TokenValidated":
                tokenValidatedEvent = new Event(eventName, correlationId, customer.getId());

        }
    }


    @When("the {string} is handled")
    public void theIsHandled(String eventName) {
        switch (eventName) {
            case "CustomerAccountRegistrationRequested":
                accountService.policyCustomerRegistrationRequested(this.registerCustomerEvent);
                break;
            case "MerchantAccountRegistrationRequested":
                accountService.policyMerchantRegistrationRequested(this.registerMerchantEvent);
                break;
            case "CustomerAccountDeregistrationRequested":
                accountService.policyCustomerDeregistrationRequested(this.deregisterCustomerEvent);
                break;
            case "MerchantAccountDeregistrationRequested":
                accountService.policyMerchantDeregistrationRequested(this.deregisterMerchantEvent);
                break;
        }

    }

    @Then("a {string} event published")
    public void aEventPublished(String eventName) {

        ArgumentCaptor<Event> captor1 = ArgumentCaptor.forClass(Event.class);
        verify(queue).publish(captor1.capture());
        Event capturedEvent = captor1.getValue();

        switch (eventName) {
            case "CustomerRegistered", "MerchantRegistered":
                assertEquals(eventName, capturedEvent.getTopic());
                assertEquals(correlationId, capturedEvent.getArgument(0, CorrelationId.class));
                assertNotNull(capturedEvent.getArgument(1, String.class));
                break;
            case "CustomerRegistrationFailed", "MerchantRegistrationFailed":
                assertEquals(eventName, capturedEvent.getTopic());
                assertEquals(correlationId, capturedEvent.getArgument(0, CorrelationId.class));
                assertNotNull(capturedEvent.getArgument(1, String.class));
                break;
            case "CustomerDeregistered", "MerchantDeregistered":
                assertEquals(eventName, capturedEvent.getTopic());
                assertEquals(correlationId, capturedEvent.getArgument(0, CorrelationId.class));
        }
    }

    @Given("a merchant first name is {string} and the last name {string} and the cpr is {string} and the account number is {string}")
    public void aMerchantFirstNameIsAndTheLastNameAndTheCprIsAndTheAccountNumberIs(String firstName, String lastName, String cpr, String accountNumber) {
        merchant = new Merchant();
        merchant.setFirstName(firstName);
        merchant.setLastName(lastName);
        merchant.setCpr(cpr);
        merchant.setBankAccountNumber(accountNumber);
    }

    @And("the customer is registered")
    public void theCustomerIsRegistered() {
        registerCustomerEvent = new Event("CustomerAccountRegistrationRequested", correlationId, customer);
        accountService.policyCustomerRegistrationRequested(this.registerCustomerEvent);
    }
}
