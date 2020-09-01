@Acceptance
Feature: HQ Employees

  Scenario Outline: A HQ record is ingested and created
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    And we run HQ actions
    Then the HQ employee "hq00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    Given the roleId for "hq00000001" is set to "<role_id>" in gsuite
    When we retrieve the roleIds from GSuite for "hq00000001"
    And we run HQ actions
    Then the user "hq00000001" is added to the following groups "<groups>"
    Examples:
      | role_id    | groups            | lws   | xma   |
      | xx-RMTx    | hq-all,rmt-all    | false | false |
      | PT-FPHx-xx | hq-all,pt-fph-all | false | false |
      | PT-FPTx-xx | hq-all,pt-fpt-all | false | false |
    ### Add in extra service creates once implemented

  Scenario: An existing HQ record is ingested and updated
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    And the HQ employee "hq00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    When A "HQ" ingest CSV "00000000_000003_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    Then the hq employee "hq00000001" is correctly updated in gsuite

  Scenario: An existing HQ record not ingested or updated
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    And the HQ employee "hq00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    When A "HQ" ingest CSV "00000000_000004_CFOD_HQ_Extract.csv" exists in SFTP
    And we ingest the HQ CSV
    And we run HQ actions
    Then the employee "AB-CDE1" is not updated in gsuite

  Scenario: A HQ record is left
    Given A "HQ" ingest CSV "00000000_000001_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    And we run HQ actions
    Then the HQ employee "hq00000001" is correctly created in gsuite with orgUnit "ONS HQ Staff"
    Given A "HQ" ingest CSV "00000000_000002_CFOD_HQ_Extract.csv" exists in SFTP
    When we ingest the HQ CSV
    And we run HQ actions
    Then the employee "hq00000001" is correctly suspended in gsuite