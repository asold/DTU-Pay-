package dk.dtu.businesslogic.services;

import dk.dtu.businesslogic.exceptions.DuplicateTokenUUIDException;
import dk.dtu.businesslogic.models.Token;
import dk.dtu.businesslogic.repositories.TokenRepository;
import messaging.Event;
import messaging.MessageQueue;

import java.util.List;
import java.util.UUID;

public class TokenService {

    private MessageQueue queue;
    private final TokenRepository tokenRepository;

    public TokenService(MessageQueue q) {
        this.queue = q;
        queue.addHandler("TokensRequested", this::policyTokensRequested);
        tokenRepository = new TokenRepository();
    }

    private void policyTokensRequested(Event event) {
        var customerId = event.getArgument(0, String.class);
        var amount = event.getArgument(1, Integer.class);
        try {
            getTokens(customerId, amount);
        } catch (DuplicateTokenUUIDException e) {
            throw new RuntimeException(e);
        }
    }

    public void getTokens(String customerId, int amount) throws DuplicateTokenUUIDException {
        //Customer can only have at most 6 tokens
        //Customer can only request 1 to 5 tokens
        var listTokens = tokenRepository.getUnusedTokensByCustomerId(customerId);
        if (amount >= 1 && amount <= 5) {
            if (listTokens.isEmpty()) {
                generateTokens(customerId, amount);
                var result = tokenRepository.getUnusedTokensByCustomerId(customerId);
                System.out.println("publishing user had no tokens");
                queue.publish(new Event("TokensGenerated", result ));
            }
            else if (amount + listTokens.size() <= 5) {
                generateTokens(customerId, amount);
                var result = tokenRepository.getUnusedTokensByCustomerId(customerId);
                System.out.println("publishing user had some tokens");
                queue.publish(new Event("TokensGenerated", result));
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
            Token token = new Token(customerId, UUID.randomUUID(), false);
            tokenRepository.saveToken(token);
        }
    }



}
