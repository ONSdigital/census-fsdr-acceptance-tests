Feature: Ingest Adecco field staff data into FSDR

  Scenario: Pull new data from Adecco into FSDR and all the data is in correct format.
    Given Adecco makes new data available
    Then the FSDR is populated with new data and the record count is correct

  Scenario: Send new data from FSDR to Airwatch.
    Given new data is available in FSDR
    Then a seperate account is created for each record sent by FSDR in Airwatch

  Scenario: Send encrypted csv file from FSDR to Logistics Granby using authorised user.
    Given new data is available in FSDR for logistics
    then the encrypted csv file has all the new records and the record count is correct.