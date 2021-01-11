@Acceptance
Feature: Updates

  Scenario Outline: A record in FSDR with a device receives an update
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a closing report id of "<cr_id>"
    And a role id of "<role_id>"
    And we ingest them
    Then the employee "<id>" with closing report id "<cr_id>" is sent to Adecco
      ### LWS requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with closing report id "<cr_id>" with phone number "<number>" and IMEI number "990000888888888"
    And we ingest them
      ###
    And we receive an update from adecco for employee "<id>" with new first name "<new_name>"
    And we ingest them
    Then the employee "<id>" with closing report id "<cr_id>" is correctly updated in gsuite with name "<new_name>" and roleId "<role_id>"
    Then the employee "<id>" with closing report id "<cr_id>" is correctly updated in ServiceNow with "<role_id>" and name "<new_name>" and number "<number>" and status "ASSIGNED"
    Then the employee "<id>" with closing report id "<cr_id>" from "<source>" with roleId "<role_id>" is correctly updated in XMA with name "<new_name>" and group "<group>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<number>" as an update with name "<new_name>"
    And the employee "<id>" with closing report id "<cr_id>" with roleId "<role_id>" "phone" device allocation details are sent to xma with ID "<number>"
    Then the employee "<id>" with closing report id "<cr_id>" is sent to LWS as an update with name "<new_name>" and phone number "<number>" and "<role_id>" with expected hierarchy items "<hier1>" "<hier2>" "<hier3>" "<hier4>" "<hier5>" "<hier6>" "<hier7>"

    Examples:
      | id        | cr_id  | role_id       | inLogisitcs | source | new_name | group                                | number        | hier1           | hier2                   | hier3 | hier4     | hier5          | hier6         | hier7        |
      | 300000001 | cr3001 | HB-CAR1       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | +447234567890 | England & Wales | Household               | B     | Carlisle  | Area Manager 1 |               |              |
      | 300000002 | cr3002 | HB-CAR1-ZA    | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | +447234567890 | England & Wales | Household               | B     | Carlisle  | Area Manager 1 | Team Leader A |              |
      | 300000003 | cr3003 | HB-CAR1-ZA-01 | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | +447234567890 | England & Wales | Household               | B     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
      | 300000004 | cr3004 | SA-CAR1-ZB    | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | +447234567890 | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader B |              |
      | 300000005 | cr3005 | SA-CAR1-ZB-01 | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | +447234567890 | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader B | 01 Tranche 1 |
      | 300000006 | cr3006 | CA-RNO1       | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | +447234567890 | England & Wales | Census Coverage Survey  | A     | North     | Area Manager 1 |               |              |
      | 300000007 | cr3007 | CA-RNO1-ZA    | is          | ADECCO | John     | 7DD2611D-F60D-4A17-B759-B021BC5C669A | +447234567890 | England & Wales | Census Coverage Survey  | A     | North     | Area Manager 1 | Team Leader A |              |
      | 300000008 | cr3008 | CA-RNO1-ZA-01 | is not      | ADECCO | John     | 8A2FEF60-9429-465F-B711-83753B234BDD | +447234567890 | England & Wales | Census Coverage Survey  | A     | North     | Area Manager 1 | Team Leader A | 01 Tranche 1 |

  Scenario Outline: A record in FSDR receives a device
    And An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a closing report id of "<cr_id>"
    And a role id of "<role_id>"
    And we ingest them
    Then the employee "<id>" with closing report id "<cr_id>" is sent to Adecco
    And we ingest a device from pubsub for "<id>" with closing report id "<cr_id>" with phone number "<phone_number>" and IMEI number "990000888888888"
    And we run create actions
    Then the employee "<id>" with closing report id "<cr_id>" is not updated in gsuite
    Then the employee "<id>" with closing report id "<cr_id>" is correctly updated in ServiceNow with "<role_id>" and name "<name>" and number "<phone_number>" and status "ASSIGNED"
    Then the employee "<role_id>" is not updated in XMA
    Then the employee "<id>" with closing report id "<cr_id>" is sent to Adecco with phone number "07234567890"
    And the employee "<id>" with closing report id "<cr_id>" with roleId "<role_id>" "phone" device allocation details are sent to xma with ID "<phone_number>"
    And the employee "<id>" with closing report id "<cr_id>" is sent to LWS as an create with name "Fransico" and phone number "<phone_number>" and "<role_id>" with expected hierarchy items "<hier1>" "<hier2>" "<hier3>" "<hier4>" "<hier5>" "<hier6>" "<hier7>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "<phone_number>" as an update with name "<name>"

    Examples:
      | id        | cr_id | role_id       | inLogisitcs | source | name     | phone_number  | hier1           | hier2                   | hier3 | hier4     | hier5          | hier6         | hier7        |
      | 300000009 | cr3009 | HB-CAR1       | is          | ADECCO | Fransico | +447234567890 | England & Wales | Household               | B     | Carlisle  | Area Manager 1 |               |              |
      | 300000010 | cr3010 | HB-CAR1-ZA    | is          | ADECCO | Fransico | +447234567890 | England & Wales | Household               | B     | Carlisle  | Area Manager 1 | Team Leader A |              |
      | 300000011 | cr3011 | HB-CAR1-ZA-01 | is not      | ADECCO | Fransico | +447234567890 | England & Wales | Household               | B     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
      | 300000012 | cr3012 | SA-CAR1-ZB    | is          | ADECCO | Fransico | +447234567890 | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader B |              |
      | 300000013 | cr3013 | SA-CAR1-ZB-01 | is not      | ADECCO | Fransico | +447234567890 | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader B | 01 Tranche 1 |
      | 300000014 | cr3014 | CA-RNO1       | is          | ADECCO | Fransico | +447234567890 | England & Wales | Census Coverage Survey  | A     | North     | Area Manager 1 |               |              |
      | 300000015 | cr3015 | CA-RNO1-ZA    | is          | ADECCO | Fransico | +447234567890 | England & Wales | Census Coverage Survey  | A     | North     | Area Manager 1 | Team Leader A |              |
      | 300000016 | cr3016 | CA-RNO1-ZA-01 | is not      | ADECCO | Fransico | +447234567890 | England & Wales | Census Coverage Survey  | A     | North     | Area Manager 1 | Team Leader A | 01 Tranche 1 |

  Scenario: A record in FSDR receives a replacement phone device
    Given An employee exists in "ADECCO" with an id of "300000017"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a closing report id of "cr3017"
    And a role id of "HB-CAR1-ZA-01"
    And we ingest them
    Then the employee "300000017" with closing report id "cr3017" is sent to Adecco
    And we ingest a device from pubsub for "300000017" with closing report id "cr3017" with phone number "+447234567890" and IMEI number "990000888888888"
    And we run create actions
    Then the employee "300000017" with closing report id "cr3017" is not updated in gsuite
    Then the employee "300000017" with closing report id "cr3017" is correctly updated in ServiceNow with "HB-CAR1-ZA-01" and name "Fransico" and number "+447234567890" and status "ASSIGNED"
    And the employee "300000017" with closing report id "cr3017" is sent to LWS as an create with name "Fransico" and phone number "+447234567890" and "HB-CAR1-ZA-01" with expected hierarchy items "England & Wales" "Household" "B" "Carlisle" "Area Manager 1" "Team Leader A" "01 Tranche 1"
    Then the employee "HB-CAR1-ZA-01" is not updated in XMA
    Then the employee "is not" in the Logisitics CSV with "HB-CAR1-ZA-01" and phone number "+447234567890" as an update with name "Fransico"
    Then the employee "300000017" with closing report id "cr3017" is sent to Adecco with phone number "07234567890"
    And the employee "300000017" with closing report id "cr3017" with roleId "HB-CAR1-ZA-01" "phone" device allocation details are sent to xma with ID "+447234567890"
    When we ingest a device from pubsub for "300000017" with closing report id "cr3017" with phone number "+447234567891" and IMEI number "990000777777777"
    And we run create actions
    And the employee "300000017" with closing report id "cr3017" will only have one phone
    Then the employee "300000017" with closing report id "cr3017" is not updated in gsuite
    Then the employee "300000017" with closing report id "cr3017" is correctly updated in ServiceNow with "HB-CAR1-ZA-01" and name "Fransico" and number "+447234567891" and status "ASSIGNED"
    Then the employee "HB-CAR1-ZA-01" is not updated in XMA
    Then the employee "is not" in the Logisitics CSV with "HB-CAR1-ZA-01" and phone number "+447234567891" as an update with name "Fransico"
    Then the employee "300000017" with closing report id "cr3017" is sent to LWS as an update with name "Fransico" and phone number "+447234567891" and "HB-CAR1-ZA-01" with expected hierarchy items "England & Wales" "Household" "B" "Carlisle" "Area Manager 1" "Team Leader A" "01 Tranche 1"
    Then the employee "300000017" with closing report id "cr3017" is sent to Adecco with phone number "07234567891"
    And the employee "300000017" with closing report id "cr3017" with roleId "HB-CAR1-ZA-01" "phone" device allocation details are sent to xma with ID "+447234567891"

  Scenario: A record in FSDR receives a replacement chromebook device
    And An employee exists in "ADECCO" with an id of "300000018"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a closing report id of "cr3018"
    And a role id of "HB-CAR1-ZA-01"
    And we ingest them
    Then the employee "300000018" with closing report id "cr3018" is sent to Adecco
    And we ingest a chromebook device for "300000018" with closing report id "cr3018" with id "XMA123456"
    And we run create actions
    Then the employee "300000018" with closing report id "cr3018" is not updated in gsuite
    Then the employee "300000018" with closing report id "cr3018" is not updated in ServiceNow
    Then the employee "300000018" with closing report id "cr3018" is not sent to LWS
    Then the employee "HB-CAR1-ZA-01" is not updated in XMA
    And the employee "is not" in the Logisitics CSV with "HB-CAR1-ZA-01" as a create
    And the employee "300000018" with closing report id "cr3018" with roleId "HB-CAR1-ZA-01" "chromebook" device allocation details are sent to xma with ID "XMA123456"
    And we ingest a chromebook device for "300000018" with closing report id "cr3018" with id "XMA123457"
    And we run create actions
    Then the employee "300000018" with closing report id "cr3018" is not updated in gsuite
    Then the employee "300000018" with closing report id "cr3018" is not updated in ServiceNow
    Then the employee "300000018" with closing report id "cr3018" is not sent to LWS
    Then the employee "HB-CAR1-ZA-01" is not updated in XMA
    And the employee "300000018" with closing report id "cr3018" will only have one phone
    Then the employee "is not" in the Logisitics CSV with "HB-CAR1-ZA-01" and phone number "+447234567890" as an update with name "Fransico"
    And the employee "300000018" with closing report id "cr3018" with roleId "HB-CAR1-ZA-01" "chromebook" device allocation details are sent to xma with ID "XMA123457"

  Scenario: Device details are sent to xma and lws when the employee assignment status updates to Assigned
    Given An employee exists in "ADECCO" with an id of "300000019"
    And an assignment status of "READY TO START"
    And a closing report status of "ACTIVE"
    And a role id of "HB-CAR1"
    And a contract start date of "2020-01-01"
    And a closing report id of "cr3019"
    And we ingest them
    Then the employee "300000019" with closing report id "cr3019" is correctly created in gsuite with roleId "HB-CAR1"
    And the employee "300000019" with closing report id "cr3019" from "ADECCO" with roleId "HB-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HB-CAR1" as a create
    And the employee "300000019" with closing report id "cr3019" is correctly created in ServiceNow with "HB-CAR1" and status "READY_TO_START"
    Then the employee "300000019" with closing report id "cr3019" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "300000019" with closing report id "cr3019" with phone number "+447234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "300000019" with closing report id "cr3019" device details are not sent to xma
    And the employee "300000019" with closing report id "cr3019" is not sent to LWS
    And the employee assignment status changes to "ASSIGNED"
    And we ingest them
    And the employee "300000019" with closing report id "cr3019" with roleId "HB-CAR1" "phone" device allocation details are sent to xma with ID "+447234567890"
    And the employee "300000019" with closing report id "cr3019" is sent to LWS as an create with name "Fransico" and phone number "+447234567890" and "HB-CAR1" with expected hierarchy items "England & Wales" "Household" "B" "Carlisle" "Area Manager 1" "" ""

  #TODO Is this still a valid scenario?
