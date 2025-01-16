package dk.dtu.businesslogic.models;

import org.jmolecules.ddd.annotation.Entity;
import java.util.UUID;


@Entity
public final class Token {
    private final UUID tokenId;
    private final String customerId;
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
