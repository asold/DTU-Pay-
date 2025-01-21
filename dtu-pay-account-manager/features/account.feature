Feature: Account

  Scenario: Successful customer registration
    Given a customer first name is "Bob" and the last name "Fred" and the cpr is "120999-7333" and the account number is "123456789"
    And an event called "CustomerAccountRegistrationRequested" received
    When the "CustomerAccountRegistrationRequested" is handled
    Then a "CustomerRegistered" event published

  Scenario: Successful merchant registration
    Given a merchant first name is "Alice" and the last name "Kris" and the cpr is "120999-7311" and the account number is "012345678"
    And an event called "MerchantAccountRegistrationRequested" received
    When the "MerchantAccountRegistrationRequested" is handled
    Then a "MerchantRegistered" event published

  Scenario: Unsuccessful customer registration due to invalid first name
    Given a customer first name is "" and the last name "Fred" and the cpr is "120999-7333" and the account number is "123456789"
    And an event called "CustomerAccountRegistrationRequested" received
    When the "CustomerAccountRegistrationRequested" is handled
    Then a "CustomerRegistrationFailed" event published

  Scenario: Unsuccessful merchant registration due to invalid first name
    Given a merchant first name is "" and the last name "Fred" and the cpr is "120999-7333" and the account number is "123456789"
    And an event called "MerchantAccountRegistrationRequested" received
    When the "MerchantAccountRegistrationRequested" is handled
    Then a "MerchantRegistrationFailed" event published

  Scenario: Unsuccessful customer registration due to invalid last name
    Given a customer first name is "Joe" and the last name "" and the cpr is "120999-7333" and the account number is "123456789"
    And an event called "CustomerAccountRegistrationRequested" received
    When the "CustomerAccountRegistrationRequested" is handled
    Then a "CustomerRegistrationFailed" event published

  Scenario: Unsuccessful merchant registration due to invalid last name
    Given a merchant first name is "Joe" and the last name "" and the cpr is "120999-7333" and the account number is "123456789"
    And an event called "MerchantAccountRegistrationRequested" received
    When the "MerchantAccountRegistrationRequested" is handled
    Then a "MerchantRegistrationFailed" event published

  Scenario: Unsuccessful customer registration due to invalid cpr
    Given a customer first name is "Joe" and the last name "Kris" and the cpr is "" and the account number is "123456789"
    And an event called "CustomerAccountRegistrationRequested" received
    When the "CustomerAccountRegistrationRequested" is handled
    Then a "CustomerRegistrationFailed" event published

  Scenario: Unsuccessful merchant registration due to invalid cpr
    Given a merchant first name is "Joe" and the last name "Kris" and the cpr is "" and the account number is "123456789"
    And an event called "MerchantAccountRegistrationRequested" received
    When the "MerchantAccountRegistrationRequested" is handled
    Then a "MerchantRegistrationFailed" event published

  Scenario: Unsuccessful customer registration due to invalid bank account number
    Given a customer first name is "Joe" and the last name "Kris" and the cpr is "120999-7333" and the account number is ""
    And an event called "CustomerAccountRegistrationRequested" received
    When the "CustomerAccountRegistrationRequested" is handled
    Then a "CustomerRegistrationFailed" event published

  Scenario: Unsuccessful merchant registration due to invalid bank account number
    Given a merchant first name is "Joe" and the last name "Kris" and the cpr is "120999-7333" and the account number is ""
    And an event called "MerchantAccountRegistrationRequested" received
    When the "MerchantAccountRegistrationRequested" is handled
    Then a "MerchantRegistrationFailed" event published

  Scenario: Successful customer deregistration
    Given a customer first name is "Joe" and the last name "Kris" and the cpr is "120999-7333" and the account number is "123456789"
    And an event called "CustomerAccountDeregistrationRequested" received
    When the "CustomerAccountDeregistrationRequested" is handled
    Then a "CustomerDeregistered" event published

  Scenario: Successful merchant deregistration
    Given a merchant first name is "Joe" and the last name "Kris" and the cpr is "120999-7333" and the account number is "123456789"
    And an event called "MerchantAccountDeregistrationRequested" received
    When the "MerchantAccountDeregistrationRequested" is handled
    Then a "MerchantDeregistered" event published

  Scenario: Successful customer account retrievement
    Given a customer first name is "Bob" and the last name "Fred" and the cpr is "120999-7333" and the account number is "123456789"
    And an event called "CustomerAccountRegistrationRequested" received
    And the "CustomerAccountRegistrationRequested" is handled
    And a "CustomerRegistered" event published
    And an event called "TokenValidated" received
    When the "TokenValidated" is handled
    Then a "CustomerBankAccountRetrieved" event published

  Scenario: Successful merchant account retrievement
    Given a merchant first name is "Bob" and the last name "Fred" and the cpr is "120999-7319" and the account number is "012345678"
    And an event called "MerchantAccountRegistrationRequested" received
    And the "MerchantAccountRegistrationRequested" is handled
    And a "MerchantRegistered" event published
    And an event called "PaymentRequested" received
    When the "PaymentRequested" is handled
    Then a "MerchantBankAccountRetrieved" event published




