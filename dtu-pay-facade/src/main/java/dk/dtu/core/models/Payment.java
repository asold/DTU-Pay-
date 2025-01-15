package dk.dtu.core.models;

import org.jmolecules.ddd.annotation.ValueObject;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Models a payment value object holding all information to describe a payment made by a customer.
 *
 * @author Karrar Adam s230432
 */
@ValueObject
public final class Payment {

    private String merchantId;
    private UUID token;
    private BigDecimal amount;

    public Payment(String merchantId, UUID token, BigDecimal amount) {
        this.merchantId = merchantId;
        this.token = token;
        this.amount = amount;
    }

    public Payment() {}

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(merchantId, payment.merchantId) && Objects.equals(token, payment.token) && Objects.equals(amount, payment.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(merchantId, token, amount);
    }
}
