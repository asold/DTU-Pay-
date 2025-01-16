package dk.dtu.businesslogic.repositories;

import dk.dtu.businesslogic.exceptions.DuplicateTokenUUIDException;
import dk.dtu.businesslogic.exceptions.TokenNotFoundException;
import dk.dtu.businesslogic.models.Token;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class TokenRepository {

    private final List<Token> tokens;

    public TokenRepository() {
        this.tokens = new ArrayList<>();
    }

    public Token saveToken(Token token) throws DuplicateTokenUUIDException {
        if(this.tokens.stream().noneMatch(x -> x.getTokenId().equals(token.getTokenId()))) {
            this.tokens.add(token);
            return token;
        }
        else {
            throw new DuplicateTokenUUIDException(token.getTokenId().toString());
        }
    }

    public Token getTokenByValue(UUID tokenValue) throws TokenNotFoundException {
        return tokens.stream()
                .filter(t -> tokenValue.equals(t.getTokenId()))
                .findAny()
                .orElseThrow(() -> new TokenNotFoundException(tokenValue.toString()));
    }

    public List<Token> getTokensByCustomerId(String customerId) {
        return tokens.stream()
                .filter(x -> x.getCustomerId().equals(customerId))
                .toList();
    }

    public List<Token> getUnusedTokensByCustomerId(String customerId) {
        return tokens.stream()
                .filter(x -> x.getCustomerId().equals(customerId) && x.isNotUsed())
                .toList();
    }

    public boolean modifyToken(Token token) throws TokenNotFoundException {
        var tokenElement = tokens.stream()
                .filter(t -> t.getTokenId().equals(token.getTokenId()))
                .findAny()
                .orElseThrow(() -> new TokenNotFoundException(token.getTokenId().toString()));
        this.tokens.remove(tokenElement);
        return this.tokens.add(token);
    }

    public void removeToken(Token token) throws TokenNotFoundException {
        var tokenElement = tokens.stream()
                .filter(t -> t.getTokenId().equals(token.getTokenId()))
                .findAny()
                .orElseThrow(() -> new TokenNotFoundException(token.getTokenId().toString()));
        this.tokens.remove(tokenElement);
    }
}