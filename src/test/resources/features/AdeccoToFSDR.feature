Feature: Ingest Adecco field staff data into FSDR

  Scenario: Pull new data from Adecco into FSDR and all the data is in correct format.
    Given Adecco makes new data available
    Then the FSDR is populated with new data and the record count is correct

  Scenario: IF Adecco goes down for 2 days
    Given Adecco makes new data available and is down for 2 days
    When FSDR makes a new API call after 2 days (when Adecco is up)
    Then the FSDR is populated with new data and the record count is correct for the period since the Adecco was down (2 days data + current day data).

