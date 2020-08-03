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
    Then the employee "<id>" is sent to Adecco
      ### LWS requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with phone number "<number>" and IMEI number "990000888888888"
    And we ingest them
      ###
    And we receive an update from adecco for employee "<id>" with new first name "<new_name>"
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee is correctly updated in gsuite with name "<new_name>"
    Then the employee "<id>" is sent to LWS as an update with name "<new_name>" and phone number "<number>" and "<role_id>"
    Then the employee "<id>" is correctly updated in ServiceNow with "<role_id>" and name "<new_name>" and number "<number>" and asset id "990000888888888"
    Then the employee from "<source>" with roleId "<role_id>" is correctly updated in XMA with name "<new_name>" and group "<group>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<number>" as an update with name "<new_name>"
    And Check the employee "<id>" is sent to RCA
    And the employee "<id>" with roleId "<role_id>" device allocation details are sent to xma with IMEI number "990000888888888"

    Examples:
      | id         | role_id          | inLogisitcs | source | new_name | group                                | number     |
      | 123456781  | HA-CAR1          | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 |
      | 123456782  | HA-CAR1-ZA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 |
      | 123456783  | HA-CAR1-ZA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | 07234567890 |
      | 123456784  | SA-CAR1-ZA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 |
      | 123456785  | SA-CAR1-ZA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | 07234567890 |
      | 123456786  | CA-RLN1          | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 |
      | 123456787  | CA-RLN1-ZA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 |
      | 123456788  | CA-RLN1-ZA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | 07234567890 |

  Scenario Outline: A record in FSDR receives a device
    Given the managers of "<role_id>" exist
    And we ingest managers
    And An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And we ingest them
    And the employee "<id>" is sent to all downstream services
    Then the employee "<id>" is sent to Adecco
    And we ingest a device from pubsub for "<id>" with phone number "<phone_number>" and IMEI number "990000888888888"
    And we run create actions
    When the employee "<id>" is sent to all downstream services
    Then the employee "<role_id>" is not updated in gsuite
    Then the employee "<id>" is correctly updated in ServiceNow with "<role_id>" and name "<name>" and number "<phone_number>" and asset id "990000888888888"
    Then the employee "<id>" is sent to LWS as an update with name "<name>" and phone number "<phone_number>" and "<role_id>"
    Then the employee "<role_id>" is not updated in XMA
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as an update with name "<name>"
    And Check the employee "<id>" is sent to RCA
    Then the employee "<id>" is sent to Adecco with phone number "<phone_number>"
    And the employee "<id>" with roleId "<role_id>" device allocation details are sent to xma with IMEI number "990000888888888"

    Examples:
      | id        | role_id       | inLogisitcs | source | name     | phone_number |
      | 123456781 | HA-CAR1       | is          | ADECCO | Fransico | 07234567810  |
      | 123456782 | HA-CAR1-ZA    | is          | ADECCO | Fransico | 07234567850  |
      | 123456783 | HA-CAR1-ZA-01 | is not      | ADECCO | Fransico | 07234567820  |
      | 123456784 | SA-CAR1-ZA    | is          | ADECCO | Fransico | 07234567830  |
      | 123456785 | SA-CAR1-ZA-01 | is not      | ADECCO | Fransico | 07234567840  |
      | 123456786 | CA-RLN1       | is          | ADECCO | Fransico | 07234567860  |
      | 123456787 | CA-RLN1-ZA    | is          | ADECCO | Fransico | 07234567870  |
      | 123456788 | CA-RLN1-ZA-01 | is not      | ADECCO | Fransico | 07234567880  |

    Scenario: A record in FSDR receives a replacement device
      Given the managers of "HA-CAR1-ZA-01" exist
      And we ingest managers
      And An employee exists in "ADECCO" with an id of "123456781"
      And an assignment status of "ASSIGNED"
      And a closing report status of "ACTIVE"
      And a role id of "HA-CAR1-ZA-01"
      And we ingest them
      And the employee "123456781" is sent to all downstream services
      Then the employee "123456781" is sent to Adecco
      And we ingest a device from pubsub for "123456781" with phone number "07234567810" and IMEI number "990000888888888"
      And we run create actions
      When the employee "123456781" is sent to all downstream services
      Then the employee "HA-CAR1-ZA-01" is not updated in gsuite
      Then the employee "123456781" is correctly updated in ServiceNow with "HA-CAR1-ZA-01" and name "Fransico" and number "07234567810" and asset id "990000888888888"
      Then the employee "123456781" is sent to LWS as an update with name "Fransico" and phone number "07234567810" and "HA-CAR1-ZA-01"
      Then the employee "HA-CAR1-ZA-01" is not updated in XMA
      Then the employee "is not" in the Logisitics CSV with "HA-CAR1-ZA-01" and phone number "07234567810" as an update with name "Fransico"
      And Check the employee "123456781" is sent to RCA
      Then the employee "123456781" is sent to Adecco with phone number "07234567810"
      And the employee "123456781" with roleId "HA-CAR1-ZA-01" device allocation details are sent to xma with IMEI number "990000888888888"
      When we ingest a device from pubsub for "123456781" with phone number "07234567811" and IMEI number "990000777777777"
      And we run create actions
      And the employee "123456781" will only have one phone
      When the employee "123456781" is sent to all downstream services
      Then the employee "HA-CAR1-ZA-01" is not updated in gsuite
      Then the employee "123456781" is correctly updated in ServiceNow with "HA-CAR1-ZA-01" and name "Fransico" and number "07234567811" and asset id "990000777777777"
      Then the employee "123456781" is sent to LWS as an update with name "Fransico" and phone number "07234567811" and "HA-CAR1-ZA-01"
      Then the employee "HA-CAR1-ZA-01" is not updated in XMA
      Then the employee "is not" in the Logisitics CSV with "HA-CAR1-ZA-01" and phone number "07234567811" as an update with name "Fransico"
      And Check the employee "123456781" is sent to RCA
      And the employee "123456781" with roleId "HA-CAR1-ZA-01" device allocation details are sent to xma with IMEI number "990000777777777"

  Scenario: An existing HQ record is ingested and updated
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    And the roleId for "00000001" is set to "AB-CDE1" in gsuite
    And we retrieve the roleIds from GSuite
    When A "HQ" ingest CSV "00000000_000003_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    Then the hq employee "00000001" is correctly updated in gsuite

  Scenario: An existing HQ record not ingested or updated
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    And the roleId for "00000001" is set to "AB-CDE1" in gsuite
    And we retrieve the roleIds from GSuite
    When A "HQ" ingest CSV "00000000_000004_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    Then the employee "AB-CDE1" is not updated in gsuite