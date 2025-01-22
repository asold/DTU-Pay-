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

  Scenario: Successful manager report generation
    Given 3 payment between a customer and a merchant
    When the manager requests for a report
    Then A report with all payment(s) is generated successfully
    And the report contains at least given 3 payment(s)
