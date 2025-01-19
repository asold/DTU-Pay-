package dk.dtu.businesslogic.repositories;


import dk.dtu.businesslogic.models.PaymentLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jeppe Jensen 233488
 */
public class ReportRepository {

    private Set<PaymentLog> paymentLogs = new HashSet<>();

    public ReportRepository() {}

    public void addPaymentLog(PaymentLog paymentLog) {
        paymentLogs.add(paymentLog);
    }

    public List<PaymentLog> getPaymentLogsByCustomerId(String id) {
        return paymentLogs.stream().filter(p -> p.getCustomerId().equals(id)).toList();
    }

    public List<PaymentLog> getPaymentLogsByMerchantId(String id) {
        return paymentLogs.stream().filter(p -> p.getMerchantId().equals(id)).toList();
    }

    public List<PaymentLog> getPaymentLogs() {
        return paymentLogs.stream().toList();
    }
}
