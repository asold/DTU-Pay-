@Account
Feature: Account Feature

  Scenario: Successful customer registration on DTUPay
    Given a accountTest customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the accountTest customer is registered with the bank with an initial balance of 1000 kr
    When the accountTest customer registers with DTUPay
    Then the customer registration is successful

  Scenario: Successful customer deregistration on DTUPay
    Given a accountTest customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the accountTest customer is registered with the bank with an initial balance of 1000 kr
    And the accountTest customer is registered with DTUPay
    When the accountTest customer deregisters from DTUPay
    Then the customer account deregistration is successful

  Scenario: Successful merchant registration on DTUPay
    Given a accountTest merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
    And the accountTest merchant is registered with the bank with an initial balance of 1000 kr
    When the accountTest merchant registers in DTU Pay
    Then the merchant registration is successful

  Scenario: Successful merchant deregistration on DTUPay
    Given a accountTest merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
    And the accountTest merchant is registered with the bank with an initial balance of 1000 kr
    And the accountTest merchant is registered with DTUPay
    When the accountTest merchant deregisters from DTUPay
    Then the merchant account deregistration is successful

  Scenario: Customer fails to register with empty bank account
    Given a accountTest customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the accountTest customer is not registered with the bank
    When the accountTest customer registers with DTUPay
    Then the accountTest customer registration fails
    And the error message "Invalid bank account number" is returned

  Scenario: Customer fails to register with empty first name
    Given a accountTest customer with name "", last name "Bob", and CPR "250103-7220"
    And the accountTest customer is registered with the bank with an initial balance of 1000 kr
    When the accountTest customer registers with DTUPay
    Then the accountTest customer registration fails
    And the error message "Invalid first name" is returned

  Scenario: Customer fails to register with empty last name
    Given a accountTest customer with name "Alice", last name "", and CPR "250103-7220"
    And the accountTest customer is registered with the bank with an initial balance of 1000 kr
    When the accountTest customer registers with DTUPay
    Then the accountTest customer registration fails
    And the error message "Invalid last name" is returned

  Scenario: Customer fails to register with empty cpr
    Given a accountTest customer with name "Susan", last name "Baldwin", and CPR ""
    And the accountTest customer is registered with the bank with an initial balance of 1000 kr
    When the accountTest customer registers with DTUPay
    Then the accountTest customer registration fails
    And the error message "Invalid CPR" is returned

  Scenario: Merchant fails to register with empty bank account
    Given a accountTest merchant with name "Susan", last name "Baldwin", and CPR "250103-7220"
    And the accountTest merchant is not registered with the bank
    When the accountTest merchant registers in DTU Pay
    Then the accountTest merchant registration fails
    And the error message "Invalid bank account number" is returned

  Scenario: Merchant fails to register with empty first name
    Given a accountTest merchant with name "", last name "Bob", and CPR "250103-7220"
    And the accountTest merchant is registered with the bank with an initial balance of 1000 kr
    When the accountTest merchant registers in DTU Pay
    Then the accountTest merchant registration fails
    And the error message "Invalid first name" is returned

  Scenario: Merchant fails to register with empty last name
    Given a accountTest merchant with name "Alice", last name "", and CPR "250103-7220"
    And the accountTest merchant is registered with the bank with an initial balance of 1000 kr
    When the accountTest merchant registers in DTU Pay
    Then the accountTest merchant registration fails
    And the error message "Invalid last name" is returned

  Scenario: Merchant fails to register with empty cpr
    Given a accountTest merchant with name "Susan", last name "Baldwin", and CPR ""
    And the accountTest merchant is registered with the bank with an initial balance of 1000 kr
    When the accountTest merchant registers in DTU Pay
    Then the accountTest merchant registration fails
    And the error message "Invalid CPR" is returned


