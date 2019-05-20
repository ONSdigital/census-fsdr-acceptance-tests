package uk.gov.ons.census.fsdr.steps.endToEndIntegrationSteps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import uk.gov.ons.census.fsdr.utils.CSVReader;
import uk.gov.ons.census.fsdr.utils.CompareCsvAndDbRecords;
import uk.gov.ons.census.fsdr.utils.DbConnect;
import uk.gov.ons.census.fsdr.utils.ReadPropertyFile;

import java.io.IOException;

public class AdeccoToFSDR {

        private DbConnect dbConnect = new DbConnect();
        private CSVReader csvReader = new CSVReader();
        private ReadPropertyFile readPropertyFile = new ReadPropertyFile();
        private CompareCsvAndDbRecords compareCsvAndDbRecords = new CompareCsvAndDbRecords();
        String [][] FSDRData = new String [40000][50];
        String [][] AdeccoData = new String [40000][50];

        @Before
        public void setup() {

        }

        @After
        public void tearDown() {
        }

        @Given("Adecco makes new data available")
        public void adecco_makes_new_data_available() throws IOException {
                FSDRData = dbConnect.queryAndRetrieveRecords();
                AdeccoData = csvReader.readCSV();
        }

        @Then("the FSDR is populated with new data and the record count is correct")
        public void the_FSDR_is_populated_with_new_data_and_the_record_count_is_correct() {
                compareCsvAndDbRecords.checkRecords(FSDRData, AdeccoData);
        }

      }