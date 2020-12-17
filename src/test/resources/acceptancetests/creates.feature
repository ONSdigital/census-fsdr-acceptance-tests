@Acceptance
Feature: Creates

  Scenario Outline: A record is created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And a contract start date of "2020-01-01"
    And the managers of "<role_id>" exist
    And we ingest managers
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee "<id>" is correctly created in gsuite with roleId "<role_id>"
    And the employee from "<source>" with roleId "<role_id>" is correctly created in XMA with group "<group>"
    And the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" as a create
    And the employee "<id>" is correctly created in ServiceNow with "<role_id>" with employment status "ASSIGNED"
    Then the employee "<id>" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with phone number "+447234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "<id>" with roleId "<role_id>" "phone" device allocation details are sent to xma with ID "+447234567890"
    And the employee "<id>" is sent to LWS as an create with name "Fransico" and phone number "+447234567890" and "<role_id>" with expected hierarchy items "<hier1>" "<hier2>" "<hier3>" "<hier4>" "<hier5>" "<hier6>" "<hier7>"

  Examples:
    | id        | role_id       | inLogisitcs | source | group                                | hier1           | hier2                   | hier3 | hier4     | hier5          | hier6         | hier7        |
    | 123456781 | HA-CAR1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Household               | A     | Carlisle  | Area Manager 1 |               |              |
    | 123456782 | HA-CAR1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
    | 123456783 | HA-CAR1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
    | 123456784 | SA-CAR1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
    | 123456785 | SA-CAR1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
    | 123456786 | CA-RUN1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 |               |              |
    | 123456787 | CA-RUN1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A |              |
    | 123456788 | CA-RUN1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A | 01 Tranche 1 |
    | 123456788 | XF-CEA1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | England & Wales | Community Engagement    | F     | Leicester | Area Manager 1 | Team Leader A | 01 Tranche 1 |

  Scenario Outline: A record is not created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "<assignment_status>"
    And a closing report status of "<cr_status>"
    And a role id of "<role_id>"
    And a contract start date of "<start_date>"
    And we ingest them
    When the employee "<id>" is not sent to all downstream services
    Then the employee "<id>" is not created in gsuite
    And the employee "<id>" is not created in ServiceNow
    And the employee  is not created in XMA
    And the employee "<id>" is not sent to LWS
    And the employee is not in the Logisitics CSV
    And the employee "<id>" is not sent to Adecco

  Examples:
    | id        | assignment_status    | cr_status | role_id       | start_date | source |
    | 123456781 | ASSIGNMENT ENDED     | ACTIVE    | SA-CAR1-ZA    | 2020-01-01 | ADECCO |
    | 123456782 | ASSIGNMENT CANCELLED | ACTIVE    | SA-CAR1-ZA-01 | 2020-01-01 | ADECCO |
    | 123456783 | ASSIGNED             | INACTIVE  | CA-RLN1       | 2020-01-01 | ADECCO |
    | 123456784 | READY TO START       | INACTIVE  | CA-RLN1       | 2020-01-01 | ADECCO |
    | 123456785 | ASSIGNMENT ENDED     | INACTIVE  | CA-RLN1-ZA    | 2020-01-01 | ADECCO |
    | 123456786 | ASSIGNMENT CANCELLED | INACTIVE  | CA-RLN1-ZA-01 | 2020-01-01 | ADECCO |
    | 123456787 | ASSIGNMENT ENDED     | PENDING   | CA-RLN1-ZA    | 2020-01-01 | ADECCO |
    | 123456788 | ASSIGNMENT CANCELLED | PENDING   | CA-RLN1-ZA-01 | 2020-01-01 | ADECCO |
    | 123456789 | ASSIGNED             | ACTIVE    | HA-CAR1       | 2021-01-01 | ADECCO |
    | 223456781 | READY TO START       | ACTIVE    | HA-CAR1       | 2021-01-01 | ADECCO |
    | 223456782 | ASSIGNMENT ENDED     | ACTIVE    | HA-CAR1-ZA    | 2021-01-01 | ADECCO |
    | 223456783 | ASSIGNMENT CANCELLED | ACTIVE    | HA-CAR1-ZA-01 | 2021-01-01 | ADECCO |
    | 223456784 | ASSIGNED             | INACTIVE  | CA-RLN1       | 2021-01-01 | ADECCO |
    | 223456785 | READY TO START       | INACTIVE  | CA-RLN1       | 2021-01-01 | ADECCO |
    | 223456786 | ASSIGNMENT ENDED     | INACTIVE  | CA-RLN1-ZA    | 2021-01-01 | ADECCO |
    | 223456787 | ASSIGNMENT CANCELLED | INACTIVE  | CA-RLN1-ZA-01 | 2021-01-01 | ADECCO |
    | 223456788 | ASSIGNED             | PENDING   | CA-RLN1       | 2021-01-01 | ADECCO |
    | 223456789 | READY TO START       | PENDING   | CA-RLN1       | 2021-01-01 | ADECCO |
    | 323456781 | ASSIGNMENT ENDED     | PENDING   | CA-RLN1-ZA    | 2021-01-01 | ADECCO |
    | 323456782 | ASSIGNMENT CANCELLED | PENDING   | CA-RLN1-ZA-01 | 2021-01-01 | ADECCO |

  Scenario: A record with a start date grater than 6 days in the future is not created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "123456789"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date 8 days in the future
    And we ingest them
    When the employee "123456789" is not sent to all downstream services
    Then the employee "123456789" is not created in gsuite
    And the employee "123456789" is not created in ServiceNow
    And the employee  is not created in XMA
    And the employee "123456789" is not sent to LWS
    And the employee is not in the Logisitics CSV
