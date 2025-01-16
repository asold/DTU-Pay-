package dk.dtu.adapters;

import dk.dtu.core.models.TokenResult;
import io.cucumber.messages.internal.com.google.common.reflect.TypeToken;
import jakarta.inject.Singleton;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
@Singleton
public class TokenFacade {

    private CompletableFuture<List<TokenResult>> tokens;

    private final MessageQueue queue;

    public TokenFacade() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public TokenFacade(RabbitMqQueue q) {
        queue = q;
        q.addHandler("TokensGenerated", this::policyTokensGenerated);
    }

    private void policyTokensGenerated(Event e){
        Type listType = new TypeToken<List<TokenResult>>() {}.getType();

        List<TokenResult> tokenResults = e.getArgument(0, listType);
        tokens.complete(tokenResults);
    }

    public List<TokenResult> getTokens(String id, int amount) throws ExecutionException, InterruptedException {
        tokens = new CompletableFuture<>();
        queue.publish(new Event("TokensRequested", id, amount));

        System.out.println("---------------" + tokens);
        System.out.println("------------- customerTokens: " + tokens.getClass());
        for (Object token : tokens.get()) {
            System.out.println("++++++++++++++++++++++ Element type: " + token.getClass());
        }


        return tokens.get();
    }

}
