package dk.dtu.businesslogic.services;

import dk.dtu.businesslogic.exceptions.DuplicateTokenUUIDException;
import dk.dtu.businesslogic.exceptions.InvalidTokenException;
import dk.dtu.businesslogic.exceptions.TokenNotFoundException;
import dk.dtu.businesslogic.models.Token;
import dk.dtu.businesslogic.models.TokenResult;
import dk.dtu.businesslogic.repositories.TokenRepository;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final MessageQueue queue;
    private final TokenRepository tokenRepository;

    public TokenService(MessageQueue q) {
        this.queue = q;
        queue.addHandler("TokensRequested", this::policyTokensRequested);
        queue.addHandler("PaymentRequested", this::policyPaymentRequested);
        tokenRepository = new TokenRepository();
    }

    private void policyPaymentRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        UUID token = event.getArgument(1, UUID.class);
        try{
            String customerId = validateToken(token);
            queue.publish(new Event("TokenValidated", correlationId, customerId));
        } catch (InvalidTokenException e) {
            log.error("e:", e);
            queue.publish(new Event("TokenValidationFailed", correlationId, "Invalid Token"));
        } catch (Exception e) {
            log.error("e: ", e);
            queue.publish(new Event("TokenValidationFailed", correlationId, "Error Validating Token"));
        }
    }

    private String validateToken(UUID tokenId) throws InvalidTokenException {
        Token token;
        try {
            token = tokenRepository.getTokenByValue(tokenId);
        } catch (TokenNotFoundException e) {
            throw new InvalidTokenException("Token " + tokenId+ "  not found");
        }
        if(token.isNotUsed()){
            revokeToken(token);
            return token.getCustomerId();
        }
        else{
            throw new InvalidTokenException("Token " + tokenId + " was already used");
        }

    }

    private void revokeToken(Token token) {
        token.setUsed(true);
        try {
            tokenRepository.modifyToken(token);
        } catch (TokenNotFoundException e) {
            throw new RuntimeException("Error modifying Token", e);
        }
    }

    private void policyTokensRequested(Event event) {
        CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
        var customerId = event.getArgument(1, String.class);
        var amount = event.getArgument(2, Integer.class);
        try {
            List<TokenResult> lstTokens = getTokens(customerId, amount);
            queue.publish(new Event("TokensGenerated", correlationId, lstTokens));
        } catch (DuplicateTokenUUIDException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TokenResult> getTokens(String customerId, int amount) throws DuplicateTokenUUIDException {
        //Customer can only have at most 6 tokens
        //Customer can only request 1 to 5 tokens
        var listTokens = tokenRepository.getUnusedTokensByCustomerId(customerId);
        if (amount >= 1 && amount <= 5) {
            if (listTokens.isEmpty()) {
                generateTokens(customerId, amount);
                var result = tokenRepository.getUnusedTokensByCustomerId(customerId);
                return result.stream().map(token -> new TokenResult(token.getTokenId())).toList();
            }
            else if (amount + listTokens.size() <= 5) {
                generateTokens(customerId, amount);
                var result = tokenRepository.getUnusedTokensByCustomerId(customerId);
                return result.stream().map(token -> new TokenResult(token.getTokenId())).toList();
            }
            else {
                throw new IllegalArgumentException("Number of requested tokens exceeds the limit of allowed tokens");
            }
        }
        else{
            throw new IllegalArgumentException("Amount must be between 1 and 5");
        }
    }

    private void generateTokens(String customerId, int amount) throws DuplicateTokenUUIDException {
        for (int i = 0; i < amount; i++){
            Token token = new Token(UUID.randomUUID(), customerId);
            tokenRepository.saveToken(token);
        }
    }
}