#    And Check the employee "123456789" is not sent to RCA
    And the employee "123456789" is not sent to Adecco

  Scenario: A record with a start date 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "223456789"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date 6 days in the future
    And we ingest them
    When the employee "223456789" is sent to all downstream services
    Then the employee "223456789" is correctly created in gsuite with roleId "HA-CAR1"
    And the employee "223456789" is correctly created in ServiceNow with "HA-CAR1" with employment status "ASSIGNED"
    And the employee from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
    And the employee "223456789" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "223456789" with phone number "+447234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "223456789" with roleId "HA-CAR1" "phone" device allocation details are sent to xma with ID "+447234567890"
    And the employee "223456789" is sent to LWS as an create with name "Fransico" and phone number "+447234567890" and "HA-CAR1" with expected hierarchy items "England & Wales" "Household" "A" "Carlisle" "Area Manager 1" "" ""

  Scenario: A record with a start date less than 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "323456789"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date 5 days in the future
    And we ingest them
    When the employee "323456789" is sent to all downstream services
    And the employee "323456789" is correctly created in gsuite with roleId "HA-CAR1"
    Then the employee "323456789" is sent to Adecco
    And the employee "323456789" is correctly created in ServiceNow with "HA-CAR1" with employment status "ASSIGNED"
    And the employee from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "323456789" with phone number "+447234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "323456789" with roleId "HA-CAR1" "phone" device allocation details are sent to xma with ID "+447234567890"
    And the employee "323456789" is sent to LWS as an create with name "Fransico" and phone number "+447234567890" and "HA-CAR1" with expected hierarchy items "England & Wales" "Household" "A" "Carlisle" "Area Manager 1" "" ""

  Scenario Outline: A HQ record is ingested and created
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    And we run HQ actions
    Then the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    Given the roleId for "00000001" is set to "<role_id>" in gsuite
    When we retrieve the roleIds from GSuite for "00000001"
    And we run HQ actions
    Then the user "00000001" is added to the following groups "<groups>"
    And the HQ employee "00000001" with roleId "<role_id>" is correctly created in XMA

    Examples:
    | role_id    | groups            | lws   |
    | xx-RMTx    | hq-all,rmt-all    | false |
    | PT-FPHx-xx | hq-all,pt-fph-all | false |
    | PT-FPTx-xx | hq-all,pt-fpt-all | false |

  Scenario: Device details are not sent to xma and lws when ready to start
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
    And the employee "123456781" is correctly created in ServiceNow with "HA-CAR1" with employment status "READY_TO_START"
    Then the employee "123456781" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "123456781" with phone number "+447234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "123456781" device details are not sent to xma
    And the employee "123456781" is not sent to LWS

  Scenario: Chromebook details are sent to XMA
    Given An employee exists in "ADECCO" with an id of "123456781"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date of "2020-01-01"
    And we ingest them
    When the employee "123456781" is sent to all downstream services
    Then the employee "123456781" is correctly created in gsuite with roleId "HA-CAR1"
    And the employee from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
    And the employee "123456781" is correctly created in ServiceNow with "HA-CAR1" with employment status "ASSIGNED"
    Then the employee "123456781" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a chromebook device for "123456781" with id "XMA123456"
    And we ingest them
    And the employee "123456781" with roleId "HA-CAR1" "chromebook" device allocation details are sent to xma with ID "XMA123456"
    And the employee "123456781" is not sent to LWS
