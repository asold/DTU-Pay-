Feature: Payment
  Scenario: Invalid Token
    Given the payment is made with "Invalid token" error
    When the "TokenValidationFailed" event is handled
    Then the future completes exceptionally with an "InvalidPaymentException"
