@Acceptance
Feature: Leavers

  Scenario Outline: A record in FSDR becomes a leaver
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    Given the managers of "<role_id>" exist and have been sent downstream
    And we ingest them
    And the employee "<id>" is created in XMA
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with phone number "<phone_number>" and IMEI number "990000888888888"
    And we ingest them
    And the employee "<id>" is created in XMA
      ###
    And we receive a job role update from adecco for employee  "<id>"
    And an assignment status of "<new_assignment_status>"
    And a closing report status of "<new_cr_status>"
    And a role id of "<role_id>"
    And an operational end date of "<op_end_date>"
    And we ingest them
    Then the employee "<id>" is correctly suspended in gsuite
    And the employee "<id>" is correctly suspended in ServiceNow with "<role_id>"
    And the employee "<id>" is sent to LWS as an leaver with "<phone_number>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as a leaver
    And Check the employee "<id>" is not sent to RCA
    And the employee "<id>" with roleId "<role_id>" is correctly suspended in XMA

    Examples:
      | id        | role_id       | new_assignment_status | new_cr_status | inLogisitcs | source | op_end_date | phone_number |
      | 700000001 | HA-CAR1       | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000002 | HA-CAR1-ZA    | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000003 | HA-CAR1-ZA-01 | ASSIGNMENT CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 700000004 | SA-CAR1-ZA    | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000005 | SA-CAR1-ZA-01 | ASSIGNMENT CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 700000006 | CA-RUN1       | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000007 | CA-RUN1-ZA    | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000008 | CA-RUN1-ZA-01 | ASSIGNMENT CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 700000009 | HA-CAR1       | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000010 | HA-CAR1-ZA    | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000011 | HA-CAR1-ZA-01 | ASSIGNMENT ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 700000012 | SA-CAR1-ZA    | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000013 | SA-CAR1-ZA-01 | ASSIGNMENT ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 700000014 | CA-RUN1       | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000015 | CA-RUN1-ZA    | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | 07234567890  |
      | 700000016 | CA-RUN1-ZA-01 | ASSIGNMENT ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | 07234567890  |
      | 700000017 | HA-CAR1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 700000018 | HA-CAR1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 700000019 | HA-CAR1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 07234567890  |
      | 700000020 | SA-CAR1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 700000021 | SA-CAR1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 07234567890  |
      | 700000022 | CA-RUN1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 700000023 | CA-RUN1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | 07234567890  |
      | 700000024 | CA-RUN1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | 07234567890  |