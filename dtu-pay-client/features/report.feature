@Report
Feature: Reporting

  Scenario: Successful customer report generation
    Given 1 payment between a customer and a merchant
    When the customer requests for a report
    Then the report with 1 payment(s) is generated successfully
    And the report contains the given 1 payment(s)

  Scenario: Successful merchant report generation
    Given 2 payment between a customer and a merchant
    When the merchant requests for a report
    Then the report with 2 payment(s) is generated successfully
    And the report contains the given 2 payment(s)

  #    ReportRepository  must be cleared of PaymentLog before running this test succesfully.
  #    Remove PaymentLog(s) when deregistrerer Customer, Merchant, or redefine scenario.

#  Scenario: Successful manager report generation
#    Given 3 payment between a customer and a merchant
#    When the manager requests for a report
#    Then the report with 3 payment(s) is generated successfully
#    And the report contains the given 3 payment(s)

  #    To implement this Scenario correctly ReportService must publish events to AccountManager to check
  #    if MerchantID is valid. ReportService does not know if MerchantID is in the system, only that
  #    no payment with merchant ID was made. Or redefine scenario.
  #    ReportService Should not keep this information If MerchantID is still valid Account.

#  Scenario: Report generation fails when initiated by an invalid merchant
#    Given a merchant not registered in DTUPay
#    When the merchant requests a report
#    Then report is not returned
#    And the error message "Invalid merchant" is returned

  #    To implement this Scenario correctly ReportService must publish events to AccountManager..As above scenario.

#  Scenario: Report generation fails when initiated by an invalid merchant
#    Given a customer not registered in DTUPay
#    When the customer requests a report
#    Then report is not returned
#    And the error message "Invalid customer" is returned
