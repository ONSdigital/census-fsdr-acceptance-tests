@Acceptance
Feature: Movers

  Scenario Outline: A record receives an new Role Id and is correctly moved.
    Given An employee exists in "<source>" with an id of "123456789"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And the managers of "<role_id>" exist
    And the managers of "<new_role_id>" exist
    And we ingest them
    And the employee "123456789" is sent to all downstream services
    And a device exists in XMA with "<role_id>", "0123456789" and "Allocated"
    And we retrieve the devices from xma
#    And we run create actions
#    And the employee is sent to LWS
    And their old job role gets cancelled
    And we receive a new active job role from adecco for employee "123456789" with new role_id "<new_role_id>"
    And we ingest them
    When the employee "123456789" is sent to all downstream services
    Then the employee is correctly moved in gsuite with roleId "<new_role_id>" to "<new_org_unit>"
    And the employee "123456789" is no longer in the following groups "<old_groups>"
    And the employee "123456789" is now in the current groups "<new_groups>"
    Then the employee is correctly moved in ServiceNow with "<new_role_id>"
    Then the employee from "<source>" with old roleId "<role_id>" and new roleId "<new_role_id>" is correctly moved in XMA with group "<new_group>"
#    Then the employee "is" in the LWS CSV as mover with "<new_role_id>"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<new_role_id>"
    And Check the employee "123456789" is sent to RCA

    Examples:
      | role_id       | inLogisitcs | source | new_group                            | new_role_id | new_org_unit | new_groups                                                   | old_groups                                         |
      | CAR1          | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | RLN1        | Managers     | rln1-group,ccs-group,ons_users                               | car1-group,household-group                         |
      | CAR1-HA       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | RLN1-CA     | Managers     | rln1-group,rln1-ca-group,ccs-group,ccs_drive,ons_users       | car1-group,car1-ha-group,household-group,ons_drive |
      | CAR1-HA-01    | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | RLN1-CA-01  | EW-Officers  | rln1-ca-group,ccs-group,ccs_drive,ons_users                  | car1-ha-group,household-group,ons_drive            |
      | CAR1          | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | TCH1        | Managers     | tch1-group,household-group,ons_users                         | car1-group                                         |
      | CAR1-HA       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | TCH1-HA     | Managers     | tch1-group,tch1-ha-group,household-group,ons_users,ons_drive | car1-group,car1-ha-group                           |
      | CAR1-HA-01    | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | TCH1-HA-01  | EW-Officers  | tch1-ha-group,household-group,ons_users,ons_drive            | car1-ha-group                                      |
      | CAR1          | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | CAR1-HA     | Managers     | car1-group,car1-ha-group,household-group,ons_users,ons_drive | N/A                                                |
      | CAR1-HA       | is          | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | CAR1-HA-01  | EW-Officers  | car1-ha-group,household-group,ons_users,ons_drive            | car1-group                                         |
      | CAR1-HA-01    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | CAR1        | Managers     | car1-group,household-group,ons_users                         | car1-ha-group,ons_drive                            |
      | CAR1          | is          | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | CAR1-HA-01  | EW-Officers  | car1-ha-group,household-group,ons_users,ons_drive            | car1-group                                         |
      | CAR1-HA       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | CAR1        | Managers     | car1-group,household-group,ons_users                         | car1-ha-group,ons_drive                            |
      | CAR1-HA-01    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | CAR1-HA     | Managers     | car1-group,car1-ha-group,household-group,ons_users,ons_drive | N/A                                                |