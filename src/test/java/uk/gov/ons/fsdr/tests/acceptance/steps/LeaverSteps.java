package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoPeopleFactory;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SnowMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.XmaMockUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Slf4j
@PropertySource("classpath:application.properties")
public class LeaverSteps {

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

  private AdeccoResponse adeccoResponse = new AdeccoResponse();

  @Value("${service.rabbit.url}")
  private String rabbitLocation;

  @Value("${service.rabbit.username}")
  private String rabbitUsername;

  @Value("${service.rabbit.password}")
  private String rabbitPassword;

  @Value("${addeco.baseUrl}")
  private String mockAdeccoUrl;
  private String adeccoWorker;

  @Before
  public void setup() throws Exception {
    adeccoMockUtils.clearMock();
    adeccoMockUtils.cleardb();

    adeccoMockUtils.enableRequestRecorder();

    gatewayEventMonitor = new GatewayEventMonitor();
    gatewayEventMonitor.enableEventMonitor(rabbitLocation, rabbitUsername, rabbitPassword);
  }

  @After
  public void tearDownGatewayEventMonitor() throws IOException {
    adeccoMockUtils.disableRequestRecorder();
    gsuiteMockUtils.clearMock();
    snowMockUtils.clearMock();
    xmaMockUtils.clearMock();
    gatewayEventMonitor.tearDownGatewayEventMonitor();
  }

  @Given("An employee exists in {string} with an id of {string}")
  public void we_recieve_an_employee_with_an_id_of(String source, String id) {

    adeccoResponse = AdeccoPeopleFactory.buildFransicoBuyo(id);
    adeccoResponse.setContractStartDate(LocalDate.now().toString());
  }

  @Given("an assignment status of {string}")
  public void an_assignment_status_of(String assignmentStatus) {
    adeccoResponse.setStatus(assignmentStatus);
  }

  @Given("a closing report status of {string}")
  public void a_closing_report_status_of(String crStatus) {
    adeccoResponse.setCrStatus(crStatus);
  }

  @Given("a role id of {string}")
  public void a_role_id_of(String roleId) {
    adeccoResponse.getResponseJob().setRoleId(roleId);
  }

  @Given("an operational end date of {string}")
  public void an_operational_end_date_of(String date) {
    adeccoResponse.setOperationalEndDate(date);
  }

  @When("we receive a job role update from adecco for employee  {string}")
  public void we_receive_a_job_role_update_from_adecco_for_employee(String id) {
    adeccoResponse.setContractStartDate(LocalDate.now().toString());
  }

  @Given("we ingest them")
  public void we_ingest_them() throws IOException {
    List<AdeccoResponse> adeccoResponseList = new ArrayList<>();
    adeccoResponseList.add(adeccoResponse);
    adeccoMockUtils.addUsersAdecco(adeccoResponseList);

    fsdrUtils.ingestAdecco();
    fsdrUtils.ingestRunFSDRProcess();

  }

  @When("the employee is sent to all downstream services")
  public void theEmployeeIsSentToAllDownstreamServices() throws Exception {

    fsdrUtils.ingestGsuit();
    fsdrUtils.ingestXma();
    fsdrUtils.ingestSnow();
    fsdrUtils.ingestGranby();
    fsdrUtils.lwsExtract();
  }

  @When("the employee is sent to LWS")
  public void theEmployeeIsSentToLWS() throws Exception {

    fsdrUtils.lwsExtract();
  }

  @Then("the employee is correctly suspended in gsuite")
  public void theEmployeeIsCorrectlySuspendedInGsuite() {
    String[] records = gsuiteMockUtils.getRecords();
    String suspended1 = records[1];
    String suspended2 = records[2];
    assertEquals("{\"changePasswordAtNextLogin\":true,\"suspended\":true}", suspended1);
    assertEquals("{\"changePasswordAtNextLogin\":false}", suspended2);
  }

  @Then("the employee is correctly suspended in XMA")
  public void theEmployeeIsCorrectlySuspendedInXMA() {
    String[] records = xmaMockUtils.getRecords();
    String suspended1 = records[records.length-2];
    String suspended2 = records[records.length-1];

    assertEquals("{\"changePasswordAtNextLogin\":true,\"suspended\":true}", suspended1);
    assertEquals("{\"changePasswordAtNextLogin\":false}", suspended2);
    assertEquals(6, records.length);
  }

  @Then("the employee is correctly suspended in ServiceNow with {string}")
  public void theEmployeeIsCorrectlySuspendedInSNow(String roleId) {
    String[] records = snowMockUtils.getRecords();
    String suspended = records[records.length-1];

    assertThat(suspended).containsPattern("\"active\":false");
    assertThat(suspended).containsPattern("\"u_employment_status\":\"Left\"");
    assertThat(suspended).containsPattern(".*\"user_name\":\""+roleId+"[0-9]{6}\".*");

  }

  @Then("the employee {string} in the Logisitics CSV with {string} and phone number {string}")
  public void theEmployeeIsCorrectInTheLogisticsCsv(String inCsv, String roleId, String phoneNumber) throws Exception {
    String csvFilename = null;
    List<GatewayEventDTO> logistics_extract_sent = gatewayEventMonitor.getEventsForEventType("LOGISTICS_EXTRACT_SENT", 10);
    for (GatewayEventDTO gatewayEventDTO : logistics_extract_sent) {
      csvFilename = gatewayEventDTO.getMetadata().get("logisticsFilename");
    }
    if(inCsv.equals("is")) {
      String csv = sftpUtils.getCsv("logistics/", csvFilename);
      assertThat(csv).containsPattern("\"Fransico\",\"Buyo\",,,,,,,\"\",,\"Fransico.Buyo[0-9]{2}@domain\",,\""+phoneNumber+"\",,\""+roleId+"\",,\"LEFT\"");
    } else {
      assertThat(csvFilename).isBlank();
    }
  }

  @Then("the employee {string} in the LWS CSV")
  public void theEmployeeIsCorrectInTheLwsCsv(String inCsv) throws Exception {
    String csvFilename = null;
    List<GatewayEventDTO> lws_extract_sent = gatewayEventMonitor.getEventsForEventType("LWS_EXTRACT_SENT", 10);
    for (GatewayEventDTO gatewayEventDTO : lws_extract_sent) {
      csvFilename = gatewayEventDTO.getMetadata().get("lwsFilename");
    }
    if(inCsv.equals("is")) {
      String csv = sftpUtils.getCsv("lws/", csvFilename);
      assertThat(csv).containsPattern("\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
          + "\"Fransico Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"ONS\",\"\",\"N/A\",\"N/A\",\"LEAVER\"");
    } else {
      assertThat(csvFilename).isBlank();
    }
  }

  @Given("a device exists in XMA with {string}, {string} and {string}")
  public void a_device_exists_in_XMA_with_and(String roleId, String phoneNumber, String status) {
    xmaMockUtils.postDevice(roleId,phoneNumber,status);
  }

  @Given("we retrieve the devices from xma")
  public void we_retrieve_the_devices_from_xma() throws IOException {
    fsdrUtils.devices();
  }
}
