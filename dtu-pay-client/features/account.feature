#Feature: Account Feature
#
#  Scenario: Successful customer registration on DTUPay
#    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
#    And the customer is registered with the bank with an initial balance of 1000 kr
#    When the customer registers with DTUPay
#    Then the registration is succesfull
#    And the customer id is returned from DTUPay
#
#  Scenario: Successful customer deregistration on DTUPay
#    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
#    And the customer is registered with DTUPay
#    When the customer deregisters from DTUPay
#    Then the deregistration is succesfull
#
#  Scenario: Successful merchant registration on DTUPay
#    Given a merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
#    And the merchant is registered with the bank with an initial balance of 1000 kr
#    When the merchant registers in DTU Pay
#    Then the registration is succesfull
#    And the merchant id is returned from DTUPay
#
#  Scenario: Successful merchant deregistration on DTUPay
#    Given a merchant with name "Daniel", last name "Oliver", and CPR "241902-7253"
#    And the merchant is registered with DTUPay
#    When the merchant deregisters from DTUPay
#    Then the deregistration is succesfull
#
#  Scenario: Customer fails to register with the same bank account more than once
#    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
#    And the customer is registered with the bank with an initial balance of 1000 kr
#    When the customer registers with DTUPay
#    Then the registration fails
#    And the error message "Bank account already registered" is returned
#
#
#  Scenario: Merchant fails to register with the same bank account more than once
#    Given a merchant with name "Susan", last name "Baldwin", and CPR "250103-7220"
#    And the merchant is registered with the bank with an initial balance of 1000 kr
#    When the merchant registers with DTUPay
#    Then the registration fails
#    And the error message "Bank account already registered" is returned
#
#  Scenario: Customer fails to register with empty bank account
#    Given a customer with name "Susan", last name "Baldwin", and CPR "250103-7220"
#    And the customer is not registered with the bank
#    When the customer registers with DTUPay
#    Then the registration fails
#    And the error message "Bank account number is required for registration" is returned
#
#  Scenario: Customer fails to register with empty name
#    Given a customer with name "", last name "", and CPR "250103-7220"
#    And the customer has a bank account
#    When the customer registers with DTUPay
#    Then the registration fails
#    And the error message "First Name is required for registration" is returned
#
#  Scenario: Customer fails to register with empty cpr
#    Given a customer with name "Susan", last name "Baldwin", and CPR ""
#    And the customer has a bank account
#    When the customer registers with DTUPay
#    Then the registration fails
#    And the error message "CPR is required for registration" is returned
#
#  Scenario: Merchant fails to register with empty bank account
#    Given a merchant with name "Daniel", last name "Baldwin", and CPR "250103-7220"
#    And the merchant is not registered with the bank
#    When the merchant registers with DTUPay
#    Then the registration fails
#    And the error message "Bank account number is required for registration" is returned
#
#  Scenario: Merchant fails to register with empty name
#    Given a merchant with name "Daniel", last name "Oliver", CPR ""
#    And the merchant has a bank account
#    When the merchant registers with DTUPay
#    Then the registration fails
#    And the error message "CPR is required for registration" is returned
#
#  Scenario: Merchant fails to register with empty cpr
#    Given a merchant with name "Daniel", last name "Oliver", CPR ""
#    And the merchant has a bank account
#    When the merchant registers with DTUPay
#    Then the registration fails
#    And the error message "CPR is required for registration" is returned
#
#
