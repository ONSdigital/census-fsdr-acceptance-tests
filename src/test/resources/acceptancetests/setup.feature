@Acceptance
Feature: Setup

  Scenario Outline: A record is created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And a contract start date of "2020-01-01"
    And the managers of "<role_id>" exist
    And we ingest managers
    And we ingest them
    Then the employee "<id>" is correctly created in gsuite with roleId "<role_id>" and orgUnit "Zero Access"
    And the employee assignment status changes to "TRAINING IN PROGRESS"
    And we ingest them
    Then the employee "<id>" is correctly setup in gsuite with orgUnit "<org_unit>"
    And the employee "<id>" is now in the current groups "<new_groups>"
    And Check the employee "<id>" is sent to RCA
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
