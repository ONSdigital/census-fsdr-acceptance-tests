@Acceptance
Feature: Updates

  Scenario Outline: A record in FSDR with a device receives an update
    Given the managers of "<role_id>" exist
    And we ingest managers
    And An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And we ingest them
    And the employee "<id>" is sent to all downstream services
      ### LWS requires a device to be created ###
    And a device exists in XMA with "<role_id>", "0123456789" and "Allocated"
    And we retrieve the devices from xma
    And we ingest them
      ###
    And we receive an update from adecco for employee "<id>" with new first name "<new_name>"
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee is correctly updated in gsuite with name "<new_name>"
    Then the employee "<id>" is sent to LWS as an update with name "<new_name>" and phone number "0123456789" and "<role_id>"
    Then the employee "<id>" is correctly updated in ServiceNow with "<role_id>" and name "<new_name>" and number "0123456789"
    Then the employee from "<source>" with roleId "<role_id>" is correctly updated in XMA with name "<new_name>" and group "<group>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "0123456789" as an update with name "<new_name>"
    And Check the employee "<id>" is sent to RCA

    Examples:
      | id         | assignment_status | cr_status  | role_id       | inLogisitcs | source | new_name | group                                |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1          | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1          | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD |

  Scenario Outline: A record in FSDR receives a device
    Given the managers of "<role_id>" exist
    And we ingest managers
    And An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And we ingest them
    And the employee "<id>" is sent to all downstream services
    And a device exists in XMA with "<role_id>", "<phone_number>" and "<status>"
    And we retrieve the devices from xma
    And we run create actions
    When the employee "<id>" is sent to all downstream services
    Then the employee "<role_id>" is not updated in gsuite
    Then the employee "<id>" is correctly updated in ServiceNow with "<role_id>" and name "<name>" and number "<phone_number>"
    Then the employee "<id>" is sent to LWS as an update with name "<name>" and phone number "<phone_number>" and "<role_id>"
    Then the employee "<role_id>" is not updated in XMA
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as an update with name "<name>"
    And Check the employee "<id>" is sent to RCA

    Examples:
      | id         | assignment_status | cr_status  | role_id       | inLogisitcs | source | name     | phone_number | status    |
      | 123456781  | ASSIGNED          | ACTIVE     | CAR1          | is          | ADECCO | Fransico | 0123456781   | Allocated |
      | 123456782  | ASSIGNED          | ACTIVE     | CAR1-HA       | is          | ADECCO | Fransico | 0123456785   | Allocated |
      | 123456783  | ASSIGNED          | ACTIVE     | CAR1-HA-01    | is not      | ADECCO | Fransico | 0123456782   | Allocated |
      | 123456784  | ASSIGNED          | ACTIVE     | CAR1-SA       | is          | ADECCO | Fransico | 0123456783   | Allocated |
      | 123456785  | ASSIGNED          | ACTIVE     | CAR1-SA-01    | is not      | ADECCO | Fransico | 0123456784   | Allocated |
      | 123456786  | ASSIGNED          | ACTIVE     | RLN1          | is          | ADECCO | Fransico | 0123456786   | Allocated |
      | 123456787  | ASSIGNED          | ACTIVE     | RLN1-CA       | is          | ADECCO | Fransico | 0123456787   | Allocated |
      | 123456788  | ASSIGNED          | ACTIVE     | RLN1-CA-01    | is not      | ADECCO | Fransico | 0123456788   | Allocated |
