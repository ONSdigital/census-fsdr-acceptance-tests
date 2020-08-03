@Acceptance
Feature: Leavers

  Scenario Outline: A record in FSDR becomes a leaver
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And the managers of "<role_id>" exist
    And we ingest managers
    And we ingest them
    And the employee "<id>" is sent to all downstream services
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with phone number "<phone_number>" and IMEI number "990000888888888"
    And we ingest them
    And the employee "<id>" is sent to all downstream services
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
      | id        | role_id       | new_assignment_status | new_cr_status | inLogisitcs | source | op_end_date | phone_number |
      | 123456780 | HA-CAR1       | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456781 | HA-CAR1-ZA    | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456782 | HA-CAR1-ZA-01 | ASSIGNMENT_CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 123456783 | SA-CAR1-ZA    | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456784 | SA-CAR1-ZA-01 | ASSIGNMENT_CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 123456785 | CA-RLN1       | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456786 | CA-RLN1-ZA    | ASSIGNMENT_CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456787 | CA-RLN1-ZA-01 | ASSIGNMENT_CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 123456788 | HA-CAR1       | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456789 | HA-CAR1-ZA    | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456780 | HA-CAR1-ZA-01 | ASSIGNMENT_ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 223456781 | SA-CAR1-ZA    | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 223456782 | SA-CAR1-ZA-01 | ASSIGNMENT_ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 223456783 | CA-RLN1       | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 223456784 | CA-RLN1-ZA    | ASSIGNMENT_ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 223456785 | CA-RLN1-ZA-01 | ASSIGNMENT_ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 223456786 | HA-CAR1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 223456787 | HA-CAR1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 223456788 | HA-CAR1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 07234567890  |
      | 223456789 | SA-CAR1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 223456780 | SA-CAR1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 07234567890  |
      | 323456781 | CA-RLN1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 323456782 | CA-RLN1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 323456783 | CA-RLN1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 07234567890  |
