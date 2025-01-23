package dk.dtu;

import com.google.common.math.DoubleMath;
import dk.dtu.adapters.*;
import dk.dtu.core.exceptions.TokenServiceException;
import dk.dtu.core.models.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.wildfly.common.Assert.assertTrue;

import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FacadeSteps {

    private final MessageQueue mockQueue = mock(MessageQueue.class);

    private final CustomerFacade customerFacade = new CustomerFacade(mockQueue);
    private final MerchantFacade merchantFacade = new MerchantFacade(mockQueue);
    private final PaymentFacade paymentFacade = new PaymentFacade(mockQueue);
    private final ReportFacade reportFacade = new ReportFacade(mockQueue);
    private final TokenFacade tokenFacade = new TokenFacade(mockQueue);

    private String customerId;
    private String merchantId;

    private RegisterCustomerRequest customerRegistrationRequest;
    private RegisterMerchantRequest merchantRegistrationRequest;

    private Payment payment;
    private int tokens;
    private UUID token;

    private Event event;

    @Given("Given a customer registration request where the first name is {string} and the last name {string} and the cpr is {string} and the account number is {string}")
    public void givenACustomerRegistrationRequestWhereTheFirstNameIsAndTheLastNameAndTheCprIsAndTheAccountNumberIs(String firstName, String lastName, String cpr, String accountNumber) {
        customerRegistrationRequest = new RegisterCustomerRequest(firstName, lastName, cpr, accountNumber);
    }


    @When("the customer registration request handled")
    public void theCustomerRegistrationRequestHandled() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            completeCustomerFutures();
        });
        customerFacade.registerCustomer(customerRegistrationRequest);
    }

    @Then("a {string} event published")
    public void aEventPublished(String eventName) {
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(mockQueue).publish(captor.capture());
        event = captor.getValue();
        assertEquals(eventName, event.getTopic());
    }

    @And("the event contains a customer with the first name {string} the last name {string} the cpr is {string} and the account number is {string}")
    public void theEventContainsACustomerWithTheFirstNameTheLastNameTheCprIsAndTheAccountNumberIs(String firstName, String lastName, String cpr, String accountNumber) {
        RegisterCustomerRequest customerReqRequest = event.getArgument(1, RegisterCustomerRequest.class);
        assertEquals(firstName, customerReqRequest.firstName());
        assertEquals(lastName, customerReqRequest.lastName());
        assertEquals(cpr, customerReqRequest.cpr());
        assertEquals(accountNumber, customerReqRequest.bankAccountNumber());
    }


    @When("the customer deregistration request handled")
    public void theCustomerDeregistrationRequestHandled() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            completeCustomerFutures();
        });
        customerFacade.deregisterCustomer(customerId);
    }

    @And("the event contains a customer id with a value of {string}")
    public void theEventContainsACustomerIdWithAValueOf(String id) {
        String eventValue = event.getArgument(1, String.class);
        assertEquals(id, eventValue);
    }

    private Pair<CorrelationId, CompletableFuture<String>> addFuture() {
        Pair<CorrelationId, CompletableFuture<String>> pair = Pair.of(new CorrelationId(), new CompletableFuture<>());
        customerFacade.getFutures().put(pair.getLeft(), pair.getRight());
        return pair;
    }

    @Given("Given a merchant registration request where the first name is {string} and the last name {string} and the cpr is {string} and the account number is {string}")
    public void givenAMerchantRegistrationRequestWhereTheFirstNameIsAndTheLastNameAndTheCprIsAndTheAccountNumberIs(String firstName, String lastName, String cpr, String accountNumber) {
        merchantRegistrationRequest = new RegisterMerchantRequest(firstName, lastName, cpr, accountNumber);

    }

    @When("the merchant registration request handled")
    public void theMerchantRegistrationRequestHandled() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            completeMerchantFutures();
        });
        merchantFacade.registerMerchant(merchantRegistrationRequest);

    }

    @And("the event contains a merchant with the first name {string} the last name {string} the cpr is {string} and the account number is {string}")
    public void theEventContainsAMerchantWithTheFirstNameTheLastNameTheCprIsAndTheAccountNumberIs(String firstName, String lastName, String cpr, String accountNumber) {
        RegisterMerchantRequest merchantReqRequest = event.getArgument(1, RegisterMerchantRequest.class);
        assertEquals(firstName, merchantReqRequest.firstName());
        assertEquals(lastName, merchantReqRequest.lastName());
        assertEquals(cpr, merchantReqRequest.cpr());
        assertEquals(accountNumber, merchantReqRequest.bankAccountNumber());
    }

    @Given("Given a merchant registered with ID {string}")
    public void aMerchantRegisteredWithID(String id) {
        merchantId = id;
    }

    @When("the merchant deregistration request handled")
    public void theMerchantDeregistrationRequestHandled() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            completeMerchantFutures();
        });
        merchantFacade.deregisterMerchant(merchantId);

    }

    @And("the event contains a merchant id with a value of {string}")
    public void theEventContainsAMerchantIdWithAValueOf(String id) {
        String eventValue = event.getArgument(1, String.class);
        assertEquals(id, eventValue);
    }

    @Given("Given payment where the merchant id is {string} and the amount is {int}")
    public void givenPaymentWhereTheMerchantIdIsAndTheAmountIs(String merchantId, int amount) {
        token = UUID.randomUUID();
        payment = new Payment();
        payment.setMerchantId(merchantId);
        payment.setToken(token);
        payment.setAmount(new BigDecimal(amount));

    }

    @When("the payment request handled")
    public void paymentRequestHandled() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            completePaymentFutures();
        });
        paymentFacade.requestPayment(payment);
    }


    @And("the event contains a payment where the amount is {int} and the merchant id is {string}")
    public void theEventContainsAPaymentWhereTheAmountIsAndTheMerchantIdIs(int amount, String merchantId) {
        UUID tokenValue = event.getArgument(1, UUID.class);
        String merchantIdValue = event.getArgument(2, String.class);
        BigDecimal paymentValue = event.getArgument(3, BigDecimal.class);
        assertEquals(token, tokenValue);
        assertEquals(merchantId, merchantIdValue);
        assertTrue(DoubleMath.fuzzyEquals(new BigDecimal(amount).doubleValue(), paymentValue.doubleValue(), 0.009));
    }

    @When("the customer record request handled")
    public void customerRecordRequestHandled() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            completeReportFutures();
        });
        reportFacade.getCustomerPaymentLogs(customerId);
    }

    @Given("a customer registered customer with ID {string}")
    public void aCustomerRegisteredCustomerWithID(String id) {
        customerId = id;
    }

    @When("the merchant record request handled")
    public void merchantRecordRequestHandled() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            completeReportFutures();
        });
        reportFacade.getMerchantPaymentLogs(merchantId);
    }

    @Given("a merchant registered with Id {string}")
    public void aMerchantRegisteredWithId(String id) {
        merchantId = id;
    }

    @When("a manager record request handled")
    public void aManagerRecordRequestHandled() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            completeReportFutures();
        });
        reportFacade.getManagerPaymentLogs();
    }

    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(int count) {
        tokens = count;
    }

    @And("the token request handled")
    public void theTokenRequestHandled() throws ExecutionException, InterruptedException, TokenServiceException {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            completeTokenFutures();
        });
        tokenFacade.getTokens(customerId, tokens);
    }

    @And("the event contains the amount of {int} and the customer id {string}")
    public void theEventContainsTheAmountOfAndTheCustomerId(int count, String id) {
        String eventCustomerId = event.getArgument(1, String.class);
        int amount = event.getArgument(2, int.class);
        assertEquals(id, eventCustomerId);
        assertEquals(count, amount);
    }

    private void completeCustomerFutures() {
        customerFacade.getFutures().forEach((k,v) -> v.complete(""));
    }

    private void completeMerchantFutures() {
        merchantFacade.getFutures().forEach((k,v) -> v.complete(""));
    }

    private void completePaymentFutures() {
        paymentFacade.getPaymentRequests().forEach((k,v) -> v.complete(new PaymentResponse(UUID.randomUUID(),true)));
    }

    private void completeReportFutures() {
        reportFacade.getReportRequests().forEach((k,v) -> v.complete(new ArrayList<>()));
    }

    private void completeTokenFutures() {
        tokenFacade.getTokenRequests().forEach((k,v) -> v.complete(new ArrayList<>()));
    }
}
