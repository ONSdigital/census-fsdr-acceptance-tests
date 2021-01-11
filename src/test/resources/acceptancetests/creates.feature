@Acceptance
Feature: Creates

  Scenario Outline: A record is created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And a closing report id of "<cr_id>"
    And a contract start date of "2020-01-01"
    And we ingest them
    Then the employee "<id>" with closing report id "<cr_id>" is correctly created in gsuite with roleId "<role_id>"
    And the employee "<id>" with closing report id "<cr_id>" is correctly created in ServiceNow with "<role_id>"
    And the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" as a create
    Then the employee "<id>" with closing report id "<cr_id>" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with closing report id "<cr_id>" with phone number "+447234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "<id>" with closing report id "<cr_id>" with roleId "<role_id>" "phone" device allocation details are sent to xma with ID "+447234567890"
    And the employee "<id>" with closing report id "<cr_id>" is sent to LWS as an create with name "Fransico" and phone number "+447234567890" and "<role_id>" with expected hierarchy items "<hier1>" "<hier2>" "<hier3>" "<hier4>" "<hier5>" "<hier6>" "<hier7>"

    Examples:
      | id        | cr_id  | role_id       | inLogisitcs | source | group                                | hier1           | hier2                   | hier3 | hier4     | hier5          | hier6         | hier7        |
      | 100000001 | cr1001 | HB-CAR1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Household               | B     | Carlisle  | Area Manager 1 |               |              |
      | 100000002 | cr1002 | HB-CAR1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Household               | B     | Carlisle  | Area Manager 1 | Team Leader A |              |
      | 100000003 | cr1003 | HB-CAR1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | England & Wales | Household               | B     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
      | 100000004 | cr1004 | SA-CAR1-ZB    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader B |              |
      | 100000005 | cr1005 | SA-CAR1-ZB-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader B | 01 Tranche 1 |
      | 100000006 | cr1006 | CA-RNO1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Census Coverage Survey  | A     | North | Area Manager 1 |               |              |
      | 100000007 | cr1007 | CA-RNO1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Census Coverage Survey  | A     | North | Area Manager 1 | Team Leader A |              |
      | 100000008 | cr1008 | CA-RNO1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | England & Wales | Census Coverage Survey  | A     | North | Area Manager 1 | Team Leader A | 01 Tranche 1 |
      | 100000009 | cr1009 | XF-CEA1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | England & Wales | Community Engagement    | F     | Leicester | Area Manager 1 | Team Leader A | 01 Tranche 1 |

  Scenario Outline: A record is not created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a closing report id of "<cr_id>"
    And a role id of "<role_id>"
    And a contract start date of "<start_date>"
    And we ingest them
    Then the employee "<id>" with closing report id "<cr_id>" is not created in gsuite
    And the employee "<id>" with closing report id "<cr_id>" is not created in ServiceNow
    And the employee  is not created in XMA
    And the employee "<id>" with closing report id "<cr_id>" is not sent to LWS
    And the employee is not in the Logisitics CSV
    And the employee "<id>" with closing report id "<cr_id>" is not sent to Adecco

    Examples:
      | id        | cr_id  |  assignment_status    | cr_status | role_id       | start_date | source |
      | 100000010 | cr1010 |  ASSIGNMENT ENDED     | ACTIVE    | SA-CAR1-ZB    | 2020-01-01 | ADECCO |
      | 100000012 | cr1011 |  ASSIGNMENT CANCELLED | ACTIVE    | SA-CAR1-ZB-01 | 2020-01-01 | ADECCO |
      | 100000013 | cr1012 |  ASSIGNED             | INACTIVE  | CA-RNO1       | 2020-01-01 | ADECCO |
      | 100000014 | cr1013 |  READY TO START       | INACTIVE  | CA-RNO1       | 2020-01-01 | ADECCO |
      | 100000015 | cr1014 |  ASSIGNMENT ENDED     | INACTIVE  | CA-RNO1-ZA    | 2020-01-01 | ADECCO |
      | 100000016 | cr1015 |  ASSIGNMENT CANCELLED | INACTIVE  | CA-RNO1-ZA-01 | 2020-01-01 | ADECCO |
      | 100000017 | cr1016 |  ASSIGNMENT ENDED     | PENDING   | CA-RNO1-ZA    | 2020-01-01 | ADECCO |
      | 100000018 | cr1017 |  ASSIGNMENT CANCELLED | PENDING   | CA-RNO1-ZA-01 | 2020-01-01 | ADECCO |
      | 100000019 | cr1018 |  ASSIGNED             | ACTIVE    | HB-CAR1       | 2022-01-01 | ADECCO |
      | 100000020 | cr1019 |  READY TO START       | ACTIVE    | HB-CAR1       | 2022-01-01 | ADECCO |
      | 100000021 | cr1020 |  ASSIGNMENT ENDED     | ACTIVE    | HB-CAR1-ZA    | 2022-01-01 | ADECCO |
      | 100000022 | cr1021 |  ASSIGNMENT CANCELLED | ACTIVE    | HB-CAR1-ZA-01 | 2022-01-01 | ADECCO |
      | 100000023 | cr1022 |  ASSIGNED             | INACTIVE  | CA-RNO1       | 2022-01-01 | ADECCO |
      | 100000024 | cr1023 |  READY TO START       | INACTIVE  | CA-RNO1       | 2022-01-01 | ADECCO |
      | 100000025 | cr1024 |  ASSIGNMENT ENDED     | INACTIVE  | CA-RNO1-ZA    | 2022-01-01 | ADECCO |
      | 100000026 | cr1025 |  ASSIGNMENT CANCELLED | INACTIVE  | CA-RNO1-ZA-01 | 2022-01-01 | ADECCO |
      | 100000027 | cr1026 |  ASSIGNED             | PENDING   | CA-RNO1       | 2022-01-01 | ADECCO |
      | 100000028 | cr1027 |  READY TO START       | PENDING   | CA-RNO1       | 2022-01-01 | ADECCO |
      | 100000029 | cr1028 |  ASSIGNMENT ENDED     | PENDING   | CA-RNO1-ZA    | 2022-01-01 | ADECCO |
      | 100000030 | cr1029 |  ASSIGNMENT CANCELLED | PENDING   | CA-RNO1-ZA-01 | 2022-01-01 | ADECCO |

  Scenario: A record with a start date grater than 6 days in the future is not created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "100000031"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a closing report id of "cr1031"
    And a role id of "HB-CAR1"
    And a contract start date 8 days in the future
    And we ingest them
    Then the employee "100000031" with closing report id "cr1031" is not created in gsuite
    And the employee "100000031" with closing report id "cr1031" is not created in ServiceNow
    And the employee  is not created in XMA
    And the employee "100000031" with closing report id "cr1031" is not sent to LWS
    And the employee is not in the Logisitics CSV
    And the employee "100000031" with closing report id "cr1031" is not sent to Adecco

  Scenario: A record with a start date 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "100000032"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a closing report id of "cr1032"
    And a role id of "HB-CAR1"
    And a contract start date 6 days in the future
    And we ingest them
    Then the employee "100000032" with closing report id "cr1032" is correctly created in gsuite with roleId "HB-CAR1"
    And the employee "100000032" with closing report id "cr1032" is correctly created in ServiceNow with "HB-CAR1"
    And the employee "100000032" with closing report id "cr1032" from "ADECCO" with roleId "HB-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HB-CAR1" as a create
    And the employee "100000032" with closing report id "cr1032" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "100000032" with closing report id "cr1032" with phone number "+447234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "100000032" with closing report id "cr1032" with roleId "HB-CAR1" "phone" device allocation details are sent to xma with ID "+447234567890"
    And the employee "100000032" with closing report id "cr1032" is sent to LWS as an create with name "Fransico" and phone number "+447234567890" and "HB-CAR1" with expected hierarchy items "England & Wales" "Household" "B" "Carlisle" "Area Manager 1" "" ""

  Scenario: A record with a start date less than 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "100000033"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a closing report id of "cr1033"
    And a role id of "HB-CAR1"
    And a contract start date 5 days in the future
    And we ingest them
    And the employee "100000033" with closing report id "cr1033" is correctly created in gsuite with roleId "HB-CAR1"
    Then the employee "100000033" with closing report id "cr1033" is sent to Adecco
    And the employee "100000033" with closing report id "cr1033" is correctly created in ServiceNow with "HB-CAR1"
    And the employee "100000033" with closing report id "cr1033" from "ADECCO" with roleId "HB-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HB-CAR1" as a create
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "100000033" with closing report id "cr1033" with phone number "+447234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "100000033" with closing report id "cr1033" with roleId "HB-CAR1" "phone" device allocation details are sent to xma with ID "+447234567890"
    And the employee "100000033" with closing report id "cr1033" is sent to LWS as an create with name "Fransico" and phone number "+447234567890" and "HB-CAR1" with expected hierarchy items "England & Wales" "Household" "B" "Carlisle" "Area Manager 1" "" ""

  Scenario: Device details are not sent to xma and lws when ready to start
    Given An employee exists in "ADECCO" with an id of "100000034"
    And an assignment status of "READY TO START"
    And a closing report status of "ACTIVE"
    And a closing report id of "cr1034"
    And a role id of "HB-CAR1"
    And a contract start date of "2020-01-01"
    And we ingest them
    Then the employee "100000034" with closing report id "cr1034" is correctly created in gsuite with roleId "HB-CAR1"
    And the employee "100000034" with closing report id "cr1034" from "ADECCO" with roleId "HB-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HB-CAR1" as a create
    And the employee "100000034" with closing report id "cr1034" is correctly created in ServiceNow with "HB-CAR1"
    Then the employee "100000034" with closing report id "cr1034" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "100000034" with closing report id "cr1034" with phone number "+447234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "100000034" with closing report id "cr1034" device details are not sent to xma
    And the employee "100000034" with closing report id "cr1034" is not sent to LWS

  Scenario: Chromebook details are sent to XMA
    Given An employee exists in "ADECCO" with an id of "100000035"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a closing report id of "cr1035"
    And a role id of "HB-CAR1"
    And a contract start date of "2020-01-01"
    And we ingest them
    Then the employee "100000035" with closing report id "cr1035" is correctly created in gsuite with roleId "HB-CAR1"
    And the employee "100000035" with closing report id "cr1035" from "ADECCO" with roleId "HB-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HB-CAR1" as a create
    And the employee "100000035" with closing report id "cr1035" is correctly created in ServiceNow with "HB-CAR1"
    Then the employee "100000035" with closing report id "cr1035" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a chromebook device for "100000035" with closing report id "cr1035" with id "XMA123456"
    And we ingest them
    And the employee "100000035" with closing report id "cr1035" with roleId "HB-CAR1" "chromebook" device allocation details are sent to xma with ID "XMA123456"
    And the employee "100000035" with closing report id "cr1035" is not sent to LWS

  Scenario Outline: A HQ record is ingested and created
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    And we run HQ actions
    Then the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    Given the roleId for "00000001" is set to "<role_id>" in gsuite
    When we retrieve the roleIds from GSuite for "00000001"
    And we run HQ actions
    Then the user "00000001" with closing report id "" is added to the following groups "<groups>"
    And the HQ employee "00000001" with roleId "<role_id>" is correctly created in XMA

    Examples:
      | role_id    | groups            | lws   |
      | xx-RMTx    | hq-all,rmt-all    | false |
      | PT-FPHx-xx | hq-all,pt-fph-all | false |
      | PT-FPTx-xx | hq-all,pt-fpt-all | false |

