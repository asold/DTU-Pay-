package dk.dtu.adapters;

import dk.dtu.core.models.Token;
import jakarta.inject.Singleton;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
@Singleton
public class TokenFacade {

    private CompletableFuture<List<Token>> tokens;

    private final MessageQueue queue;

    public TokenFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public TokenFacade(RabbitMqQueue q) {
        queue = q;
        q.addHandler("TokensGenerated", this::policyTokensGenerated);
    }

    private void policyTokensGenerated(Event e){
        tokens.complete(e.getArgument(0, List.class));
    }

    public List<Token> getTokens(String id, int amount) throws ExecutionException, InterruptedException {
        System.out.println("Trying to get tokens");
        tokens = new CompletableFuture<>();
        System.out.println("Publishing getting tokens");
        queue.publish(new Event("TokensRequested", id, amount));
        return tokens.get();
    }

}
