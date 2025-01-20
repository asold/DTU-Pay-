Feature: Reporting

  Scenario: Successful customer report generation
    Given 1 payment between a customer and a merchant
    When the customer requests for a report
    Then the report with 1 payment is generated successfully
    And the report contains the given 1 payment

#  Scenario: Successful merchant report generation
#    Given 2 payment between a customer and a merchant
#    When the merchant requests for a report
#    Then the report is generated successfully
#    And the report contains the given payment(s)
#
#  Scenario: Successful manager report generation
#    Given 3 payment between a customer and a merchant
#    When the manager requests for a report
#    Then the report is generated successfully
#    And the report contains the given payment(s)
#
#
#  Scenario: Report generation fails when initiated by an invalid merchant
#    Given a merchant not registered in DTUPay
#    When the merchant requests a report
#    Then report is not returned
#    And the error message "Invalid merchant" is returned
#
#  Scenario: Report generation fails when initiated by an invalid customer
#    Given a customer not registered in DTUPay
#    When the customer requests a report
#    Then report is not returned
#    And the error message "Invalid customer" is returned
