#@author Simão Teixeira (s232431)
Feature: Token Service

  Scenario: Successful token generation
    Given a customer with 0 tokens
    When a "TokensRequested" event with amount 5 is published
    And a "CustomerAccountValidated" event with isValidAccount true is sent
    Then the "TokensGenerated" event is published with 5 tokens

  Scenario: Unsuccessful token generation due to number of tokens requested greater then 5
    Given a customer with 0 tokens
    When a "TokensRequested" event with amount 6 is published
    And a "CustomerAccountValidated" event with isValidAccount true is sent
    Then the "TokensGeneratedFailed" event is published

  Scenario: Unsuccessful token generation due to number of tokens requested is 0
    Given a customer with 0 tokens
    When a "TokensRequested" event with amount 0 is published
    And a "CustomerAccountValidated" event with isValidAccount true is sent
    Then the "TokensGeneratedFailed" event is published

  Scenario: Unsuccessful token generation due to customer already has more than 1 token
    Given a customer with 4 tokens
    When a "TokensRequested" event with amount 4 is published
    And a "CustomerAccountValidated" event with isValidAccount true is sent
    Then the "TokensGeneratedFailed" event is published

  Scenario: Unsuccessful token generation with invalid user
    Given a customer with 0 tokens
    When a "TokensRequested" event with amount 6 is published
    And a "CustomerAccountValidated" event with isValidAccount false is sent
    Then the "TokensGeneratedFailed" event is published

  Scenario: Successful token generation (events inverted)
    Given a customer with 0 tokens
    When a "CustomerAccountValidated" event with isValidAccount true is sent
    And a "TokensRequested" event with amount 5 is published
    Then the "TokensGenerated" event is published with 5 tokens

  Scenario: Successful Token Validation during payment
    Given a customer with 1 tokens
    When a "PaymentRequested" event with "valid" token is published
    Then the "TokenValidated" event is published with the customerID

  Scenario: Unsuccessful Token Validation during payment due to invalid token
    When a "PaymentRequested" event with "invalid" token is published
    Then the "TokenValidationFailed" event is published



