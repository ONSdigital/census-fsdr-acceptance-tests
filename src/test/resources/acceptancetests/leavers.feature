@Acceptance
Feature: Leavers

  Scenario Outline: A record in FSDR becomes a leaver
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And the managers of "<role_id>" exist
    And we ingest managers
    And we ingest them
    And the employee "<id>" is sent to all downstream services
      ### LWS Requires a device to be created ###
    And a device exists in XMA with "<role_id>", "<phone_number>" and "<status>"
    And we retrieve the devices from xma
    And we ingest them
      ###
    And we receive a job role update from adecco for employee  "<id>"
    And an assignment status of "<new_assignment_status>"
    And a closing report status of "<new_cr_status>"
    And a role id of "<role_id>"
    And an operational end date of "<op_end_date>"
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee is correctly suspended in gsuite
    And the employee "<id>" is correctly suspended in ServiceNow with "<role_id>"
    And the employee with roleId "<role_id>" is correctly suspended in XMA
    And the employee "<id>" is sent to LWS as an leaver with "<phone_number>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as a leaver
    And Check the employee "<id>" is not sent to RCA

    Examples:
      | id        | assignment_status | cr_status | role_id    | new_assignment_status | new_cr_status | inLogisitcs | source | op_end_date | phone_number | status    |
      | 123456780 | ASSIGNED          | ACTIVE    | CAR1       | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456781 | ASSIGNED          | ACTIVE    | CAR1-HA    | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456782 | ASSIGNED          | ACTIVE    | CAR1-HA-01 | ASSIGNMENT_CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456783 | ASSIGNED          | ACTIVE    | CAR1-SA    | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456784 | ASSIGNED          | ACTIVE    | CAR1-SA-01 | ASSIGNMENT_CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456785 | ASSIGNED          | ACTIVE    | RLN1       | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456786 | ASSIGNED          | ACTIVE    | RLN1-CA    | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456787 | ASSIGNED          | ACTIVE    | RLN1-CA-01 | ASSIGNMENT_CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456788 | ASSIGNED          | ACTIVE    | CAR1       | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456789 | ASSIGNED          | ACTIVE    | CAR1-HA    | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 123456780 | ASSIGNED          | ACTIVE    | CAR1-HA-01 | ASSIGNMENT_ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 223456781 | ASSIGNED          | ACTIVE    | CAR1-SA    | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 223456782 | ASSIGNED          | ACTIVE    | CAR1-SA-01 | ASSIGNMENT_ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 223456783 | ASSIGNED          | ACTIVE    | RLN1       | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 223456784 | ASSIGNED          | ACTIVE    | RLN1-CA    | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 223456785 | ASSIGNED          | ACTIVE    | RLN1-CA-01 | ASSIGNMENT_ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 0123456789   | Allocated |
      | 223456786 | ASSIGNED          | ACTIVE    | CAR1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 223456787 | ASSIGNED          | ACTIVE    | CAR1-HA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 223456788 | ASSIGNED          | ACTIVE    | CAR1-HA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 223456789 | ASSIGNED          | ACTIVE    | CAR1-SA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 223456780 | ASSIGNED          | ACTIVE    | CAR1-SA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 323456781 | ASSIGNED          | ACTIVE    | RLN1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 323456782 | ASSIGNED          | ACTIVE    | RLN1-CA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 0123456789   | Allocated |
      | 323456783 | ASSIGNED          | ACTIVE    | RLN1-CA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 0123456789   | Allocated |
