#@author Simão Teixeira (s232431)
@Token
Feature: Token Generation

  Scenario: Successful token generation
    Given a customer of DTU pay
    When the customer requests 1 tokens
    Then 1 tokens are generated for the customer

  Scenario: Successful token generation with 1 token
    Given a customer of DTU pay
    When the customer requests 1 tokens
    And the customer requests 5 tokens
    Then 6 tokens are generated for the customer

  Scenario: Unsuccessful token generation with more then 6 tokens
    Given a customer of DTU pay
    When the customer requests 3 tokens
    Then 3 tokens are generated for the customer
    When the customer requests 4 tokens
    Then the customer receives the error message "The number of requested tokens exceeds the limit of allowed tokens per customer"

  Scenario: Unsuccessful token generation when requesting 0 tokens
    Given a customer of DTU pay
    When the customer requests 0 tokens
    Then 0 tokens are generated for the customer
    And the customer receives the error message "Amount of requested tokens must be between 1 and 5"

  Scenario: Unsuccessful token generation when requesting 6 tokens
    Given a customer of DTU pay
    When the customer requests 6 tokens
    Then 0 tokens are generated for the customer
    And the customer receives the error message "Amount of requested tokens must be between 1 and 5"

  Scenario: Token generation fails when initiated by a unknown customer
    Given a customer not registered in DTUPay
    When the customer requests 1 tokens
    Then 0 tokens are generated for the customer
    And the customer receives the error message "Invalid Customer Account"
