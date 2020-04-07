@Acceptance
Feature: Creates

  Scenario Outline: A record is created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And a contract start date of "<start_date>"
    And the managers of "<role_id>" exist
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee is correctly created in gsuite with roleId "<role_id>" and orgUnit "<org_unit>"
    And the employee "<id>" is now in the current groups "<new_groups>"
    And the employee from "<source>" with roleId "<role_id>" is correctly created in XMA with group "<group>"
    And the employee is not in the LWS CSV as a create
    And the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" as a create
    And Check the employee "<id>" is sent to RCA

    Examples:
      | id         | assignment_status | cr_status  | role_id       | inLogisitcs | source | start_date | group                                | org_unit    | new_groups                                                   |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1          | is          | ADECCO | 2020-01-01 | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Managers    | car1-group,ons_users,Household-group                         |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA       | is          | ADECCO | 2020-01-01 | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Managers    | car1-group,car1-ha-group,ons_users,ons_drive,Household-group |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA-01    | is not      | ADECCO | 2020-01-01 | 8A2FEF60-9429-465F-B711-83753B234BDD | EW-Officers | car1-ha-group,ons_users,ons_drive,Household-group            |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA       | is          | ADECCO | 2020-01-01 | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Managers    | car1-group,car1-sa-group,ons_users,ons_drive,CE-group        |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA-01    | is not      | ADECCO | 2020-01-01 | 8A2FEF60-9429-465F-B711-83753B234BDD | EW-Officers | car1-sa-group,ons_users,ons_drive,CE-group                   |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1          | is          | ADECCO | 2020-01-01 | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Managers    | rln1-group,ons_users,CCS-group                               |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA       | is          | ADECCO | 2020-01-01 | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Managers    | rln1-group,rln1-ca-group,ons_users,ccs_drive,CCS-group       |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA-01    | is not      | ADECCO | 2020-01-01 | 8A2FEF60-9429-465F-B711-83753B234BDD | EW-Officers | rln1-ca-group,ons_users,ccs_drive,CCS-group                  |

  Scenario Outline: A record is not created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And a contract start date of "<start_date>"
    And we ingest them
    When the employee "<id>" is not sent to all downstream services
    Then the employee "<id>" is not created in gsuite
    And the employee is not created in ServiceNow
    And the employee  is not created in XMA
    And the employee is not in the LWS CSV as a create
    And the employee is not in the Logisitics CSV
    And Check the employee "<id>" is not sent to RCA

    Examples:
      | id         | assignment_status    | cr_status    | role_id       | start_date | source |
      | 123456781  | ASSIGNMENT_ENDED     | ACTIVE       | CAR1-SA       | 2020-01-01 | ADECCO |
      | 123456782  | ASSIGNMENT_CANCELLED | ACTIVE       | CAR1-SA-01    | 2020-01-01 | ADECCO |
      | 123456783  | ASSIGNED             | INACTIVE     | RLN1          | 2020-01-01 | ADECCO |
      | 123456784  | READY_TO_START       | INACTIVE     | RLN1          | 2020-01-01 | ADECCO |
      | 123456785  | ASSIGNMENT_ENDED     | INACTIVE     | RLN1-CA       | 2020-01-01 | ADECCO |
      | 123456786  | ASSIGNMENT_CANCELLED | INACTIVE     | RLN1-CA-01    | 2020-01-01 | ADECCO |
      | 123456787  | ASSIGNMENT_ENDED     | PENDING      | RLN1-CA       | 2020-01-01 | ADECCO |
      | 123456788  | ASSIGNMENT_CANCELLED | PENDING      | RLN1-CA-01    | 2020-01-01 | ADECCO |
      | 123456789  | ASSIGNED             | ACTIVE       | CAR1          | 2021-01-01 | ADECCO |
      | 123456789  | READY_TO_START       | ACTIVE       | CAR1          | 2021-01-01 | ADECCO |
      | 123456789  | ASSIGNMENT_ENDED     | ACTIVE       | CAR1-HA       | 2021-01-01 | ADECCO |
      | 123456789  | ASSIGNMENT_CANCELLED | ACTIVE       | CAR1-HA-01    | 2021-01-01 | ADECCO |
      | 123456789  | ASSIGNED             | INACTIVE     | RLN1          | 2021-01-01 | ADECCO |
      | 123456789  | READY_TO_START       | INACTIVE     | RLN1          | 2021-01-01 | ADECCO |
      | 123456789  | ASSIGNMENT_ENDED     | INACTIVE     | RLN1-CA       | 2021-01-01 | ADECCO |
      | 123456789  | ASSIGNMENT_CANCELLED | INACTIVE     | RLN1-CA-01    | 2021-01-01 | ADECCO |
      | 123456789  | ASSIGNED             | PENDING      | RLN1          | 2021-01-01 | ADECCO |
      | 123456789  | READY_TO_START       | PENDING      | RLN1          | 2021-01-01 | ADECCO |
      | 123456789  | ASSIGNMENT_ENDED     | PENDING      | RLN1-CA       | 2021-01-01 | ADECCO |
      | 123456789  | ASSIGNMENT_CANCELLED | PENDING      | RLN1-CA-01    | 2021-01-01 | ADECCO |

  Scenario: A record with a start date grater than 6 days in the future is not created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "123456789"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "CAR1"
    And a contract start date 8 days in the future
    And we ingest them
    When the employee "<id>" is not sent to all downstream services
    Then the employee "123456789" is not created in gsuite
    And the employee is not created in ServiceNow
    And the employee  is not created in XMA
    And the employee is not in the LWS CSV as a create
    And the employee is not in the Logisitics CSV
    And Check the employee "123456781" is not sent to RCA

  Scenario: A record with a start date 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "123456789"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "CAR1"
    And a contract start date 6 days in the future
    And we ingest them
    When the employee "123456789" is sent to all downstream services
    Then the employee is correctly created in gsuite with roleId "CAR1" and orgUnit "Managers"
    And the employee is correctly created in ServiceNow with "CAR1"
    And the employee from "ADECCO" with roleId "CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee is not in the LWS CSV as a create
    And the employee "is" in the Logisitics CSV with "CAR1" as a create
    And Check the employee "123456789" is sent to RCA

  Scenario: A record with a start date less than 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "123456789"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "CAR1"
    And a contract start date 5 days in the future
    And we ingest them
    When the employee "123456789" is sent to all downstream services
    Then the employee is correctly created in gsuite with roleId "CAR1" and orgUnit "Managers"
    And the employee is correctly created in ServiceNow with "CAR1"
    And the employee from "ADECCO" with roleId "CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee is not in the LWS CSV as a create
    And the employee "is" in the Logisitics CSV with "CAR1" as a create
    And Check the employee "123456789" is sent to RCA