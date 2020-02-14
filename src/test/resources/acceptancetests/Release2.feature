#@Acceptance
#Feature: Release 2
#
#  Scenario Outline: Single Closing Report
#    Given an employee "<person>"
#    And has "<cr-qty>" Job Roles
#    And CR-Status for Job Role 1 is "<jr1-cr-status>"
#    And Assignment-Status for Job Role 1 is "<jr1-assignment-status>"
#    And CR-Status for Job Role 2 is "<jr2-cr-status>"
#    And Assignment-Status for Job Role 2 is "<jr2-assignment-status>"
#    And is added to Adecco
#    When FSDR system pulls updates from Adecco
#    And FSDR system runs FSDR-Process
#    Then the employee whether it is updated is "<is-received-from-adecco>"
#    And isActive is "<is-active>"
#    And whether Interface Action Row Exists is "<acion-row-exists>"
#    And GSuite Action is "<gsuite-action>"
#    And XMA Action    is "<xma-action>"
#    And LWS Action    is "<lws-action>"
#    And Granby Action is "<granby-action>"
#    And SNOW Action   is "<snow-action>"
#
#    Examples:
#    | person   | cr-qty | jr1-cr-status  | jr1-assignment-status | jr2-cr-status | jr2-assignment-status | is-received-from-adecco | is-active | acion-row-exists | gsuite-action   | xma-action   | lws-action   | granby-action   | snow-action   |
#    | person1  | 1      | Active         | Ready to Start        |               |                       | true                    | true      | true             | CREATE          | CREATE       | CREATE       | CREATE          | CREATE        |
#    | person2  | 1      | Active         | Assigned              |               |                       | true                    | true      | true             | CREATE          | CREATE       | CREATE       | CREATE          | CREATE        |
#    | person4  | 1      | Active         | Assignment Cancelled  |               |                       | true                    | false     | true             | LEFT            | LEFT         | LEFT         | LEFT            | LEFT          |
#    | person5  | 1      | Active         | Assignment Ended      |               |                       | true                    | false     | true             | LEFT            | LEFT         | LEFT         | LEFT            | LEFT          |
#    | person8  | 2      | Active         | Ready to Start        | Active        | Ready to Start        | true                    | true      | true             | ERROR           | ERROR        | ERROR        | ERROR           | ERROR         |
#    | person9  | 2      | Active         | Assigned              | Cancelled     | Assignment Cancelled  | true                    | true      | true             | CREATE          | CREATE       | CREATE       | CREATE          | CREATE        |
#    | person10 | 2      | Active         | Assigned              | Cancelled     | Assignment Ended      | true                    | true      | true             | CREATE          | CREATE       | CREATE       | CREATE          | CREATE        |
#    | person12 | 2      | Active         | Assigned              | Cancelled     | Assigned              | true                    | true      | true             | CREATE          | CREATE       | CREATE       | CREATE          | CREATE        |
#    | person13 | 1      | Active         | Assigned              |               |                       | true                    | true      | true             | CREATE          | CREATE       | CREATE       | CREATE          | CREATE        |
#