@Acceptance
Feature: Pull from Adecco

  Scenario: As FSDR populate database
    Given Adecco has created a worker with an employee
    Then as FSDR system I can pull off Adecco
    And search database for "adecco" employee with ID "123456789"