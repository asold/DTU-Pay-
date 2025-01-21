package dk.dtu.adapters;

import com.google.gson.reflect.TypeToken;
import dk.dtu.core.exceptions.TokenServiceException;
import dk.dtu.core.models.TokenResult;
import jakarta.inject.Singleton;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Token facade responsible to publish and handle events related with the tokens resource
 *
 * @author Simão Teixeira (s232431)
 */
@Singleton
public class TokenFacade {

    private final Map<CorrelationId, CompletableFuture<List<TokenResult>>> tokenRequests = new ConcurrentHashMap<>();

    private final MessageQueue queue;

    public TokenFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public TokenFacade(RabbitMqQueue q) {
        queue = q;
        q.addHandler("TokensGenerated", this::policyTokensGenerated);
        q.addHandler("TokensGeneratedFailed", this::policyTokensGeneratedFailed);
    }

    private void policyTokensGenerated(Event e){
        // This is done for serialization
        Type listType = new TypeToken<List<TokenResult>>() {}.getType();
        List<TokenResult> tokenResults = e.getArgument(1, listType);
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        CompletableFuture<List<TokenResult>> future = tokenRequests.get(correlationId);
        future.complete(tokenResults);
        tokenRequests.remove(correlationId);
    }

    private void policyTokensGeneratedFailed(Event e) {
        CorrelationId correlationId = e.getArgument(0, CorrelationId.class);
        CompletableFuture<List<TokenResult>> future = tokenRequests.get(correlationId);
        String errorCode = e.getArgument(1, String.class);
        String errorMessage = e.getArgument(2, String.class);
        future.completeExceptionally(new TokenServiceException(errorCode, errorMessage));
    }

    public List<TokenResult> getTokens(String id, int amount) throws ExecutionException, InterruptedException, TokenServiceException {
        CorrelationId correlationId = new CorrelationId();
        CompletableFuture<List<TokenResult>> tokens = new CompletableFuture<>();
        tokenRequests.put(correlationId, tokens);
        queue.publish(new Event("TokensRequested", correlationId, id, amount));
        try {
            return tokens.get();
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause() instanceof TokenServiceException) {
                throw (TokenServiceException) e.getCause();
            } else {
                throw e;
            }
        }
    }
}
