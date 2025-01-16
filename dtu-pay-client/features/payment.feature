Feature: Payment

  Scenario: Successful Payment
    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with Simple DTU Pay using their bank account
    And the customer has 1 valid token from DTU Pay
    And a merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with Simple DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr using the customer's token
    Then the payment is successful
#    And the balance of the customer at the bank is 990 kr
#    And the balance of the merchant at the bank is 1010 kr
#    And the customer's token is no longer valid