@Acceptance
Feature: Leavers

  Scenario Outline: A record in FSDR becomes a leaver
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And we ingest them
    And the employee "<id>" is sent to all downstream services
    And a device exists in XMA with "<role_id>", "<phone_number>" and "<status>"
    And we retrieve the devices from xma
    And the employee is sent to LWS
    And we receive a job role update from adecco for employee  "<id>"
    And an assignment status of "<new_assignment_status>"
    And a closing report status of "<new_cr_status>"
    And a role id of "<role_id>"
    And an operational end date of "<op_end_date>"
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee is correctly suspended in gsuite
    Then the employee is correctly suspended in ServiceNow with "<role_id>"
    Then the employee with roleId "<role_id>" is correctly suspended in XMA
    Then the employee "<inLws>" in the LWS CSV as a leaver
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as a leaver
    And Check the employee "<id>" is not sent to RCA

    Examples:
      | id         | assignment_status | cr_status  | role_id       | new_assignment_status | new_cr_status  | inLogisitcs | inLws | source | op_end_date | phone_number | status    |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1          | ASSIGNMENT_CANCELLED  | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA-01    | ASSIGNMENT_CANCELLED  | INACTIVE       | is not      | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA       | ASSIGNMENT_CANCELLED  | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA-01    | ASSIGNMENT_CANCELLED  | INACTIVE       | is not      | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA       | ASSIGNMENT_CANCELLED  | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1          | ASSIGNMENT_CANCELLED  | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA       | ASSIGNMENT_CANCELLED  | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA-01    | ASSIGNMENT_CANCELLED  | INACTIVE       | is not      | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1          | ASSIGNMENT_ENDED      | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA       | ASSIGNMENT_ENDED      | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA-01    | ASSIGNMENT_ENDED      | INACTIVE       | is not      | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA       | ASSIGNMENT_ENDED      | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA-01    | ASSIGNMENT_ENDED      | INACTIVE       | is not      | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1          | ASSIGNMENT_ENDED      | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA       | ASSIGNMENT_ENDED      | INACTIVE       | is          | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA-01    | ASSIGNMENT_ENDED      | INACTIVE       | is not      | is    | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1          | ASSIGNED              | ACTIVE         | is          | is    | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA       | ASSIGNED              | ACTIVE         | is          | is    | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-HA-01    | ASSIGNED              | ACTIVE         | is not      | is    | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA       | ASSIGNED              | ACTIVE         | is          | is    | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | CAR1-SA-01    | ASSIGNED              | ACTIVE         | is not      | is    | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1          | ASSIGNED              | ACTIVE         | is          | is    | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA       | ASSIGNED              | ACTIVE         | is          | is    | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 123456789  | ASSIGNED          | ACTIVE     | RLN1-CA-01    | ASSIGNED              | ACTIVE         | is not      | is    | ADECCO | 2019-01-01  | 0123456789   | Allocated |
