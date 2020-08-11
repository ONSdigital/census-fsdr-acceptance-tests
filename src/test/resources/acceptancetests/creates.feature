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
    Then the employee "<id>" is correctly created in gsuite with roleId "<role_id>" and orgUnit "<org_unit>"
    And the employee "<id>" is now in the current groups "<new_groups>"
    And the employee from "<source>" with roleId "<role_id>" is correctly created in XMA with group "<group>"
    And the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" as a create
    And the employee "<id>" is correctly created in ServiceNow with "<role_id>"
    And Check the employee "<id>" is sent to RCA
    Then the employee "<id>" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "<id>" with phone number "07234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "<id>" is sent to LWS as an create with name "Fransico" and phone number "07234567890" and "<role_id>" with expected hierarchy items "<hier1>" "<hier2>" "<hier3>" "<hier4>" "<hier5>" "<hier6>" "<hier7>"
    And the employee "<id>" with roleId "<role_id>" device allocation details are sent to xma with IMEI number "990000888888888"

    Examples:
      | id        | role_id       | inLogisitcs | source | group                                | org_unit     | new_groups                                                         | hier1           | hier2                   | hier3 | hier4     | hier5          | hier6         | hier7        |
      | 123456781 | HA-CAR1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | ONS Managers | ha-car1-group,ons_users,household-group                            | England & Wales | Household               | A     | Carlisle  | Area Manager 1 |               |              |
      | 123456782 | HA-CAR1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | ONS Managers | ha-car1-group,ha-car1-za-group,ons_users,ons_drive,household-group | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
      | 123456783 | HA-CAR1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | ONS Officers | ha-car1-za-group,ons_users,ons_drive,household-group               | England & Wales | Household               | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
      | 123456784 | SA-CAR1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | ONS Managers | sa-car1-group,sa-car1-za-group,ons_users,ons_drive,CE-group        | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A |              |
      | 123456785 | SA-CAR1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | ONS Officers | sa-car1-za-group,ons_users,ons_drive,CE-group                      | England & Wales | Communal Establishments | A     | Carlisle  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
      | 123456786 | CA-RUN1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | ONS Managers | ca-run1-group,ons_users,CCS-group                                  | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 |               |              |
      | 123456787 | CA-RUN1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | ONS Managers | ca-run1-group,ca-run1-za-group,ons_users,ccs_drive,CCS-group       | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A |              |
      | 123456788 | CA-RUN1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | ONS Officers | ca-run1-za-group,ons_users,ccs_drive,CCS-group                     | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A | 01 Tranche 1 |

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
    And Check the employee "<id>" is not sent to RCA
    And the employee "<id>" is not sent to Adecco

    Examples:
      | id        | assignment_status    | cr_status | role_id       | start_date | source |
      | 123456781 | ASSIGNMENT_ENDED     | ACTIVE    | SA-CAR1-ZA    | 2020-01-01 | ADECCO |
      | 123456782 | ASSIGNMENT_CANCELLED | ACTIVE    | SA-CAR1-ZA-01 | 2020-01-01 | ADECCO |
      | 123456783 | ASSIGNED             | INACTIVE  | CA-RLN1       | 2020-01-01 | ADECCO |
      | 123456784 | READY_TO_START       | INACTIVE  | CA-RLN1       | 2020-01-01 | ADECCO |
      | 123456785 | ASSIGNMENT_ENDED     | INACTIVE  | CA-RLN1-ZA    | 2020-01-01 | ADECCO |
      | 123456786 | ASSIGNMENT_CANCELLED | INACTIVE  | CA-RLN1-ZA-01 | 2020-01-01 | ADECCO |
      | 123456787 | ASSIGNMENT_ENDED     | PENDING   | CA-RLN1-ZA    | 2020-01-01 | ADECCO |
      | 123456788 | ASSIGNMENT_CANCELLED | PENDING   | CA-RLN1-ZA-01 | 2020-01-01 | ADECCO |
      | 123456789 | ASSIGNED             | ACTIVE    | HA-CAR1       | 2021-01-01 | ADECCO |
      | 223456781 | READY_TO_START       | ACTIVE    | HA-CAR1       | 2021-01-01 | ADECCO |
      | 223456782 | ASSIGNMENT_ENDED     | ACTIVE    | HA-CAR1-ZA    | 2021-01-01 | ADECCO |
      | 223456783 | ASSIGNMENT_CANCELLED | ACTIVE    | HA-CAR1-ZA-01 | 2021-01-01 | ADECCO |
      | 223456784 | ASSIGNED             | INACTIVE  | CA-RLN1       | 2021-01-01 | ADECCO |
      | 223456785 | READY_TO_START       | INACTIVE  | CA-RLN1       | 2021-01-01 | ADECCO |
      | 223456786 | ASSIGNMENT_ENDED     | INACTIVE  | CA-RLN1-ZA    | 2021-01-01 | ADECCO |
      | 223456787 | ASSIGNMENT_CANCELLED | INACTIVE  | CA-RLN1-ZA-01 | 2021-01-01 | ADECCO |
      | 223456788 | ASSIGNED             | PENDING   | CA-RLN1       | 2021-01-01 | ADECCO |
      | 223456789 | READY_TO_START       | PENDING   | CA-RLN1       | 2021-01-01 | ADECCO |
      | 323456781 | ASSIGNMENT_ENDED     | PENDING   | CA-RLN1-ZA    | 2021-01-01 | ADECCO |
      | 323456782 | ASSIGNMENT_CANCELLED | PENDING   | CA-RLN1-ZA-01 | 2021-01-01 | ADECCO |

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
    And Check the employee "123456789" is not sent to RCA
    And the employee "123456789" is not sent to Adecco

  Scenario: A record with a start date 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "223456789"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date 6 days in the future
    And we ingest them
    When the employee "223456789" is sent to all downstream services
    Then the employee "223456789" is correctly created in gsuite with roleId "HA-CAR1" and orgUnit "ONS Managers"
    And the employee "223456789" is correctly created in ServiceNow with "HA-CAR1"
    And the employee from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
    And Check the employee "223456789" is sent to RCA
    And the employee "223456789" is sent to Adecco
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "223456789" with phone number "07234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "223456789" is sent to LWS as an create with name "Fransico" and phone number "07234567890" and "HA-CAR1" with expected hierarchy items "England & Wales" "Household" "A" "Carlisle" "Area Manager 1" "" ""
    And the employee "223456789" with roleId "HA-CAR1" device allocation details are sent to xma with IMEI number "990000888888888"

  Scenario: A record with a start date less than 6 days in the future is created in the downstream systems
    Given An employee exists in "ADECCO" with an id of "323456789"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "HA-CAR1"
    And a contract start date 5 days in the future
    And we ingest them
    When the employee "323456789" is sent to all downstream services
    And the employee "323456789" is correctly created in gsuite with roleId "HA-CAR1" and orgUnit "ONS Managers"
    Then the employee "323456789" is sent to Adecco
    And the employee "323456789" is correctly created in ServiceNow with "HA-CAR1"
    And the employee from "ADECCO" with roleId "HA-CAR1" is correctly created in XMA with group "7DD2611D-F60D-4A17-B759-B021BC5C669A"
    And the employee "is" in the Logisitics CSV with "HA-CAR1" as a create
    And Check the employee "323456789" is sent to RCA
      ### LWS Requires a device to be created ###
    And we ingest a device from pubsub for "323456789" with phone number "07234567890" and IMEI number "990000888888888"
    And we ingest them
    And the employee "323456789" is sent to LWS as an create with name "Fransico" and phone number "07234567890" and "HA-CAR1" with expected hierarchy items "England & Wales" "Household" "A" "Carlisle" "Area Manager 1" "" ""
    And the employee "323456789" with roleId "HA-CAR1" device allocation details are sent to xma with IMEI number "990000888888888"

  Scenario Outline: A HQ record is ingested and created
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    Then the HQ employee "00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    Given the roleId for "00000001" is set to "<role_id>" in gsuite
    When we retrieve the roleIds from GSuite for "00000001"
    Then the user "00000001" is added to the following groups "<groups>"
    Examples:
      | role_id    | groups            | lws   | xma   |
      | xx-RMTx    | hq-all,rmt-all    | false | false |
      | PT-FPHx-xx | hq-all,pt-fph-all | false | false |
      | PT-FPTx-xx | hq-all,pt-fpt-all | false | false |
      ### Add in extra service creates once implemented
