Feature: Pass 'House Hold Case' instructions from Response Manager to COMET

  Scenario: Pass 'Create Case' instruction to COMET with all correct data
   Given reset the TMMock and MQ
    And a create case in the MQ with correct data
    And the case is picked and sent to TM

  Scenario: Pass a duplicate 'Create Case' instruction to COMET with all correct data
   Given a duplicate create case in the MQ with the following data
    And the duplicate case is picked and sent to TM

  Scenario: Pass 'Create Case' instruction to COMET with a missing ARID attribute
    Given a create case in the MQ with the missing ARID attribute
    And the missing ARID attribute case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing AddressLine1 attribute
    Given a create case in the MQ with the missing AddressLine1 attribute
    And the missing AddressLine1 attribute case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing EstabType attribute
    Given a create case in the MQ with the missing EstabTyp eattribute
    And the missing EstabType attribute case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing Postcode attribute
    Given a create case in the MQ with the missing Postcode attribute
    And the missing Postcode attribute case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing Typeattribute
    Given a create case in the MQ with the missing Type attribute
    And the missing Type attribute case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing UPRN attribute
    Given a create case in the MQ with the missing UPRN attribute
    And the missing UPRN attribute case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing Locality attribute
    Given a create case in the MQ with the missing Locality attribute
    And the missing Locality attribute case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing ARID tag
    Given a create case in the MQ with the missing ARID tag
    And the missing ARID tagecase is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing AddressLine1 tag
    Given a create case in the MQ with the missing AddressLine1 tag
    And the missing AddressLine1 tag case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing EstabType tag
    Given a create case in the MQ with the missing EstabType tag
    And the missing EstabType tag case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing Postcode tag
    Given a create case in the MQ with the missing Postcode tag
    And the missing Postcode tag case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing Type tag
    Given a create case in the MQ with the missing Type tag
    And the missing Type tag case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing UPRN tag
    Given a create case in the MQ with the missing UPRN tag
    And the missing UPRN tag case is sent to dead letter queue

  Scenario: Pass 'Create Case' instruction to COMET with a missing Locality tag
    Given a create case in the MQ with the missing Locality tag
    And the missing Locality tag case is sent to dead letter queue