#Feature: Token Generation
#  Scenario: Successful token generation with 0 tokens
#    Given a customer of DTU pay
#    And the customer has 0 valid tokens
#    When the customer requets 1 tokens
#    Then 1 tokens are generated for the customer
#
#  Scenario: Successful token generation with 1 token
#    Given a customer of DTU pay
#    And the customer has 1 valid tokens
#    When the customer requests 5 tokens
#    Then 5 tokens are generated for the customer
#
#  Scenario: Unsuccessful token generation with 6 token
#    Given a customer of DTU pay
#    And the customer has 6 valid tokens
#    When the customer requests 2 tokens
#    Then 0 tokens are generated for the customer
#    And the customer receives the error message "Customer already has to many valid tokens"
#
#  Scenario: Unsuccessful token generation when requesting 0 tokens
#    Given a customer of DTU pay
#    When the customer requests 0 tokens
#    Then 0 tokens are generated for the customer
#    Then the customer receives the error message "Amount of tokens requested must be between 1 to 5"
#
#  Scenario: Unsuccessful token generation when requesting 6 tokens
#    Given a customer of DTU pay
#    When the customer requests 6 tokens
#    Then 0 tokens are generated for the customer
#    Then the customer receives the error message "Amount of tokens requested must be between 1 to 5"
#
#  Scenario: Token generation fails when initiated by a customer
#    Given a customer not registered in DTUPay
#    When the customer requests 1 token
#    Then 0 tokens are generated for the customer
#    And the error message "Invalid merchant" is returned
