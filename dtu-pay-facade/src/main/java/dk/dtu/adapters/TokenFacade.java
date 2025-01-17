package dk.dtu.adapters;

import dk.dtu.core.models.TokenResult;
import io.cucumber.messages.internal.com.google.common.reflect.TypeToken;
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
@Singleton
public class TokenFacade {

    private Map<CorrelationId, CompletableFuture<List<TokenResult>>> tokenRequests = new ConcurrentHashMap<>();

    private final MessageQueue queue;

    public TokenFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public TokenFacade(RabbitMqQueue q) {
        queue = q;
        q.addHandler("TokensGenerated", this::policyTokensGenerated);
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

    public List<TokenResult> getTokens(String id, int amount) throws ExecutionException, InterruptedException {
        CorrelationId correlationId = new CorrelationId();
        CompletableFuture<List<TokenResult>> tokens = new CompletableFuture<>();
        tokenRequests.put(correlationId, tokens);
        queue.publish(new Event("TokensRequested",correlationId, id, amount));
        return tokens.get();
    }

}
