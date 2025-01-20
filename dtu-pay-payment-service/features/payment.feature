Feature: Payment
  Scenario: Successful payment
    Given there is a "PaymentRequested" event received
    And there is a "CustomerBankAccountRetrieved" event received
    And there is a "MerchantBankAccountRetrieved" event received
    When the "PaymentRequested" event is handled
    And the "CustomerBankAccountRetrieved" event is handled
    And the "MerchantBankAccountRetrieved" event is handled
    Then the "PaymentProcessed" event is published

  Scenario: Negative amount requested
    Given there is a "PaymentRequested" event received
    And the payment amount is -10
    And there is a "CustomerBankAccountRetrieved" event received
    And there is a "MerchantBankAccountRetrieved" event received
    When the "PaymentRequested" event is handled
    And the "CustomerBankAccountRetrieved" event is handled
    And the "MerchantBankAccountRetrieved" event is handled
    Then the "NegativeAmountRequested" event is published

  Scenario: Creditor account not found
    Given there is a "PaymentRequested" event received
    And there is a "CustomerBankAccountRetrieved" event received
    And the customer bank account is not registered in the bank
    And there is a "MerchantBankAccountRetrieved" event received
    When the "PaymentRequested" event is handled
    And the "CustomerBankAccountRetrieved" event is handled
    And the "MerchantBankAccountRetrieved" event is handled
    Then the "DebtorAccountNotFound" event is published

  Scenario: Creditor account not found
    Given there is a "PaymentRequested" event received
    And there is a "CustomerBankAccountRetrieved" event received
    And the merchant bank account is not registered in the bank
    And there is a "MerchantBankAccountRetrieved" event received
    When the "PaymentRequested" event is handled
    And the "CustomerBankAccountRetrieved" event is handled
    And the "MerchantBankAccountRetrieved" event is handled
    Then the "CreditorAccountNotFound" event is published