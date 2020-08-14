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
    Then the employee "<id>" is correctly suspended in gsuite
    And the employee "<id>" is correctly suspended in ServiceNow with "<role_id>"
    And the employee with roleId "<role_id>" is correctly suspended in XMA
    And the employee "<id>" is sent to LWS as an leaver with "<phone_number>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as a leaver
    And Check the employee "<id>" is not sent to RCA

    Examples:
      | id        | role_id       | new_assignment_status | new_cr_status | inLogisitcs | source | op_end_date | phone_number |
      | 123456780 | HA-CAR1       | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456781 | HA-CAR1-ZA    | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456782 | HA-CAR1-ZA-01 | ASSIGNMENT CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 123456783 | SA-CAR1-ZA    | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456784 | SA-CAR1-ZA-01 | ASSIGNMENT CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 123456785 | CA-RUN1       | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456786 | CA-RUN1-ZA    | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456787 | CA-RUN1-ZA-01 | ASSIGNMENT CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 123456788 | HA-CAR1       | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456789 | HA-CAR1-ZA    | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 123456780 | HA-CAR1-ZA-01 | ASSIGNMENT ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 223456781 | SA-CAR1-ZA    | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 223456782 | SA-CAR1-ZA-01 | ASSIGNMENT ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 223456783 | CA-RUN1       | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 223456784 | CA-RUN1-ZA    | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 223456785 | CA-RUN1-ZA-01 | ASSIGNMENT ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 223456786 | HA-CAR1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 223456787 | HA-CAR1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 223456788 | HA-CAR1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 07234567890  |
      | 223456789 | SA-CAR1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 223456780 | SA-CAR1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 07234567890  |
      | 323456781 | CA-RUN1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 323456782 | CA-RUN1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 323456783 | CA-RUN1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 07234567890  |

  Scenario: A HQ record is left
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    And we run HQ actions
    Then the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    Given A "HQ" ingest CSV "00000000_000002_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    And we run HQ actions
    Then the employee "00000001" is correctly suspended in gsuite