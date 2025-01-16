package dk.dtu.businesslogic.services;

import dk.dtu.businesslogic.modls.PaymentResponse;
import dtu.ws.fastmoney.BankServiceException_Exception;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;


public final class PaymentService {

    private final MessageQueue queue;
    private HashMap<UUID, CreatePaymentCommand> pendingPaymentCommandsMap;
    private final BankService bankService = new BankServiceService().getBankServicePort();

    public PaymentService() {
        this(new RabbitMqQueue("rabbitMq"));
    }

    public PaymentService(MessageQueue queue) {
        this.queue = queue;
        queue.addHandler("PaymentRequested", this::paymentRequestedPolicy);
        queue.addHandler("CustomerBankAccountRetrieved", this::customerBankAccountRetrievedEventHandler);
        queue.addHandler("MerchantBankAccountRetrieved", this::merchantBankAccountRetrievedEventHandler);
    }

    private void paymentRequestedPolicy(Event event) {
        var amount = event.getArgument(3, BigDecimal.class);
        var correlationId = event.getArgument(0, UUID.class);

        var existentPaymentCommand = pendingPaymentCommandsMap.get(correlationId);
        if (existentPaymentCommand == null)
        {
            // Here it means that this event was the first piece of information regarding the payment that was received.
            var paymentCommand = new CreatePaymentCommand();
            paymentCommand.setAmount(amount);
            pendingPaymentCommandsMap.put(correlationId, paymentCommand);
            return;
        }

        // Here the payment exists.
        var paymentCommand = pendingPaymentCommandsMap.get(correlationId);
        paymentCommand.setAmount(amount);
        executePaymentIfAllEventsArrived(correlationId);
    }

    private void customerBankAccountRetrievedEventHandler(Event event) {
        var customerBankAccount = event.getArgument(1, String.class);
        var correlationId = event.getArgument(0, UUID.class);

        var existentPaymentCommand = pendingPaymentCommandsMap.get(correlationId);
        if (existentPaymentCommand == null)
        {
            // Here it means that this event was the first piece of information regarding the payment that was received.
            var paymentCommand = new CreatePaymentCommand();
            paymentCommand.setCustomerBankAccountId(customerBankAccount);
            pendingPaymentCommandsMap.put(correlationId, paymentCommand);
            return;
        }

        // Here the payment exists.
        var paymentCommand = pendingPaymentCommandsMap.get(correlationId);
        paymentCommand.setCustomerBankAccountId(customerBankAccount);
        executePaymentIfAllEventsArrived(correlationId);
    }

    private void merchantBankAccountRetrievedEventHandler(Event event) {
        var merchantBankAccount = event.getArgument(1, String.class);
        var correlationId = event.getArgument(0, UUID.class);

        var existentPaymentCommand = pendingPaymentCommandsMap.get(correlationId);
        if (existentPaymentCommand == null)
        {
            // Here it means that this event was the first piece of information regarding the payment that was received.
            var paymentCommand = new CreatePaymentCommand();
            paymentCommand.setMerchantBankAccountId(merchantBankAccount);
            pendingPaymentCommandsMap.put(correlationId, paymentCommand);
            return;
        }

        // Here the payment exists.
        var paymentCommand = pendingPaymentCommandsMap.get(correlationId);
        paymentCommand.setMerchantBankAccountId(merchantBankAccount);
        executePaymentIfAllEventsArrived(correlationId);
    }

    private void executePaymentIfAllEventsArrived(UUID correlationId) {
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
                var paymentResponse = new PaymentResponse(correlationId, true);
                var paymentSucceededEvent = new Event("PaymentProcessed", paymentResponse);
                queue.publish(paymentSucceededEvent);
            } catch (BankServiceException_Exception e)
            {
                var paymentResponse = new PaymentResponse(correlationId,false);
                var paymentSucceededEvent = new Event("PaymentProcessed", paymentResponse);
                throw new RuntimeException(e);
            }

            // Here we make sure to clean any information related to the payment saga.
            pendingPaymentCommandsMap.remove(correlationId);
        }
    }
}
