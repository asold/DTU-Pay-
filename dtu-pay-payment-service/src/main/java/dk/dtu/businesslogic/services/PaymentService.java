package dk.dtu.businesslogic.services;

import dk.dtu.businesslogic.models.PaymentResponse;
import dtu.ws.fastmoney.BankServiceException_Exception;
import messaging.CorrelationId;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;

/**
 * @author  Andrei Soldan 243873
 */
public final class PaymentService {

    private final MessageQueue queue;
    private ConcurrentHashMap<CorrelationId, CreatePaymentCommand> pendingPaymentCommandsMap;
    private final BankService bankService = new BankServiceService().getBankServicePort();

    public PaymentService() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public PaymentService(MessageQueue queue) {
        this.queue = queue;
        queue.addHandler("PaymentRequested", this::paymentRequestedPolicy);
        queue.addHandler("CustomerBankAccountRetrieved", this::customerBankAccountRetrievedEventHandler);
        queue.addHandler("MerchantBankAccountRetrieved", this::merchantBankAccountRetrievedEventHandler);
        pendingPaymentCommandsMap = new ConcurrentHashMap<>();
    }

    public void paymentRequestedPolicy(Event event) {
        var amount = event.getArgument(3, BigDecimal.class);
        var correlationId = event.getArgument(0, CorrelationId.class);
        // Retrieve or create a new payment command
        var paymentCommand = pendingPaymentCommandsMap.computeIfAbsent(correlationId, k -> new CreatePaymentCommand());
        paymentCommand.setAmount(amount);
        executePaymentIfAllEventsArrived(correlationId);
    }

    public void customerBankAccountRetrievedEventHandler(Event event) {
        var customerBankAccount = event.getArgument(1, String.class);
        var correlationId = event.getArgument(0, CorrelationId.class);
        // Retrieve or create a new payment command
        var paymentCommand = pendingPaymentCommandsMap.computeIfAbsent(correlationId, k -> new CreatePaymentCommand());
        paymentCommand.setCustomerBankAccountId(customerBankAccount);
        executePaymentIfAllEventsArrived(correlationId);
    }

    public void merchantBankAccountRetrievedEventHandler(Event event) {
        var merchantBankAccount = event.getArgument(1, String.class);
        var correlationId = event.getArgument(0, CorrelationId.class);
        // Retrieve or create a new payment command
        var paymentCommand = pendingPaymentCommandsMap.computeIfAbsent(correlationId, k -> new CreatePaymentCommand());
        paymentCommand.setMerchantBankAccountId(merchantBankAccount);
        executePaymentIfAllEventsArrived(correlationId);
    }

    private void executePaymentIfAllEventsArrived(CorrelationId correlationId) {
        // Verify if all events were received
        var paymentCommand = pendingPaymentCommandsMap.get(correlationId);
        if (paymentCommand.getCustomerBankAccountId() != null && paymentCommand.getMerchantBankAccountId() != null && paymentCommand.getAmount() != null)
        {

            // Here it means we have received all the data for a payment
            // Execute Payment
            try
            {
                var paymentDescription = "Money transfer of amount:" + paymentCommand.getAmount();
                bankService.transferMoneyFromTo(paymentCommand.getCustomerBankAccountId(), paymentCommand.getMerchantBankAccountId(), paymentCommand.getAmount(), paymentDescription);
                // Because we took the payment id as the correlation id, we publish an event stating that
                // the payment was successful for that correlation id, which effectively can be interpreted as a payment id.
                var paymentResponse = new PaymentResponse(correlationId.getId(), true);
                var paymentSucceededEvent = new Event("PaymentProcessed", correlationId, paymentResponse);
                queue.publish(paymentSucceededEvent);
            } catch (BankServiceException_Exception e)
            {
                var paymentResponse = new PaymentResponse(correlationId.getId(),false);
                //Maybe the dependency on a text from the bank account is not that good here
                if(e.getMessage().equals("Debtor account does not exist")){
                    queue.publish(new Event("DebtorAccountNotFound", correlationId, "DebtorAccountNotFound"));
                    return;
                }
                if(e.getMessage().equals("Creditor account does not exist")){
                    queue.publish(new Event("CreditorAccountNotFound", correlationId, "CreditorAccountNotFound"));
                    return;
                }
                if(e.getMessage().equals("Amount must be positive")){
                    queue.publish(new Event("NegativeAmountRequested", correlationId, "NegativeAmountRequested"));
                    return;
                }

                var paymentFailureEvent = new Event("PaymentProcessed",correlationId, paymentResponse);

                queue.publish(paymentFailureEvent);
            }
            // Here we make sure to clean any information related to the payment saga.
            pendingPaymentCommandsMap.remove(correlationId);
        }
    }
}
