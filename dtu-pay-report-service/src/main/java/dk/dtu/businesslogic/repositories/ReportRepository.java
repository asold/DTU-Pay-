package dk.dtu.businesslogic.repositories;


import dk.dtu.businesslogic.models.PaymentLog;

import java.util.ArrayList;
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
        return removeCustomerId(paymentLogs.stream().filter(p -> p.getCustomerId().equals(id)).toList());
    }

    public List<PaymentLog> getPaymentLogsByMerchantId(String id) {
        return removeCustomerId(paymentLogs.stream().filter(p -> p.getMerchantId().equals(id)).toList());
    }

    public List<PaymentLog> getPaymentLogs() {
        return paymentLogs.stream().toList();
    }

    public List<PaymentLog> removeCustomerId(List<PaymentLog> withIdList){
        List<PaymentLog> withTokenList = new ArrayList<PaymentLog>();
        for(int i = 0 ; i < withIdList.size(); i++) {

            PaymentLog tmpWithTokenLog = new PaymentLog();
            tmpWithTokenLog.setTokenId(withIdList.get(i).getTokenId());
            tmpWithTokenLog.setAmount(withIdList.get(i).getAmount());
            tmpWithTokenLog.setMerchantId(withIdList.get(i).getMerchantId());
            tmpWithTokenLog.setPaymentSuccessful(withIdList.get(i).isPaymentSuccessful());
            withTokenList.add(tmpWithTokenLog);
        }
        return withTokenList;
    }
}