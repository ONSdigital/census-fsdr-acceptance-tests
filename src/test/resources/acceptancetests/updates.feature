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
    Then the employee "<id>" is sent to LWS as an update with name "<new_name>" and phone number "<number>" and "<role_id>" with expected hierarchy items "<hier1>" "<hier2>" "<hier3>" "<hier4>" "<hier5>" "<hier6>" "<hier7>"
    Then the employee "<id>" is correctly updated in ServiceNow with "<role_id>" and name "<new_name>" and number "<number>"
    Then the employee from "<source>" with roleId "<role_id>" is correctly updated in XMA with name "<new_name>" and group "<group>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<number>" as an update with name "<new_name>"
    And Check the employee "<id>" is sent to RCA
    And the employee "<id>" with roleId "<role_id>" "phone" device allocation details are sent to xma with ID "<number>"

  Examples:
    | id         | role_id          | inLogisitcs | source | new_name | group                                | number      | hier1           | hier2                   | hier3 | hier4     | hier5          | hier6         | hier7        |
    | 123456781  | HA-CAR1          | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 | England & Wales | Household               | A     | Carlisle  | Area Manager 1 |               |              |
    | 123456782  | HA-CAR1-ZA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
    | 123456783  | HA-CAR1-ZA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | 07234567890 | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
    | 123456784  | SA-CAR1-ZA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
    | 123456785  | SA-CAR1-ZA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | 07234567890 | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
    | 123456786  | CA-RUN1          | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 |               |              |
    | 123456787  | CA-RUN1-ZA       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | 07234567890 | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A |              |
    | 123456788  | CA-RUN1-ZA-01    | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | 07234567890 | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A | 01 Tranche 1 |

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
    Then the employee "<id>" is correctly updated in ServiceNow with "<role_id>" and name "<name>" and number "<phone_number>"
    Then the employee "<id>" is sent to LWS as an update with name "<name>" and phone number "<phone_number>" and "<role_id>" with expected hierarchy items "<hier1>" "<hier2>" "<hier3>" "<hier4>" "<hier5>" "<hier6>" "<hier7>"
    Then the employee "<role_id>" is not updated in XMA
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as an update with name "<name>"
    And Check the employee "<id>" is sent to RCA
    Then the employee "<id>" is sent to Adecco with phone number "<phone_number>"
    And the employee "<id>" with roleId "<role_id>" "phone" device allocation details are sent to xma with ID "<phone_number>"

  Examples:
    | id        | role_id       | inLogisitcs | source | name     | phone_number | hier1           | hier2                   | hier3 | hier4     | hier5          | hier6         | hier7        |
    | 123456781 | HA-CAR1       | is          | ADECCO | Fransico | 07234567810  | England & Wales | Household               | A     | Carlisle  | Area Manager 1 |               |              |
    | 123456782 | HA-CAR1-ZA    | is          | ADECCO | Fransico | 07234567850  | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
    | 123456783 | HA-CAR1-ZA-01 | is not      | ADECCO | Fransico | 07234567820  | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
    | 123456784 | SA-CAR1-ZA    | is          | ADECCO | Fransico | 07234567830  | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
    | 123456785 | SA-CAR1-ZA-01 | is not      | ADECCO | Fransico | 07234567840  | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
    | 123456786 | CA-RUN1       | is          | ADECCO | Fransico | 07234567860  | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 |               |              |
    | 123456787 | CA-RUN1-ZA    | is          | ADECCO | Fransico | 07234567870  | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A |              |
    | 123456788 | CA-RUN1-ZA-01 | is not      | ADECCO | Fransico | 07234567880  | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A | 01 Tranche 1 |

  Scenario: A record in FSDR receives a replacement phone device
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
    Then the employee "123456781" is correctly updated in ServiceNow with "HA-CAR1-ZA-01" and name "Fransico" and number "07234567810"
    Then the employee "123456781" is sent to LWS as an update with name "Fransico" and phone number "07234567810" and "HA-CAR1-ZA-01" with expected hierarchy items "England & Wales" "Household" "A" "Carlisle" "Area Manager 1" "Team Leader A" "01 Tranche 1"
    Then the employee "HA-CAR1-ZA-01" is not updated in XMA
    Then the employee "is not" in the Logisitics CSV with "HA-CAR1-ZA-01" and phone number "07234567810" as an update with name "Fransico"
    And Check the employee "123456781" is sent to RCA
    Then the employee "123456781" is sent to Adecco with phone number "07234567810"
    And the employee "123456781" with roleId "HA-CAR1-ZA-01" "phone" device allocation details are sent to xma with ID "07234567810"
    When we ingest a device from pubsub for "123456781" with phone number "07234567811" and IMEI number "990000777777777"
    And we run create actions
    And the employee "123456781" will only have one phone
    When the employee "123456781" is sent to all downstream services
    Then the employee "HA-CAR1-ZA-01" is not updated in gsuite
    Then the employee "123456781" is correctly updated in ServiceNow with "HA-CAR1-ZA-01" and name "Fransico" and number "07234567811"
    Then the employee "123456781" is sent to LWS as an update with name "Fransico" and phone number "07234567811" and "HA-CAR1-ZA-01" with expected hierarchy items "England & Wales" "Household" "A" "Carlisle" "Area Manager 1" "Team Leader A" "01 Tranche 1"
    Then the employee "HA-CAR1-ZA-01" is not updated in XMA
    Then the employee "is not" in the Logisitics CSV with "HA-CAR1-ZA-01" and phone number "07234567811" as an update with name "Fransico"
    And Check the employee "123456781" is sent to RCA
    And the employee "123456781" with roleId "HA-CAR1-ZA-01" "phone" device allocation details are sent to xma with ID "07234567811"

  Scenario: A record in FSDR receives a replacement chromebook device
    Given the managers of "HA-CAR1-ZA-01" exist
    And we ingest managers
    And An employee exists in "ADECCO" with an id of "123456781"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1-ZA-01"
    And we ingest them
    And the employee "123456781" is sent to all downstream services
    Then the employee "123456781" is sent to Adecco
    And we ingest a chromebook device for "123456781" with id "XMA123456"
    And we run create actions
    When the employee "123456781" is sent to all downstream services
    Then the employee "HA-CAR1-ZA-01" is not updated in gsuite
    Then the employee "123456781" is correctly updated in ServiceNow with "HA-CAR1-ZA-01" and name "Fransico" and number "07234567810"
    Then the employee "123456781" is not sent to LWS
    Then the employee "HA-CAR1-ZA-01" is not updated in XMA
    And the employee "is not" in the Logisitics CSV with "HA-CAR1-ZA-01" as a create
    And Check the employee "123456781" is sent to RCA
    And the employee "123456781" with roleId "HA-CAR1-ZA-01" "chromebook" device allocation details are sent to xma with ID "XMA123456"
    And we ingest a chromebook device for "123456781" with id "XMA123457"
    And we run create actions
    And the employee "123456781" will only have one phone
    When the employee "123456781" is sent to all downstream services
    Then the employee "HA-CAR1-ZA-01" is not updated in gsuite
    Then the employee "123456781" is correctly updated in ServiceNow with "HA-CAR1-ZA-01" and name "Fransico" and number "07234567811"
    Then the employee "123456781" is not sent to LWS
    Then the employee "HA-CAR1-ZA-01" is not updated in XMA
    Then the employee "is not" in the Logisitics CSV with "HA-CAR1-ZA-01" and phone number "07234567811" as an update with name "Fransico"
    And Check the employee "123456781" is sent to RCA
    And the employee "123456781" with roleId "HA-CAR1-ZA-01" "chromebook" device allocation details are sent to xma with ID "XMA123457"


  Scenario: An existing HQ record is ingested and updated
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    And the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    When A "HQ" ingest CSV "00000000_000003_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    Then the hq employee "00000001" is correctly updated in gsuite

  Scenario: An existing HQ record not ingested or updated
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    And the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    When A "HQ" ingest CSV "00000000_000004_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    Then the employee "AB-CDE1" is not updated in gsuite

  Scenario: Device details are sent to xma and lws when the employee assignment status updates to Assigned
    Given An employee exists in "ADECCO" with an id of "123456781"
    And an assignment status of "READY TO START"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date of "2020-01-01"
    And we ingest them
    When the employee "123456781" is sent to all downstream services
    Then the employee "123456781" is correctly created in gsuite with roleId "HA-CAR1"
    And the employee from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
    And the employee "123456781" is correctly created in ServiceNow with "HA-CAR1"
    And Check the employee "123456781" is sent to RCA
    Then the employee "123456781" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "123456781" with phone number "07234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "123456781" device details are not sent to xma
    And the employee "123456781" is not sent to LWS
    And the employee assignment status changes to "ASSIGNED"
    And we ingest them
    And the employee "123456781" is sent to LWS as an create with name "Fransico" and phone number "07234567890" and "HA-CAR1" with expected hierarchy items "England & Wales" "Household" "A" "Carlisle" "Area Manager 1" "" ""
    And the employee "123456781" with roleId "HA-CAR1" "phone" device allocation details are sent to xma with ID "07234567890"
