@Payment
Feature: Payment

  Scenario: Successful Payment
    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    And the customer has 1 valid token from DTU Pay
    And a merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr using the customer's token
    Then the payment is successful
    And the balance of the customer at the bank is 990 kr
    And the balance of the merchant at the bank is 1010 kr
    And the customer's token is no longer valid

  Scenario: Payment fails due to insufficient funds
    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    And the customer has 1 valid token from DTU Pay
    And a merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with DTU Pay using their bank account
    When the merchant initiates a payment for 9999 kr using the customer's token
    Then the payment is not successful
    And the balance of the customer at the bank is 1000 kr
    And the balance of the merchant at the bank is 1000 kr
    And the customer's token is no longer valid

  Scenario: Payment fails with invalid token
    Given a customer with name "Arwen", last name "Undomiel", and CPR "250103-7220"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    And the customer has tokens, but they are invalid
    And a merchant with name "Aragorn", last name "Ranger", and CPR "241902-7253"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr using the customer's token
    Then an error message "Invalid Token" is returned
    And the balance of the customer at the bank is 1000 kr
    And the balance of the merchant at the bank is 1000 kr

  Scenario: Payment fails with non existent customer bank account
    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the customer is not registered with the bank
    And the customer is registered with DTU Pay using their bank account
    And the customer has 1 valid token from DTU Pay
    And a merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr using the customer's token
    Then an error message "DebtorAccountNotFound" is returned
    And the balance of the merchant at the bank is 1000 kr

  Scenario: Payment fails with non existent merchant bank account
    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    And the customer has 1 valid token from DTU Pay
    And a merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
    And the merchant is not registered with the bank
    And the merchant is registered with DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr using the customer's token
    Then an error message "CreditorAccountNotFound" is returned
    And the balance of the customer at the bank is 1000 kr


  Scenario: Payment fails with a negative amount
    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    And the customer has 1 valid token from DTU Pay
    And a merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with DTU Pay using their bank account
    When the merchant initiates a payment for -10 kr using the customer's token
    Then an error message "NegativeAmountRequested" is returned
    And the balance of the customer at the bank is 1000 kr
    And the balance of the merchant at the bank is 1000 kr

  Scenario: Payment fails when initiated by a unregistered merchant
    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    And the customer has 1 valid token from DTU Pay
    And a merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is not registered with DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr using the customer's token
    Then an error message "Merchant not found" is returned
    And the balance of the customer at the bank is 1000 kr
    And the balance of the merchant at the bank is 1000 kr