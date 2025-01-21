package dk.dtu.businesslogic.models;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jeppe Jensen 233488
 */
public class PaymentLog {
    private String merchantId;
    private UUID tokenId; //Merchants ID used for the payment
    private BigDecimal amount;
    private String customerId;
    private boolean paymentSuccessful;
    private final AtomicBoolean executed = new AtomicBoolean(false);


    public PaymentLog() {

    }

    public UUID getTokenId() {
        return tokenId;
    }

    public void setTokenId(UUID tokenId) {
        this.tokenId = tokenId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    public void setPaymentSuccessful(boolean paymentSuccessful) {
        this.paymentSuccessful = paymentSuccessful;
    }

    public boolean allowsExecution() {
        if (merchantId != null && tokenId != null && amount != null && customerId != null
                && !paymentSuccessful && !executed.get()) {
            return executed.compareAndSet(false, true);
        }
        return false;
    }
}
