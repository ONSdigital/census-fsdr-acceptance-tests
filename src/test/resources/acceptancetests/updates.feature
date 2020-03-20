@Acceptance
Feature: Updates

  Scenario Outline: A record in FSDR receives an update
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And we ingest them
    And the employee "<id>" is sent to all downstream services
    And we receive an update from adecco for employee "<id>" with new first name "<new_name>"
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee is correctly updated in gsuite with name "<new_name>"
    Then the employee is correctly updated in ServiceNow with "<role_id>" and name "<new_name>" and number ""
    Then the employee from "<source>" with roleId "<role_id>" is correctly updated in XMA with name "<new_name>" and group "<group>"
    Then the employee "<in_lws_no_device>" in the LWS CSV as an update with name "<new_name>" and phone number "<phone_number>" and "<role_id>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "" as an update with name "<new_name>"
    And Check the employee "<id>" is sent to RCA

    Examples:
      | id         | assignment_status | cr_status  | role_id       | inLogisitcs | in_lws_no_device | source | new_name | group                                |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1          | is          | is not           | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA       | is          | is not           | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA-01    | is not      | is not           | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA       | is          | is not           | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA-01    | is not      | is not           | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1          | is          | is not           | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA       | is          | is not           | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA-01    | is not      | is not           | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD |

  Scenario Outline: A record in FSDR receives a device
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And we ingest them
    And the employee "<id>" is sent to all downstream services
    And a device exists in XMA with "<role_id>", "<phone_number>" and "<status>"
    And we retrieve the devices from xma
    And we run create actions
    When the employee "<id>" is sent to all downstream services
    Then the employee "<id>" is not updated in gsuite
    Then the employee is correctly updated in ServiceNow with "<role_id>" and name "<name>" and number "<phone_number>"
    Then the employee is not updated in XMA
    Then the employee "<in_lws>" in the LWS CSV as an update with name "<name>" and phone number "<phone_number>" and "<role_id>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as an update with name "<name>"
    And Check the employee "<id>" is sent to RCA

    Examples:
      | id         | assignment_status | cr_status  | role_id       | inLogisitcs | in_lws | source | name     | phone_number | status    | group                                |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1          | is          | is     | ADECCO | Fransico | 0123456789   | Allocated | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA-01    | is not      | is     | ADECCO | Fransico | 0123456789   | Allocated | 8A2FEF60-9429-465F-B711-83753B234BDD |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA       | is          | is     | ADECCO | Fransico | 0123456789   | Allocated | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA-01    | is not      | is     | ADECCO | Fransico | 0123456789   | Allocated | 8A2FEF60-9429-465F-B711-83753B234BDD |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA       | is          | is     | ADECCO | Fransico | 0123456789   | Allocated | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1          | is          | is     | ADECCO | Fransico | 0123456789   | Allocated | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA       | is          | is     | ADECCO | Fransico | 0123456789   | Allocated | 7DD2611D-F60D-4A17-B759-B021BC5C669A |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA-01    | is not      | is     | ADECCO | Fransico | 0123456789   | Allocated | 8A2FEF60-9429-465F-B711-83753B234BDD |