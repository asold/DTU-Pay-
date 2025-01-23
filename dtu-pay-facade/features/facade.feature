Feature: Facade
  Scenario: Register customer request
    Given Given a customer registration request where the first name is "Bob" and the last name "Fred" and the cpr is "120999-7319" and the account number is "012345678"
    When the customer registration request handled
    Then a "CustomerAccountRegistrationRequested" event published
    And the event contains a customer with the first name "Bob" the last name "Fred" the cpr is "120999-7319" and the account number is "012345678"

  Scenario: Deregister customer request
    Given a customer registered customer with ID "12345"
    When the customer deregistration request handled
    Then a "CustomerAccountDeregistrationRequested" event published
    And the event contains a customer id with a value of "12345"

  Scenario: Register merchant request
    Given Given a merchant registration request where the first name is "Bob" and the last name "Fred" and the cpr is "120999-7319" and the account number is "012345678"
    When the merchant registration request handled
    Then a "MerchantAccountRegistrationRequested" event published
    And the event contains a merchant with the first name "Bob" the last name "Fred" the cpr is "120999-7319" and the account number is "012345678"

  Scenario: Deregister merchant request
    Given Given a merchant registered with ID "12345"
    When the merchant deregistration request handled
    Then a "MerchantAccountDeregistrationRequested" event published
    And the event contains a merchant id with a value of "12345"

  Scenario: Payment request
    Given Given payment where the merchant id is "123" and the amount is 3
    When the payment request handled
    Then a "PaymentRequested" event published
    And the event contains a payment where the amount is 3 and the merchant id is "123"

  Scenario: Customer record request
    Given a customer registered customer with ID "12345"
    When the customer record request handled
    Then a "CustomerReportRequested" event published
    And the event contains a customer id with a value of "12345"

  Scenario: Merchant record request
    Given a merchant registered with Id "12345"
    When the merchant record request handled
    Then a "MerchantReportRequested" event published
    And the event contains a merchant id with a value of "12345"

  Scenario: Manager record request
    When a manager record request handled
    Then a "ManagerReportRequested" event published

  Scenario: Token request
    Given a customer registered customer with ID "12345"
    When the customer requests 4 tokens
    And the token request handled
    Then a "TokensRequested" event published
    And the event contains the amount of 4 and the customer id "12345"




