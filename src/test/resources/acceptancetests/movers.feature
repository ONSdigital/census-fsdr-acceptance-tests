@Acceptance
Feature: Movers

  Scenario Outline: DO MOVERS CHANGE
    Given an employee "<person>" 
    And employee is of "<job_type>"
    And is Added to Adecco
    And is pulled into FSDR
    And pushed to GSuite
    And pushed to XMA
    When employee in Adecco is moved to "<moved_job_type>"
    And is pulled into FSDR
    
 
