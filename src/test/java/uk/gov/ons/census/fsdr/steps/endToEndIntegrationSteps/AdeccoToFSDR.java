package uk.gov.ons.census.fsdr.steps.endToEndIntegrationSteps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
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
                AdeccoData = csvReader.readCSV("files/validAdeccoData.csv");
        }

        @Then("the FSDR is populated with new data and the record count is correct")
        public void the_FSDR_is_populated_with_new_data_and_the_record_count_is_correct() {
                compareCsvAndDbRecords.checkRecords(FSDRData, AdeccoData, "files/validAdeccoData.csv");
        }

        @Given("Adecco makes new data available and is down for 2 days")
        public void Adecco_makes_new_data_available_and_is_down_for_2_days() throws IOException {
                AdeccoData = csvReader.readCSV("files/validAdeccoData2Days.csv");
        }

        @When("FSDR makes a new API call after 2 days (when Adecco is up)")
        public void FSDR_makes_a_new_API_call_after_2_days_when_Adecco_is_up_() throws IOException {
                FSDRData = dbConnect.queryAndRetrieveRecords();
        }

        @Then("the FSDR is populated with new data and the record count is correct for the period since the Adecco was down (2 days data + current day data)")
        public void the_FSDR_is_populated_with_new_data_and_the_record_count_is_correct_for_the_period_since_the_Adecco_was_down() {
                compareCsvAndDbRecords.checkRecords(FSDRData, AdeccoData, "files/validAdeccoData.csv");
        }

      }