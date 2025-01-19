package dk.dtu.businesslogic.services;

import dk.dtu.businesslogic.models.PaymentLog;
import dk.dtu.businesslogic.models.PaymentResponse;
import dk.dtu.businesslogic.repositories.ReportRepository;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author  Jeppe Jensen 233488
 */
public class ReportService {

	private MessageQueue queue;
	private ReportRepository reportRepository;
	private ConcurrentHashMap<CorrelationId, PaymentLog> pendingPaymentLogs;

	private static final Logger logger = Logger.getLogger(ReportService.class.getName());

	public ReportService(MessageQueue q) {
		this.queue = q;

		queue.addHandler("PaymentRequested", this::retrievedMerchantIdAmount);
		queue.addHandler("TokenValidated", this::retrievedCustomerId);
		queue.addHandler("PaymentProcessed", this::retrievedPaymentSuccessful);

		reportRepository = new ReportRepository();
	}
	
	/* Policies */

	public void retrievedMerchantIdAmount(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		String merchantId = event.getArgument(2, String.class);
		BigDecimal amount = event.getArgument(3, BigDecimal.class);
		var pendingPaymentLog = pendingPaymentLogs.computeIfAbsent(correlationId, c -> new PaymentLog());
		pendingPaymentLog.setMerchantId(merchantId);
		pendingPaymentLog.setAmount(amount);
		storePaymentLogIfCompleted(correlationId);
	}

	public void retrievedCustomerId(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		String customerId = event.getArgument(1, String.class);
		var pendingPaymentLog = pendingPaymentLogs.computeIfAbsent(correlationId, c -> new PaymentLog());
		pendingPaymentLog.setCustomerId(customerId);
		storePaymentLogIfCompleted(correlationId);
	}

	public void retrievedPaymentSuccessful(Event event) {
		CorrelationId correlationId = event.getArgument(0, CorrelationId.class);
		boolean paymentSuccessful = event.getArgument(1, PaymentResponse.class).successful();
		var pendingPaymentLog = pendingPaymentLogs.computeIfAbsent(correlationId, c -> new PaymentLog());
		if(paymentSuccessful)
			pendingPaymentLog.setPaymentSuccessful(paymentSuccessful);
		else
			pendingPaymentLogs.remove(correlationId);
		storePaymentLogIfCompleted(correlationId);
	}

	private void storePaymentLogIfCompleted(CorrelationId correlationId) {
		// Verify if all events were received
		var pendingPaymentLog = pendingPaymentLogs.get(correlationId);
		if (pendingPaymentLog.getMerchantId() != null && pendingPaymentLog.getAmount() != null && pendingPaymentLog.getCustomerId() != null && pendingPaymentLog.isPaymentSuccessful() == true) {
			reportRepository.addPaymentLog(pendingPaymentLog);
			pendingPaymentLogs.remove(correlationId);
		}
	}

}
