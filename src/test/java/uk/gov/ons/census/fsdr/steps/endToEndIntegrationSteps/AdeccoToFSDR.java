package uk.gov.ons.census.fsdr.steps.endToEndIntegrationSteps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import uk.gov.ons.census.fsdr.utils.CSVReader;
import uk.gov.ons.census.fsdr.utils.DbConnect;
import java.io.IOException;
import java.net.URISyntaxException;

public class AdeccoToFSDR {

        private DbConnect dbConnect = new DbConnect();
        private CSVReader csvReader = new CSVReader();

        @Before
        public void setup() {

        }

        @After
        public void tearDown() {
        }

        @Given("Adecco makes new data available")
        public void adecco_makes_new_data_available() throws IOException {
                String [][] FSDRData = new String [40000][50];
                FSDRData = dbConnect.postGressConnect();
                System.out.println(FSDRData[0][0]);
                String [][] AdeccoData = new String [40000][50];
                AdeccoData = csvReader.readCSV();
                System.out.println(AdeccoData[1][0]);
        }

        @Then("the FSDR is populated with new data and the record count is correct")
        public void the_FSDR_is_populated_with_new_data_and_the_record_count_is_correct() {
        }

      }