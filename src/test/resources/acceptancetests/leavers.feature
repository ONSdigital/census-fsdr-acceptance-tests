@Acceptance
Feature: Leavers

  Scenario Outline: A record in FSDR becomes a leaver
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
     And a closing report id of "<cr_id>"
    And we ingest them
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with closing report id "<cr_id>" with phone number "<phone_number>" and IMEI number "990000888888888"
    And we ingest them
      ###
    And we receive a job role update from adecco for employee  "<id>"
    And an assignment status of "<new_assignment_status>"
    And a closing report status of "<new_cr_status>"
    And a role id of "<role_id>"
    And an operational end date of "<op_end_date>"
    And we ingest them
    Then the employee "<id>" with closing report id "<cr_id>" is correctly suspended in gsuite
    And the employee "<id>" with closing report id "<cr_id>" is correctly suspended in ServiceNow with "<role_id>"
    And the employee "<id>" with closing report id "<cr_id>" with roleId "<role_id>" is correctly suspended in XMA
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as a leaver
    And the employee "<id>" with closing report id "<cr_id>" is sent to LWS as an leaver with "<phone_number>"

    Examples:
      | id        | cr_id  | role_id       | new_assignment_status | new_cr_status | inLogisitcs | source | op_end_date | phone_number  |
      | 200000000 | cr2000 | HB-CAR1       | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000001 | cr2001 | HB-CAR1-ZA    | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000002 | cr2002 | HB-CAR1-ZA-01 | ASSIGNMENT CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | +447234567890 |
      | 200000003 | cr2003 | SA-CAR1-ZB    | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000004 | cr2004 | SA-CAR1-ZB-01 | ASSIGNMENT CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | +447234567890 |
      | 200000005 | cr2005 | CA-RNO1       | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000006 | cr2006 | CA-RNO1-ZA    | ASSIGNMENT CANCELLED  | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000007 | cr2007 | CA-RNO1-ZA-01 | ASSIGNMENT CANCELLED  | INACTIVE      | is not      | ADECCO | 2050-01-01  | +447234567890 |
      | 200000008 | cr2008 | HB-CAR1       | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000009 | cr2009 | HB-CAR1-ZA    | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000010 | cr2010 | HB-CAR1-ZA-01 | ASSIGNMENT ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | +447234567890 |
      | 200000011 | cr2011 | SA-CAR1-ZB    | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000012 | cr2012 | SA-CAR1-ZB-01 | ASSIGNMENT ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | +447234567890 |
      | 200000013 | cr2013 | CA-RNO1       | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000014 | cr2014 | CA-RNO1-ZA    | ASSIGNMENT ENDED      | INACTIVE      | is          | ADECCO | 2050-01-01  | +447234567890 |
      | 200000015 | cr2015 | CA-RNO1-ZA-01 | ASSIGNMENT ENDED      | INACTIVE      | is not      | ADECCO | 2050-01-01  | +447234567890 |
      | 200000016 | cr2016 | HB-CAR1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | +447234567890 |
      | 200000017 | cr2017 | HB-CAR1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | +447234567890 |
      | 200000018 | cr2018 | HB-CAR1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | +447234567890 |
      | 200000019 | cr2019 | SA-CAR1-ZB    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | +447234567890 |
      | 200000020 | cr2020 | SA-CAR1-ZB-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | +447234567890 |
      | 200000021 | cr2021 | CA-RNO1       | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | +447234567890 |
      | 200000022 | cr2022 | CA-RNO1-ZA    | ASSIGNED              | ACTIVE        | is          | ADECCO | 2019-01-01  | +447234567890 |
      | 200000023 | cr2023 | CA-RNO1-ZA-01 | ASSIGNED              | ACTIVE        | is not      | ADECCO | 2019-01-01  | +447234567890 |

  Scenario: A HQ record is left
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    And we run HQ actions
    Then the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    Given the roleId for "00000001" is set to "xx-RMTx" in gsuite
    When we retrieve the roleIds from GSuite for "00000001"
    And we run HQ actions
    And the HQ employee "00000001" with roleId "xx-RMTx" is correctly created in XMA
    Given A "HQ" ingest CSV "00000000_000002_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    And we run HQ actions
    And the employee "00000001" with closing report id "" with roleId "xx-RMTx" is correctly suspended in XMA
    Then the employee "00000001" with closing report id "" is correctly suspended in gsuite
