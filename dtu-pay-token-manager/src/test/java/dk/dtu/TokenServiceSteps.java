package dk.dtu;

import com.google.gson.reflect.TypeToken;
import dk.dtu.businesslogic.models.Token;
import dk.dtu.businesslogic.models.TokenResult;
import dk.dtu.businesslogic.services.TokenService;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Simão Teixeira (s232431)
 */
public class TokenServiceSteps {

    MessageQueue queue = mock(MessageQueue.class);
    TokenService tokenService = new TokenService(queue);
    CorrelationId correlationId = new CorrelationId();
    String customerId;
    List<Token> tokens;

    @Given("a customer with {int} tokens")
    public void aValidCustomerWithTokens(int amountTokens)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        customerId = UUID.randomUUID().toString();

        // Generate tokens directly using reflection
        if (amountTokens > 0) {
            Method generateTokens =
                    tokenService.getClass().getDeclaredMethod("generateTokens", String.class, int.class);
            generateTokens.setAccessible(true);

            //noinspection unchecked
            tokens = (List<Token>) generateTokens.invoke(tokenService, customerId, amountTokens);
        }
    }

    @When("a {string} event with amount {int} is published")
    public void aEventWithCustomerIdAndAmount(String eventName, int amountTokens) {
        Event event = new Event(eventName, correlationId, customerId, amountTokens);
        tokenService.tokensRequestedEventHandler(event);
    }

    @Given("a {string} event with isValidAccount {booleanValue} is sent")
    public void EventWithIsValidAccountTrue(String eventName, Boolean isValidAccount) {
        Event event = new Event(eventName, correlationId, isValidAccount);
        tokenService.customerAccountValidatedEventHandler(event);
    }

    @SuppressWarnings("unchecked")
    @Then("the {string} event is published with {int} tokens")
    public void theEventIsPublished(String expectedEventName, int expectedTokens) {
        Type listType = new TypeToken<List<TokenResult>>() {
        }.getType();
        verify(queue)
                .publish(
                        argThat(
                                event ->
                                        event.getTopic().equals(expectedEventName)
                                                && event.getArgument(0, CorrelationId.class).equals(correlationId)
                                                && ((List<TokenResult>) event.getArgument(1, listType)).size()
                                                == expectedTokens));
    }

    @When("a {string} event with {string} token is published")
    public void aEventWithAValidTokenIsPublished(String eventName, String validToken) {
        Event event;
        if (validToken.equalsIgnoreCase("VALID")) {
            event = new Event(eventName, correlationId, tokens.getFirst().getTokenId());
        } else if (validToken.equalsIgnoreCase("INVALID")) {
            event = new Event(eventName, correlationId, UUID.randomUUID());
        } else {
            throw new IllegalArgumentException(
                    "Token in the test parameter can only be \"valid\" or \"invalid\"");
        }

        tokenService.paymentRequestedEventHandler(event);
    }

    @Then("the {string} event is published with the customerID")
    public void theEventIsPublishedWithTheCustomerID(String expectedEventName) {
        verify(queue)
                .publish(
                        argThat(
                                event ->
                                        event.getTopic().equals(expectedEventName)
                                                && event.getArgument(0, CorrelationId.class).equals(correlationId)
                                                && event.getArgument(1, String.class).equals(customerId)));
    }

    @Then("the {string} event is published")
    public void theEventIsPublished(String expectedEventName) {
        verify(queue)
                .publish(
                        argThat(
                                event ->
                                        event.getTopic().equals(expectedEventName)
                                                && event.getArgument(0, CorrelationId.class).equals(correlationId)));
    }

    //region ParameterTypes
    @ParameterType(value = "true|True|TRUE|false|False|FALSE")
    public Boolean booleanValue(String value) {
        return Boolean.valueOf(value);
    }
    //endregion
}
