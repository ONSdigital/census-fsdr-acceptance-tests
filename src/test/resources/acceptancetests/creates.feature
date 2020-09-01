@Acceptance
Feature: Creates

  Scenario Outline: A record is created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And a contract start date of "2020-01-01"
    And the managers of "<role_id>" exist and have been sent downstream
    And we ingest them
    Then the employee "<id>" is correctly created in gsuite with roleId "<role_id>" and orgUnit "<org_unit>"
    And the employee "<id>" is now in the current groups "<new_groups>"
    And the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" as a create
    And the employee "<id>" is correctly created in ServiceNow with "<role_id>"
    And Check the employee "<id>" is sent to RCA
    Then the employee "<id>" is sent to Adecco
    And the employee "<id>" from "<source>" with roleId "<role_id>" is correctly created in XMA with group "<group>"
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with phone number "07234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "<id>" is sent to LWS as an create with name "Fransico" and phone number "07234567890" and "<role_id>" with expected hierarchy items "<hier1>" "<hier2>" "<hier3>" "<hier4>" "<hier5>" "<hier6>" "<hier7>"
    And the employee "<id>" with roleId "<role_id>" "phone" device allocation details are sent to xma with ID "07234567890"

  Examples:
    | id        | role_id       | inLogisitcs | source | group                                | org_unit    | new_groups | hier1           | hier2                   | hier3 | hier4     | hier5          | hier6         | hier7        |
    | 400000001 | HA-CAR1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Zero Access | ons_users  | England & Wales | Household               | A     | Carlisle  | Area Manager 1 |               |              |
    | 400000002 | HA-CAR1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Zero Access | ons_users  | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
    | 400000003 | HA-CAR1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | Zero Access | ons_users  | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
    | 400000004 | SA-CAR1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Zero Access | ons_users  | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
    | 400000005 | SA-CAR1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | Zero Access | ons_users  | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
    | 400000006 | CA-RUN1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Zero Access | ons_users  | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 |               |              |
    | 400000007 | CA-RUN1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | Zero Access | ons_users  | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A |              |
    | 400000008 | CA-RUN1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | Zero Access | ons_users  | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A | 01 Tranche 1 |

  Scenario Outline: A record is not created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And a contract start date of "<start_date>"
    And we ingest them
    Then the employee "<id>" is not created in gsuite
    And the employee "<id>" is not created in ServiceNow
    And the employee  is not created in XMA
    And the employee "<id>" is not sent to LWS
    And the employee is not in the Logisitics CSV
    And Check the employee "<id>" is not sent to RCA
    And the employee "<id>" is not sent to Adecco

  Examples:
    | id        | assignment_status    | cr_status | role_id       | start_date | source |
    | 500000001 | ASSIGNMENT ENDED     | ACTIVE    | SA-CAR1-ZA    | 2020-01-01 | ADECCO |
    | 500000002 | ASSIGNMENT CANCELLED | ACTIVE    | SA-CAR1-ZA-01 | 2020-01-01 | ADECCO |
    | 500000003 | ASSIGNED             | INACTIVE  | CA-RLN1       | 2020-01-01 | ADECCO |
    | 500000004 | READY TO START       | INACTIVE  | CA-RLN1       | 2020-01-01 | ADECCO |
    | 500000005 | ASSIGNMENT ENDED     | INACTIVE  | CA-RLN1-ZA    | 2020-01-01 | ADECCO |
    | 500000006 | ASSIGNMENT CANCELLED | INACTIVE  | CA-RLN1-ZA-01 | 2020-01-01 | ADECCO |
    | 500000007 | ASSIGNMENT ENDED     | PENDING   | CA-RLN1-ZA    | 2020-01-01 | ADECCO |
    | 500000008 | ASSIGNMENT CANCELLED | PENDING   | CA-RLN1-ZA-01 | 2020-01-01 | ADECCO |
    | 500000009 | ASSIGNED             | ACTIVE    | HA-CAR1       | 2021-01-01 | ADECCO |
    | 500000010 | READY TO START       | ACTIVE    | HA-CAR1       | 2021-01-01 | ADECCO |
    | 500000011 | ASSIGNMENT ENDED     | ACTIVE    | HA-CAR1-ZA    | 2021-01-01 | ADECCO |
    | 500000012 | ASSIGNMENT CANCELLED | ACTIVE    | HA-CAR1-ZA-01 | 2021-01-01 | ADECCO |
    | 500000013 | ASSIGNED             | INACTIVE  | CA-RLN1       | 2021-01-01 | ADECCO |
    | 500000014 | READY TO START       | INACTIVE  | CA-RLN1       | 2021-01-01 | ADECCO |
    | 500000015 | ASSIGNMENT ENDED     | INACTIVE  | CA-RLN1-ZA    | 2021-01-01 | ADECCO |
    | 500000016 | ASSIGNMENT CANCELLED | INACTIVE  | CA-RLN1-ZA-01 | 2021-01-01 | ADECCO |
    | 500000017 | ASSIGNED             | PENDING   | CA-RLN1       | 2021-01-01 | ADECCO |
    | 500000018 | READY TO START       | PENDING   | CA-RLN1       | 2021-01-01 | ADECCO |
    | 500000019 | ASSIGNMENT ENDED     | PENDING   | CA-RLN1-ZA    | 2021-01-01 | ADECCO |
    | 500000020 | ASSIGNMENT CANCELLED | PENDING   | CA-RLN1-ZA-01 | 2021-01-01 | ADECCO |

  Scenario: A record with a start date grater than 6 days in the future is not created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "600000001"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date 8 days in the future
    And we ingest them
    Then the employee "600000001" is not created in gsuite
    And the employee "600000001" is not created in ServiceNow
    And the employee  is not created in XMA
    And the employee "600000001" is not sent to LWS
    And the employee is not in the Logisitics CSV
    And Check the employee "600000001" is not sent to RCA
    And the employee "600000001" is not sent to Adecco

  Scenario: A record with a start date 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "600000002"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date 6 days in the future
    And we ingest them
    Then the employee "600000002" is correctly created in gsuite with roleId "HA-CAR1" and orgUnit "ONS Managers"
    And the employee "600000002" is correctly created in ServiceNow with "HA-CAR1"
    And the employee "600000002" from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
    And Check the employee "600000002" is sent to RCA
    And the employee "600000002" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "600000002" with phone number "07234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "600000002" is sent to LWS as an create with name "Fransico" and phone number "07234567890" and "HA-CAR1" with expected hierarchy items "England & Wales" "Household" "A" "Carlisle" "Area Manager 1" "" ""
    And the employee "600000002" with roleId "HA-CAR1" "phone" device allocation details are sent to xma with ID "07234567890"

  Scenario: A record with a start date less than 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "600000003"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date 5 days in the future
    And we ingest them
    And the employee "600000003" is correctly created in gsuite with roleId "HA-CAR1" and orgUnit "ONS Managers"
    Then the employee "600000003" is sent to Adecco
    And the employee "600000003" is correctly created in ServiceNow with "HA-CAR1"
    And the employee "600000003" from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
    And Check the employee "600000003" is sent to RCA
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "600000003" with phone number "07234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "600000003" is sent to LWS as an create with name "Fransico" and phone number "07234567890" and "HA-CAR1" with expected hierarchy items "England & Wales" "Household" "A" "Carlisle" "Area Manager 1" "" ""
    And the employee "600000003" with roleId "HA-CAR1" "phone" device allocation details are sent to xma with ID "07234567890"

  Scenario: Device details are not sent to xma and lws when ready to start
    Given An employee exists in "ADECCO" with an id of "600000004"
    And an assignment status of "READY TO START"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date of "2020-01-01"
    And we ingest them
    Then the employee "600000004" is correctly created in gsuite with roleId "HA-CAR1" and orgUnit "ONS MANAGERS"
    And the employee "600000004" is now in the current groups "ons_users"
    And the employee "600000004" from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
    And the employee "600000004" is correctly created in ServiceNow with "HA-CAR1"
    And Check the employee "600000004" is sent to RCA
    Then the employee "600000004" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "600000004" with phone number "07234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "600000004" device details are not sent to xma
    And the employee "600000004" is not sent to LWS

  Scenario: Chromebook details are sent to XMA
    Given An employee exists in "ADECCO" with an id of "600000005"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date of "2020-01-01"
    And we ingest them
    Then the employee "600000005" is correctly created in gsuite with roleId "HA-CAR1" and orgUnit "ONS MANAGERS"
    And the employee "600000005" is now in the current groups "ons_users"
    And the employee "600000005" from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
    And the employee "600000005" is correctly created in ServiceNow with "HA-CAR1"
    And Check the employee "600000005" is sent to RCA
    Then the employee "600000005" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a chromebook device for "600000005" with id "XMA123456"
    And we ingest them
    And the employee "600000005" with roleId "HA-CAR1" "chromebook" device allocation details are sent to xma with ID "XMA123456"
    And the employee "600000005" is not sent to LWS
