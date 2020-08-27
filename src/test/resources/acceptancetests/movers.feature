@Acceptance
Feature: Movers

  Scenario Outline: A record receives an new Role Id and is correctly moved.
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And the managers of "<role_id>" exist
    And the managers of "<new_role_id>" exist
    And we ingest managers
    And we ingest them
    And the employee "<id>" is sent to all downstream services
    And we ingest a device from pubsub for "<id>" with phone number "07234567890" and IMEI number "990000888888888"
    And we run create actions
    And their old job role gets cancelled
    And we receive a new active job role from adecco for employee "<id>" with new role_id "<new_role_id>"
    And we ingest them
    When the employee "<id>" is sent to all downstream services
    Then the employee is correctly moved in gsuite with roleId "<new_role_id>" to "<new_org_unit>"
    And the employee "<id>" is no longer in the following groups "<old_groups>"
    And the employee "<id>" is now in the current groups "<new_groups>"
    Then the employee "<id>" is sent to LWS as a mover with roleId "<new_role_id>" with expected hierarchy items "<hier1>" "<hier2>" "<hier3>" "<hier4>" "<hier5>" "<hier6>" "<hier7>"
#    Then the employee "<id>" is correctly moved in ServiceNow with "<new_role_id>" and asset id "990000888888888"
    Then the employee from "<source>" with old roleId "<role_id>" and new roleId "<new_role_id>" is correctly moved in XMA with group "<new_group>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<new_role_id>"
    And Check the employee "<id>" is sent to RCA

    Examples:
     | id        | role_id          | inLogisitcs | source | new_group                            | new_role_id    | new_org_unit| new_groups                                                            | old_groups                                            | hier1           | hier2                   | hier3 | hier4     | hier5          | hier6         | hier7        |
     | 123456780 | HA-CAR1          | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | CA-RUN1        | ONS Managers| ca-run1-group,ccs-group,ons_users                                     | ha-car1-group,household-group                         | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 |               |              |
     | 123456781 | HA-CAR1-ZA       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | CA-RUN1-ZA     | ONS Managers| ca-run1-group,ca-run1-za-group,ccs-group,ccs_drive,ons_users          | ha-car1-group,car1-za-group,household-group,ons_drive | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A |              |
     | 123456782 | HA-CAR1-ZA-01    | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | CA-RUN1-ZA-01  | ONS Officers| ca-run1-za-group,ccs-group,ccs_drive,ons_users                        | ha-car1-za-group,household-group,ons_drive            | England & Wales | Census Coverage Survey  | A     | Runnymede | Area Manager 1 | Team Leader A | 01 Tranche 1 |
     | 123456783 | HA-CAR1          | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | HA-TAW1        | ONS Managers| ha-taw1-group,household-group,ons_users                               | ha-car1-group                                         | England & Wales | Household               | A     | Tamworth  | Area Manager 1 |               |              |
     | 123456784 | HA-CAR1-ZA       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | HA-TAW1-ZA     | ONS Managers| ha-taw1-group,ha-taw1-za-group,household-group,ons_users,ons_drive    | ha-car1-group,car1-za-group                           | England & Wales | Household               | A     | Tamworth  | Area Manager 1 | Team Leader A |              |
     | 123456785 | HA-CAR1-ZA-01    | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | HA-TAW1-ZA-01  | ONS Officers| ha-taw1-za-group,household-group,ons_users,ons_drive                  | ha-car1-za-group                                      | England & Wales | Household               | A     | Tamworth  | Area Manager 1 | Team Leader A | 01 Tranche 1 |
     | 123456786 | HA-CAR1          | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | HA-CAR2-ZA     | ONS Managers| ha-car2-group,ha-car2-za-group,household-group,ons_users,ons_drive    | N/A                                                   | England & Wales | Household               | A     | Carlisle  | Area Manager 2 | Team Leader A |              |
     | 123456787 | HA-CAR1-ZA       | is          | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | HA-CAR2-ZA-01  | ONS Officers| ha-car2-za-group,household-group,ons_users,ons_drive                  | ha-car1-group                                         | England & Wales | Household               | A     | Carlisle  | Area Manager 2 | Team Leader A | 01 Tranche 1 |
     | 123456788 | HA-CAR1-ZA-01    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | HA-CAR2        | ONS Managers| ha-car2-group,household-group,ons_users                               | ha-car1-za-group,ons_drive                            | England & Wales | Household               | A     | Carlisle  | Area Manager 2 |               |              |
     | 123456789 | HA-CAR1          | is          | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | HA-CAR2-ZA-01  | ONS Officers| ha-car2-za-group,household-group,ons_users,ons_drive                  | ha-car1-group                                         | England & Wales | Household               | A     | Carlisle  | Area Manager 2 | Team Leader A | 01 Tranche 1 |
     | 123456780 | HA-CAR1-ZA       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | HA-CAR2        | ONS Managers| ha-car2-group,household-group,ons_users                               | ha-car1-za-group,ons_drive                            | England & Wales | Household               | A     | Carlisle  | Area Manager 2 |               |              |
     | 223456781 | HA-CAR1-ZA-01    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | HA-CAR2-ZA     | ONS Managers| ha-car2-group,ha-car2-za-group,household-group,ons_users,ons_drive    | N/A                                                   | England & Wales | Household               | A     | Carlisle  | Area Manager 2 | Team Leader A |              |