#  Scenario: A record in FSDR with recieves an update with multiple closing reports for same role ID
#    Given An employee exists in "ADECCO" with an id of "300000020"
#    And an assignment status of "ASSIGNED"
#    And a closing report status of "ACTIVE"
#    And a closing report id of "cr3020"
#    And a role id of "HB-CAR1"
#    And a contract start date of "2020-01-01"
#    And we ingest them
#    When the employee "300000020" is sent to all downstream services
#    And we receive an update from adecco for employee "300000020" with multiple closing reports for role id "HB-CAR1" updating name to "John"
#    And we ingest them
#    When the employee "300000020" is sent to all downstream services
#    Then the employee "300000020" with closing report id "cr3020" is correctly updated in gsuite with name "John" and roleId "HB-CAR1"
#    Then the employee "300000020" with closing report id "cr3020" is correctly updated in ServiceNow with "HB-CAR1" and name "John" and number "" and contract start date "2020-02-01"
#    Then the employee from "ADECCO" with roleId "HB-CAR1" is correctly updated in XMA with name "John" and group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
#    Then the employee "is" in the Logisitics CSV with "HB-CAR1" and phone number "" as an update with name "John"

  Scenario: An existing HQ record is ingested and updated
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    And the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    Given the roleId for "00000001" is set to "xx-RMTx" in gsuite
    When we retrieve the roleIds from GSuite for "00000001"
    And we run HQ actions
    And the HQ employee "00000001" with roleId "xx-RMTx" is correctly created in XMA
    When A "HQ" ingest CSV "00000000_000003_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    Then the hq employee "00000001" is correctly updated in gsuite
    And the HQ employee "00000001" with roleId "xx-RMTx" is correctly updated in XMA

  Scenario: An existing HQ record not ingested or updated
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    And the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    When A "HQ" ingest CSV "00000000_000004_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    Then the employee "00000001" with closing report id "" is not updated in gsuite
