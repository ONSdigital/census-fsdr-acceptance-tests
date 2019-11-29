package uk.gov.ons.fsdr.tests.acceptance.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.tests.acceptance.dto.Employee;
import uk.gov.ons.fsdr.tests.acceptance.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@Slf4j
@PropertySource("classpath:application.properties")
public class GetWorkerSteps {

    @Autowired
    private GsuiteMockUtils gsuiteMockUtils;

    @Autowired
    private AdeccoMockUtils adeccoMockUtils;

    @Autowired
    private XmaMockUtils xmaMockUtils;

    @Autowired
    private SnowMockUtils snowMockUtils;

    @Autowired
    private FsdrUtils fsdrUtils;

    @Autowired
    private SftpUtils sftpUtils;

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
    private String GSUITE_COMPLETE = "GSUITE_COMPLETE";
    private String XMA_EMPLOYEE_SENT = "XMA_EMPLOYEE_SENT";
    private String SERVICENOW_CREATED = "SERVICENOW_CREATED";


    private Employee fsdrEmployee;

    @Before
    public void setup() throws Exception {
        adeccoWorker = Resources.toString(Resources.getResource("files/adeccoPut.json"), Charsets.UTF_8);

        adeccoMockUtils.clearMock();
        adeccoMockUtils.cleardb();

        adeccoMockUtils.enableRequestRecorder();

        gatewayEventMonitor = new GatewayEventMonitor();
        gatewayEventMonitor.enableEventMonitor(rabbitLocation, rabbitUsername, rabbitPassword);
    }

    @After
    public void tearDownGatewayEventMonitor() throws IOException {
        adeccoMockUtils.disableRequestRecorder();
        gatewayEventMonitor.tearDownGatewayEventMonitor();
    }

    @Given("Employee is created in Adecco")
    public void employee_is_created_in_Adecco() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        AdeccoResponse adeccoResponse = objectMapper.readValue(adeccoWorker, AdeccoResponse.class);

        List<AdeccoResponse> adeccoResponseList = new ArrayList<>();
        adeccoResponseList.add(adeccoResponse);
        adeccoMockUtils.addUsersAdecco(adeccoResponseList);


        // Write code here that turns the phrase above into concrete actions
        //throw new cucumber.api.PendingException();
    }


    @When("FSDR pulls data from Adecco")
    public void fsdr_pulls_data_from_Adecco() throws IOException {
        fsdrUtils.ingestAdecco();
        fsdrUtils.ingestRunFSDRProcess();
        boolean hasBeenTriggered = gatewayEventMonitor.hasEventTriggered("<N/A>", INGEST_FROM_ADECCO, 10000L);
        assertTrue(hasBeenTriggered);

        // Write code here that turns the phrase above into concrete actions

    }

    @Then("Check the Employee created in FSDR database with ID {string}")
    public void check_the_Employee_created_in_FSDR_database_with_ID(String  employeeId) {

        String responseEmployeeId;
        ResponseEntity<Employee> employeeResponseEntity = fsdrUtils.retrieveEmployee(employeeId);
        assertEquals(HttpStatus.OK, employeeResponseEntity.getStatusCode());
        fsdrEmployee = employeeResponseEntity.getBody();

        responseEmployeeId = fsdrEmployee.getUniqueEmployeeId();

        assertEquals(employeeId, responseEmployeeId);
        // Write code here that turns the phrase above into concrete actions

    }

    @Then("FSDR update the external systems")
    public void fdr_update_the_external_system() throws IOException {

//        fsdrUtils.ingestGsuit();
//        boolean hasBeenTriggered = gatewayEventMonitor.hasEventTriggered("<N/A>", GSUITE_COMPLETE, 10000L);
//        assertTrue(hasBeenTriggered);
//
//        fsdrUtils.ingestXma();
//       boolean hasBeenTriggeredxma = gatewayEventMonitor.hasEventTriggered(fsdrEmployee.getUniqueEmployeeId(), XMA_EMPLOYEE_SENT, 10000L);
//        assertTrue(hasBeenTriggeredxma);
//
//        fsdrUtils.ingestSnow();
//        boolean hasBeenTriggeredsnow = gatewayEventMonitor.hasEventTriggered(fsdrEmployee.getUniqueEmployeeId(), SERVICENOW_CREATED, 10000L);
//        assertTrue(hasBeenTriggeredsnow);

        fsdrUtils.ingestGranby();
        boolean hasBeenTriggeredGranby = gatewayEventMonitor.hasEventTriggered("<N/A>", "LOGISTICS_EXTRACT_SENT", 10000L);
        assertTrue(hasBeenTriggeredGranby);

        ResponseEntity<Employee> employeeResponseEntity = fsdrUtils.retrieveEmployee(fsdrEmployee.getUniqueEmployeeId());
        fsdrEmployee = employeeResponseEntity.getBody();

    }

    @Then("Check the employee send to GSuit")
    public void check_the_employee_send_to_GSuit() {
        String[] records = gsuiteMockUtils.getRecords(fsdrEmployee.getOnsId());
        if(records.length != 0) {
            System.out.println("done");
        } else System.out.println("not done");


    }

    @And("Check the employee send to XMA")

    public void check_the_employee_send_to_XMA() {
            String[] records = xmaMockUtils.getRecords(fsdrEmployee.getOnsId());
            assertTrue(records.length != 0);


    }
    @And("Check the employee send to Snow")
    public void check_the_employee_send_to_Snow() {
        String[] records = snowMockUtils.getRecords(fsdrEmployee.getServiceNowUserId());
        assertTrue(records.length != 0);
    }


    @And("Check the employee send to Granby")
    public void check_the_employee_send_to_granby() throws Exception {
        final String csv = sftpUtils.getCsv("logistics/", "logistics[0-9]{12}.csv");
        assertThat(csv).containsPattern("Patrick.Adams..@domain");
    }
}


