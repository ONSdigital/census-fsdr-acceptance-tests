@Acceptance
Feature: Get Worker Tests

  Scenario: As FSDR I can get a workers details from a response from Adecco
    Given Adecco has created a worker with an employee ID of "123456789"
    Then I can retrieve the workers information for an employee with ID "123456789"