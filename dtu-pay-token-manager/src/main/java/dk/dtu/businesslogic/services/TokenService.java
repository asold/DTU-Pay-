package dk.dtu.businesslogic.services;

import dk.dtu.businesslogic.exceptions.IllegalNumberOfRequestedTokensException;
import dk.dtu.businesslogic.exceptions.InvalidCustomerAccountException;
import dk.dtu.businesslogic.exceptions.InvalidTokenException;
import dk.dtu.businesslogic.models.Token;
import dk.dtu.businesslogic.models.TokenResult;
import dk.dtu.businesslogic.models.TokensRequestPolicy;
import dk.dtu.businesslogic.repositories.TokenRepository;
import dk.dtu.businesslogic.repositories.exceptions.TokenNotFoundException;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token Service Responsible for Handling events related with DTU Pay Tokens
 *
 * @author Simao Teixeira (s232431)
 */
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final MessageQueue queue;
    private final TokenRepository tokenRepository;
    private final ConcurrentHashMap<CorrelationId, TokensRequestPolicy> tokensRequests =
            new ConcurrentHashMap<>();

    public TokenService(MessageQueue q) {
        this.queue = q;
        queue.addHandler("TokensRequested", this::tokensRequestedEventHandler);
        queue.addHandler("PaymentRequested", this::paymentRequestedEventHandler);
        queue.addHandler("CustomerAccountValidated", this::customerAccountValidatedEventHandler);
        tokenRepository = new TokenRepository();
    }

    // region Event Handlers
    public void paymentRequestedEventHandler(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        UUID token = event.getArgument(1, UUID.class);
        try {
            String customerId = validateToken(token);
            queue.publish(new Event("TokenValidated", correlationId, customerId));
        } catch (InvalidTokenException e) {
            log.error("Error during request with correlationId \"{}\": ", correlationId.getId(), e);
            queue.publish(
                    new Event("TokenValidationFailed", correlationId, e.getErrorCode(), "Invalid Token"));
        } catch (Exception e) {
            log.error("Error during request with correlationId \"{}\": ", correlationId.getId(), e);
            queue.publish(
                    new Event(
                            "TokenValidationFailed", correlationId, "UNKNOWN_ERROR", "Error Validating Token"));
        }
    }

    public void tokensRequestedEventHandler(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);

        try {
            String customerId = event.getArgument(1, String.class);
            Integer amount = event.getArgument(2, Integer.class);

            TokensRequestPolicy policy =
                    tokensRequests.computeIfAbsent(correlationId, r -> new TokensRequestPolicy());
            policy.setCustomerId(customerId);
            policy.setAmount(amount);

            tryGetTokens(correlationId);
        } catch (Exception e) {
            log.error("Error during request with correlationId \"{}\": ", correlationId.getId(), e);
            queue.publish(
                    new Event(
                            "TokensGeneratedFailed", correlationId, "UNKNOWN_ERROR", "Error Generating Token"));
        }
    }

    public void customerAccountValidatedEventHandler(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        try {
            Boolean isValidAccount = event.getArgument(1, Boolean.class);
            TokensRequestPolicy policy =
                    tokensRequests.computeIfAbsent(correlationId, r -> new TokensRequestPolicy());
            policy.setIsValidAccount(isValidAccount);

            tryGetTokens(correlationId);
        } catch (Exception e) {
            log.error("Error during request with correlationId \"{}\": ", correlationId.getId(), e);
            queue.publish(
                    new Event(
                            "TokensGeneratedFailed", correlationId, "UNKNOWN_ERROR", "Error Generating Token"));
        }
    }

    // endregion

    /**
     * Validates a given Token ID
     *
     * @param tokenId Token ID
     * @return Customer ID if the token is valid
     * @throws InvalidTokenException if the token was already used or non-existent
     */
    private String validateToken(UUID tokenId) throws InvalidTokenException {
        Token token;
        Optional<Token> tokenOptional = tokenRepository.getTokenByValue(tokenId);
        if (tokenOptional.isPresent()) {
            token = tokenOptional.get();
        } else {
            throw new InvalidTokenException("Token not found: " + tokenId);
        }

        if (token.isNotUsed()) {
            try {
                revokeToken(token);
            } catch (InvalidTokenException e) {
                // This should never happen
                throw new RuntimeException(e);
            }
            return token.getCustomerId();
        } else {
            throw new InvalidTokenException("Token " + tokenId + " was already used");
        }
    }

    /**
     * Revokes a token
     *
     * @param token Token to be revoked
     * @throws InvalidTokenException when the token to be revoked does not exist
     */
    private void revokeToken(Token token) throws InvalidTokenException {
        token.setUsed(true);
        try {
            tokenRepository.modifyToken(token);
        } catch (TokenNotFoundException e) {
            throw new InvalidTokenException(
                    "Error revoking token: \""
                            + token.getTokenId()
                            + "\" of the customer: \""
                            + token.getCustomerId()
                            + "\" does not exist",
                    e);
        }
    }

    /**
     * Try to Get Tokens when TokenRequestPolicy is completed
     *
     * @param correlationId correlationID of the request
     */
    private void tryGetTokens(CorrelationId correlationId) {
        var policy = tokensRequests.get(correlationId);
        if (policy.allowsExecution()) {
            try {
                List<TokenResult> lstTokens = getTokens(
                        policy.getCustomerId(),
                        policy.getAmount(),
                        policy.getIsValidAccount()
                );

                queue.publish(new Event("TokensGenerated", correlationId, lstTokens));

            } catch (IllegalNumberOfRequestedTokensException | InvalidCustomerAccountException e) {
                log.error("Error during request with correlationId \"{}\": ", correlationId.getId(), e);
                queue.publish(
                        new Event("TokensGeneratedFailed", correlationId, e.getErrorCode(), e.getMessage()));
            }
        }
    }

    /**
     * Get Tokens implementation
     *
     * @param customerId     Customer ID
     * @param amount         amount of tokens requested
     * @param isValidAccount if the account is valid or not
     * @return List of Tokens to the User
     * @throws IllegalNumberOfRequestedTokensException if amount of tokens request is not valid
     * @throws InvalidCustomerAccountException         if the customer does not have an account
     */
    private List<TokenResult> getTokens(String customerId, int amount, boolean isValidAccount)
            throws IllegalNumberOfRequestedTokensException, InvalidCustomerAccountException {
        if (!isValidAccount) {
            throw new InvalidCustomerAccountException("Invalid Customer Account");
        }

        var listTokens = tokenRepository.getUnusedTokensByCustomerId(customerId);
        if (amount >= 1 && amount <= 5) {
            if (listTokens.size() > 1) {
                throw new IllegalNumberOfRequestedTokensException(
                        "Customer can only request tokens when have 0 or 1 token left");

            } else {
                var result = generateTokens(customerId, amount);
                return result.stream().map(token -> new TokenResult(token.getTokenId())).toList();
            }
        } else {
            throw new IllegalNumberOfRequestedTokensException(
                    "Amount of requested tokens must be between 1 and 5");
        }
    }

    /**
     * Generates Tokens for the customer
     *
     * @param customerId Customer ID
     * @param amount     amount of tokens to be generated
     */
    private List<Token> generateTokens(String customerId, int amount) {
        List<Token> returnList = new ArrayList<>();
        for (int i = 0; i < amount; ++i) {
            Token token = new Token(UUID.randomUUID(), customerId);
            returnList.add(tokenRepository.saveToken(token));
        }
        return returnList;
    }
}
