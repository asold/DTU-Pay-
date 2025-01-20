package dk.dtu;

import dk.dtu.businesslogic.models.PaymentResponse;
import dk.dtu.businesslogic.services.CreatePaymentCommand;
import dk.dtu.businesslogic.services.PaymentService;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.CorrelationId;
import messaging.MessageQueue;
import messaging.Event;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;

public class PaymentStepDefinitions {

    private MessageQueue queue = mock(MessageQueue.class);
    private CreatePaymentCommand paymentCommand;
    private PaymentService paymentService = new PaymentService(queue);
    private BankService bankService = new BankServiceService().getBankServicePort();
    private String merchantBankAccount;
    private String customerBankAccount;
    private CorrelationId correlationId = new CorrelationId();

    private User merchantUser;
    private User customerUser;

    private Event paymentRequestedEvent;
    private Event merchantBankAccountRetrievedEvent;
    private Event customerBankAccountRetrievedEvent;
    private Event paymentProcessedEvent;
    private PaymentResponse paymentResponse;


    @Before
    public void setUp() throws BankServiceException_Exception {
        String merchantCpr = "120999-7333";
        String customerCpr = "120998-7444";

        merchantUser = new User();
        merchantUser.setCprNumber(merchantCpr);
        merchantUser.setFirstName("Bob");
        merchantUser.setLastName("Smith");

        customerUser = new User();
        customerUser.setCprNumber(customerCpr);
        customerUser.setFirstName("Alice");
        customerUser.setLastName("Doe");

        try {
            bankService.retireAccount(bankService.getAccountByCprNumber(merchantCpr).getId());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            bankService.retireAccount(bankService.getAccountByCprNumber(customerCpr).getId());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        merchantBankAccount = bankService.createAccountWithBalance(merchantUser, BigDecimal.valueOf(1000));
        customerBankAccount = bankService.createAccountWithBalance(customerUser, BigDecimal.valueOf(1000));

    }

    @Given("there is a {string} event received")
    public void there_is_a_event_received(String event) {
        switch (event) {
            case "PaymentRequested":
                paymentRequestedEvent = new Event(event, correlationId,"","", 10.0);
                break;
            case "CustomerBankAccountRetrieved":
                customerBankAccountRetrievedEvent = new Event(event, correlationId, customerBankAccount);
                break;
            case "MerchantBankAccountRetrieved":
                merchantBankAccountRetrievedEvent = new Event(event, correlationId, merchantBankAccount);
                break;
        }
    }
    @When("the {string} event is handled")
    public void the_event_is_handled(String event) {
        switch (event) {
            case "PaymentRequested":
                paymentService.paymentRequestedPolicy(paymentRequestedEvent);
                break;
            case "CustomerBankAccountRetrieved":
                paymentService.customerBankAccountRetrievedEventHandler(customerBankAccountRetrievedEvent);
                break;
            case "MerchantBankAccountRetrieved":
                paymentService.merchantBankAccountRetrievedEventHandler(merchantBankAccountRetrievedEvent);
                break;
        }
    }


    @Then("the {string} event is published")
    public void the_event_is_published(String eventName) {
       if(eventName.equals("PaymentRequested")) {
           paymentResponse = new PaymentResponse(correlationId.getId(), true);
           paymentProcessedEvent = new Event("PaymentProcessed", correlationId, paymentResponse);
           verify(queue).publish(paymentProcessedEvent);
       }
       else if (eventName.equals("NegativeAmountRequested")) {
           verify(queue).publish(new Event("NegativeAmountRequested", correlationId, "NegativeAmountRequested"));
       }
       else if (eventName.equals("DebtorAccountNotFound")) {
           verify(queue).publish(new Event("DebtorAccountNotFound", correlationId, "DebtorAccountNotFound"));
       }
       else if(eventName.equals("CreditorAccountNotFound")){
           verify(queue).publish(new Event("CreditorAccountNotFound", correlationId, "CreditorAccountNotFound"));
       }
    }

    @And("the payment amount is {int}")
    public void thePaymentAmountIs(int amount) {
        paymentRequestedEvent = new Event("PaymentRequested", correlationId, "", "", amount);
    }

    @And("the customer bank account is not registered in the bank")
    public void theCustomerBankAccountIsNotRegisteredInTheBank() {
        customerBankAccount = "unregistered-bank-account";
        customerBankAccountRetrievedEvent = new Event("CustomerBankAccountRetrieved", correlationId, customerBankAccount);
    }


    @And("the merchant bank account is not registered in the bank")
    public void theMerchantBankAccountIsNotRegisteredInTheBank() {
        merchantBankAccount = "unregistered-bank-account";
        merchantBankAccountRetrievedEvent = new Event("MerchantBankAccountRetrieved", correlationId, customerBankAccount);
    }
}
