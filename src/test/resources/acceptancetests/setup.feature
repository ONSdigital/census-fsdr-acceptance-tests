@Acceptance
Feature: Setup

  Scenario Outline: A record is created in the downstream systems
    Given An employee exists in "<source>" with an id of "<id>"
    And an assignment status of "ASSIGNED"
    And a closing report status of "ACTIVE"
    And a role id of "<role_id>"
    And a closing report id of "<cr_id>"
    And a contract start date of "2020-01-01"
    And we ingest them
    Then the employee "<id>" with closing report id "<cr_id>" is correctly created in gsuite with roleId "<role_id>"
    And the employee assignment status changes to "TRAINING IN PROGRESS"
    And we receive an update from adecco for employee "<id>" with new first name "<new_name>"
    And we ingest them
    Then the employee "<id>" with closing report id "<cr_id>" is correctly setup in gsuite with orgUnit "<org_unit>" with name "<new_name>" and roleId "<role_id>"
    Then the employee "<id>" with closing report id "<cr_id>" is correctly updated in ServiceNow with "<role_id>" and name "<new_name>" and number "" and status "TRAINING_IN_PROGRESS"
    Then the employee "<inLogisitcs>" in the Logisitics CSV with "<role_id>" and phone number "" as an update with name "<new_name>"
    Then the employee "<id>" with closing report id "<cr_id>" from "<source>" with roleId "<role_id>" is correctly updated in XMA with name "<new_name>" and group "<group>"
    And the user "<id>" with closing report id "<cr_id>" is added to the following groups "<new_groups>"
    Examples:
      | id        | cr_id  | role_id       | inLogisitcs | source | group                                | org_unit     | new_groups    | new_name |
      | 400000001 | cr4001 | HB-CAR1       | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | ONS Managers | hb-all,hb-mgr | John     |
      | 400000002 | cr4002 | HB-CAR1-ZA    | is          | ADECCO | 7DD2611D-F60D-4A17-B759-B021BC5C669A | ONS Managers | hb-all,hb-mgr | John     |
      | 400000003 | cr4003 | HB-CAR1-ZA-01 | is not      | ADECCO | 8A2FEF60-9429-465F-B711-83753B234BDD | ONS Officers | hb-all        | John     |



  ## These are used locally for checking the two lookup files against a set of roleIds
  ## Eventually the file can be read from a bucket and these tests can run when the lookup file has changed
#  Scenario: Every roleId maps to one lookup row for gsuite groups
#    Given Every roleId maps to one lookup row for gsuite groups
#
#  Scenario: Every roleId maps to one lookup row for lws regions
#    Given Every roleId maps to one lookup row for lws regions
