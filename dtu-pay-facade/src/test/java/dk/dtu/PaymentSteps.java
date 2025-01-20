package dk.dtu;

import dk.dtu.adapters.PaymentFacade;
import dk.dtu.core.exceptions.InvalidTokenException;
import dk.dtu.core.models.Payment;
import dk.dtu.core.models.PaymentResponse;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class PaymentSteps {

    private MessageQueue queue = mock(MessageQueue.class);
    private PaymentFacade paymentFacade = new PaymentFacade(queue);
    private BankService bankService = new BankServiceService().getBankServicePort();

    private String merchantBankAccount;
    private String customerBankAccount;
    private UUID customerToken = UUID.randomUUID();
    private CorrelationId correlationId;

    private User merchantUser;
    private User customerUser;

    private Event paymentRequestedEvent;
    private Event paymentProcessedEvent;

    Payment payment;

    private PaymentResponse paymentResponse;

    CompletableFuture<PaymentResponse> future;

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

    @When("the {string} event is handled")
    public void theEventIsHandled(String eventName) {
        switch (eventName) {
            case "PaymentProcessed":
                paymentFacade.policyPaymentProcessed(paymentRequestedEvent);
                break;
            case "TokenValidationFailed":
                Event tokenValidationFailedEvent = new Event(eventName, correlationId, "Invalid Token");
                paymentFacade.policyTokenValidationFailed(tokenValidationFailedEvent);
                break;
            case "MerchantNotFound":
                Event merchantNotFoundEvent = new Event(eventName, correlationId, "Merchant not found");
                paymentFacade.policyMerchantNotFound(merchantNotFoundEvent);
                break;
        }
    }

    @Then("the future completes exceptionally with an {string}")
    public void theFutureCompletesExceptionallyWithAnInvalidPaymentException(String exceptionName) throws ExecutionException, InterruptedException {
        assertTrue(future.isCompletedExceptionally());
        assertThrows("Invalid Token",ExecutionException.class, () -> future.get() );
    }

    @Given("the payment is made with {string} error")
    public void thePaymentIsMadeWithError(String errorType) throws ExecutionException, InterruptedException {
        payment = new Payment("RegisteredMerchantId", customerToken, BigDecimal.valueOf(10));

        doAnswer(invocation -> {
            Event event = invocation.getArgument(0, Event.class);
            correlationId = event.getArgument(0, CorrelationId.class); // Capture correlationId

            if ("PaymentRequested".equals(event.getTopic())) {
                future = paymentFacade.getPaymentRequests().get(correlationId);
                if (future != null) {
                    switch (errorType) {
                        case "Invalid token":
                            future.completeExceptionally(new InvalidTokenException("Invalid Token"));
                            break;
                        case "MerchantNotFoundException":
                            future.completeExceptionally(new RuntimeException("Merchant not found"));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown error type: " + errorType);
                    }
                }
            }
            return null;
        }).when(queue).publish(any(Event.class));

        CompletableFuture.runAsync(() -> {
            try {
                paymentFacade.requestPayment(payment);
            } catch (Exception e) {
            }
        }).get();
    }
}