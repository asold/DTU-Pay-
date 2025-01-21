package dk.dtu.businesslogic.models;

import com.google.gson.annotations.Expose;
import org.jmolecules.ddd.annotation.Entity;

import java.util.UUID;

/**
 * Internal model of a token in the Token Manager Microservice
 *
 * @author Simão Teixeira (s232431)
 */
@Entity
public final class Token {
    private final UUID tokenId;
    @Expose(serialize = false)
    private final String customerId;
    @Expose(serialize = false)
    private boolean used;

    public Token(UUID tokenId, String customerId) {
        this.tokenId = tokenId;
        this.customerId = customerId;
        this.used = false;
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public boolean isNotUsed() {
        return !used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
