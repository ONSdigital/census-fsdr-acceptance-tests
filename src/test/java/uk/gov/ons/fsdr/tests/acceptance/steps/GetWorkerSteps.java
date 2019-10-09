package uk.gov.ons.fsdr.tests.acceptance.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseList;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@PropertySource("classpath:application.properties")
public class GetWorkerSteps {

    @Autowired
    private AdeccoMockUtils adeccoMockUtils;

    @Autowired
    private FsdrUtils fsdrUtils;

    private GatewayEventMonitor gatewayEventMonitor;

    @Value("${service.rabbit.url}")
    private String rabbitLocation;

    @Value("${service.rabbit.username}")
    private String rabbitUsername;

    @Value("${service.rabbit.password}")
    private String rabbitPassword;

    @Value("${addeco.baseUrl}")
    private String mockAdeccoUrl;

    private String adeccoWorker;

    private String INGEST_FROM_ADECCO = "INGEST_FROM_ADECCO";
    @Before
    public void setup() throws IOException, TimeoutException {
        adeccoWorker = Resources.toString(Resources.getResource("files/adeccoPut.json"), Charsets.UTF_8);

        adeccoMockUtils.clearMock();
        adeccoMockUtils.enableRequestRecorder();

        gatewayEventMonitor = new GatewayEventMonitor();
        gatewayEventMonitor.enableEventMonitor(rabbitLocation, rabbitUsername, rabbitPassword);
    }

    @After
    public void tearDownGatewayEventMonitor() throws IOException {
        adeccoMockUtils.disableRequestRecorder();
        gatewayEventMonitor.tearDownGatewayEventMonitor();
    }

    @Given("Adecco has created a worker with an employee")
    public void adeccoHasCreatedAWorkerWithAnEmployeeIDOf() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        AdeccoResponse adeccoResponse = objectMapper.readValue(adeccoWorker, AdeccoResponse.class);

        List<AdeccoResponse> adeccoResponseList = new ArrayList<>();
        adeccoResponseList.add(adeccoResponse);
        adeccoMockUtils.addUsersAdecco(adeccoResponseList);
    }

    @Then("as FSDR system I can pull off Adecco")
    public void asFSDRSystemICanPullOffAdecco() throws IOException {
        fsdrUtils.ingestAdecco();
        boolean hasBeenTriggered = gatewayEventMonitor.hasEventTriggered("<N/A>", INGEST_FROM_ADECCO, 10000L);
        assertTrue(hasBeenTriggered);
    }

    @And("search database for {string} employee with ID {string}")
    public void searchDatabaseForEmployeeWithID(String source, String employeeId) {
        String responseEmployeeId;
        ResponseEntity<AdeccoResponseList> results = adeccoMockUtils.getEmployeeById(employeeId);

        assertEquals(results.getStatusCode(), HttpStatus.OK);

        AdeccoResponse adeccoResponse = results.getBody().getRecords().get(0);
        responseEmployeeId = adeccoResponse.getResponseContact().getEmployeeId();

        assertEquals(responseEmployeeId, employeeId);
    }
}
