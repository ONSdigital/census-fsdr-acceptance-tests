Feature: Ingest Adecco field staff data into FSDR

  Scenario: Pull new data from Adecco into FSDR and all the data is in correct format.
    Given Adecco makes new data available
    Then the FSDR is populated with new data and the record count is correct

  Scenario: Send new data from FSDR to Airwatch.
    Given new data is available in FSDR
    Then a seperate account is craeted for each record sent by FSDR in Airwatch