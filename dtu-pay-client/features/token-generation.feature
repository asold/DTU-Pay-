Feature: Token Generation
  Scenario: Successful token generation with 0 tokens
    Given a customer of DTU pay
    And the customer has 0 valid tokens
    When the customer requets 1 tokens
    Then 1 tokens are generated for the customer


  Scenario: Successful token generation with 1 token
    Given a customer of DTU pay
    And the customer has 1 valid tokens
    When the customer requests 5 tokens
    Then 5 tokens are generated for the customer

  Scenario: Unsuccessful token generation with 6 token
    Given a customer of DTU pay
    And the customer has 6 valid tokens
    When the customer requests 2 token
    Then no new tokens are generated for the customer
    And the customer receives the error message ""


  Scenario: The