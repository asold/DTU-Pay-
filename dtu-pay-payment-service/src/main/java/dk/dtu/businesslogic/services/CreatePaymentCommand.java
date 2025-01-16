package dk.dtu.businesslogic.services;

import java.math.BigDecimal;

public final class CreatePaymentCommand {

    private String merchantBankAccountId;
    private String customerBankAccountId;
    private BigDecimal amount;

    public CreatePaymentCommand(){
        merchantBankAccountId = null;
        customerBankAccountId = null;
        amount = null;
    }

    public String getMerchantBankAccountId() {
        return merchantBankAccountId;
    }

    public void setMerchantBankAccountId(String merchantBankAccountId) {
        this.merchantBankAccountId = merchantBankAccountId;
    }

    public String getCustomerBankAccountId() {
        return customerBankAccountId;
    }

    public void setCustomerBankAccountId(String customerBankAccountId) {
        this.customerBankAccountId = customerBankAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CreatePaymentCommand{" +
                "merchantBankAccountId='" + merchantBankAccountId + '\'' +
                ", customerBankAccountId='" + customerBankAccountId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
