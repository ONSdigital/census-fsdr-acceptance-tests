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
        String [][] fsdrData = new String [40000][50];
        String [][] adeccoData = new String [40000][50];
        String [][] airWatchData = new String [40000][20];
        String [][] logisticsData = new String [40000][20];
        Integer rowCount = 0;
        String sqlData = null;
        String sqlCount;

        @Before
        public void setup() {

        }

        @After
        public void tearDown() {
        }

        @Given("Adecco makes new data available")
        public void adecco_makes_new_data_available() throws IOException {
                sqlCount = readPropertyFile.loadAndReadPropertyFile("sql_for_new_adecco_record_count");
                sqlData = readPropertyFile.loadAndReadPropertyFile("sql_for_new_adecco_data");
                rowCount = dbConnect.queryRecordCount(sqlCount);
                fsdrData = dbConnect.queryAndRetrieveRecords(rowCount, sqlData, "Adecco");
                adeccoData = csvReader.readAdeccoData("files/validAdeccoData.csv");
        }

        @Then("the FSDR is populated with new data and the record count is correct")
        public void the_FSDR_is_populated_with_new_data_and_the_record_count_is_correct() {
                compareCsvAndDbRecords.checkRecords(fsdrData, adeccoData, "files/validAdeccoData.csv", rowCount);
        }

        @Given("new data is available in FSDR for Airwatch")
        public void new_data_is_available_in_FSDR_for_Airwatch() throws IOException {
                sqlCount = readPropertyFile.loadAndReadPropertyFile("sql_for_new_airwatch_record_count");
                sqlData = readPropertyFile.loadAndReadPropertyFile("sql_for_new_airwatch_data");
                rowCount = dbConnect.queryRecordCount(sqlCount);
                fsdrData = dbConnect.queryAndRetrieveRecords(rowCount, sqlData, "AirWatch");
                airWatchData = csvReader.readAirWatchData("files/validAirWatchData.csv");
        }

        @Then("a seperate account is created for each record sent by FSDR in Airwatch")
        public void a_seperate_account_is_created_for_each_record_sent_by_FSDR_in_Airwatch() {
            compareCsvAndDbRecords.checkRecords(fsdrData, airWatchData, "files/validAirWatchData.csv", rowCount);
        }

        @Given("new data is available in FSDR for logistics")
        public void new_data_is_available_in_FSDR_for_logistics() throws IOException {
                sqlCount = readPropertyFile.loadAndReadPropertyFile("sql_for_new_logistics_record_count");
                sqlData = readPropertyFile.loadAndReadPropertyFile("sql_for_new_logistics_data");
                rowCount = dbConnect.queryRecordCount(sqlCount);
                fsdrData = dbConnect.queryAndRetrieveRecords(rowCount, sqlData, "Logistics");
                logisticsData = csvReader.readLogisticsData("files/validLogiscticsData.csv");
        }

        @Then("then the encrypted csv file has all the new records and the record count is correct")
        public void then_the_encrypted_csv_file_has_all_the_new_records_and_the_record_count_is_correct() {
                compareCsvAndDbRecords.checkRecords(fsdrData, logisticsData, "files/validLogiscticsData.csv", rowCount);
        }

      }