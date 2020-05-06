@Acceptance
Feature: Updates

  Scenario Outline: A record in FSDR with a device receives an update
    Given the managers of "<role_id>" exist
    And we ingest managers
    And An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And we ingest them
    And the employee "<id>" is sent to all downstream services
      ### LWS requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with phone number "0123456789"
    And we ingest them
      ###
    And we receive an update from adecco for employee "<id>" with new first name "<new_name>"
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee is correctly updated in gsuite with name "<new_name>"
    Then the employee "<id>" is sent to LWS as an update with name "<new_name>" and phone number "<number>" and "<role_id>"
    Then the employee "<id>" is correctly updated in ServiceNow with "<role_id>" and name "<new_name>" and number "<number>"
    Then the employee from "<source>" with roleId "<role_id>" is correctly updated in XMA with name "<new_name>" and group "<group>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<number>" as an update with name "<new_name>"
    And Check the employee "<id>" is sent to RCA

    Examples:
      | id         | role_id          | inLogisitcs | source | new_name | group                                | number     |
      | 123456781  | HA-CAR1          | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 0723456789 |
      | 123456782  | HA-CAR1-ZA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 0723456789 |
      | 123456783  | HA-CAR1-ZA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | 0723456789 |
      | 123456784  | SA-CAR1-ZA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 0723456789 |
      | 123456785  | SA-CAR1-ZA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | 0723456789 |
      | 123456786  | CA-RLN1          | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 0723456789 |
      | 123456787  | CA-RLN1-ZA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 0723456789 |
      | 123456788  | CA-RLN1-ZA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | 0723456789 |

  Scenario Outline: A record in FSDR receives a device
    Given the managers of "<role_id>" exist
    And we ingest managers
    And An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And we ingest them
    And the employee "<id>" is sent to all downstream services
    And we ingest a device from pubsub for "<id>" with phone number "<phone_number>"
    And we run create actions
    When the employee "<id>" is sent to all downstream services
    Then the employee "<role_id>" is not updated in gsuite
    Then the employee "<id>" is correctly updated in ServiceNow with "<role_id>" and name "<name>" and number "<phone_number>"
    Then the employee "<id>" is sent to LWS as an update with name "<name>" and phone number "<phone_number>" and "<role_id>"
    Then the employee "<role_id>" is not updated in XMA
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as an update with name "<name>"
    And Check the employee "<id>" is sent to RCA

    Examples:
      | id         | role_id          | inLogisitcs | source | name     | phone_number |
      | 123456781  | HA-CAR1          | is          | ADECCO | Fransico | 0723456781   |
      | 123456782  | HA-CAR1-ZA       | is          | ADECCO | Fransico | 0723456785   |
      | 123456783  | HA-CAR1-ZA-01    | is not      | ADECCO | Fransico | 0723456782   |
      | 123456784  | SA-CAR1-ZA       | is          | ADECCO | Fransico | 0723456783   |
      | 123456785  | SA-CAR1-ZA-01    | is not      | ADECCO | Fransico | 0723456784   |
      | 123456786  | CA-RLN1          | is          | ADECCO | Fransico | 0723456786   |
      | 123456787  | CA-RLN1-ZA       | is          | ADECCO | Fransico | 0723456787   |
      | 123456788  | CA-RLN1-ZA-01    | is not      | ADECCO | Fransico | 0723456788   |
