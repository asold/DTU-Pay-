package dk.dtu.businesslogic.models;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Policy for Obtaining the Tokens (it must wait for multiple events to be handled)
 *
 * @author Simão Teixeira (s232431)
 */
public final class TokensRequestPolicy {
    private String customerId;
    private Integer amount;
    private Boolean isValidAccount;
    private final AtomicBoolean executed = new AtomicBoolean(false);

    public TokensRequestPolicy() {
        this.customerId = null;
        this.amount = null;
        this.isValidAccount = null;
    }

    public TokensRequestPolicy(String customerId, int amount, boolean accountValid) {
        this.customerId = customerId;
        this.amount = amount;
        this.isValidAccount = accountValid;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getIsValidAccount() {
        return isValidAccount;
    }

    public void setIsValidAccount(Boolean validAccount) {
        isValidAccount = validAccount;
    }

    public boolean allowsExecution() {
        if (customerId != null && amount != null && isValidAccount != null && !executed.get()) {
            return executed.compareAndSet(false, true);
        }
        return false;
    }
}
