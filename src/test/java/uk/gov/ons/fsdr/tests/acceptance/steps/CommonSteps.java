package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoPeopleFactory;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SnowMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.XmaMockUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PropertySource("classpath:application.properties")
public class CommonSteps {

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

  public static AdeccoResponse adeccoResponse = new AdeccoResponse();
  public static GatewayEventMonitor gatewayEventMonitor = new GatewayEventMonitor();

  @Value("${service.rabbit.url}")
  private String rabbitLocation;

  @Value("${service.rabbit.username}")
  private String rabbitUsername;

  @Value("${service.rabbit.password}")
  private String rabbitPassword;

  @Value("${addeco.baseUrl}")
  private String mockAdeccoUrl;

  @Value("${rcaExtractLocation}")
  private String rcaExtractLocation;

  @Before
  public void setup() throws Exception {
    adeccoMockUtils.clearMock();
    adeccoMockUtils.cleardb();

    adeccoMockUtils.enableRequestRecorder();

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

  @Given("we run create actions")
  public void we_run_create_actions() throws IOException {
    fsdrUtils.ingestRunFSDRProcess();
  }

  @When("the employee is sent to all downstream services")
  public void theEmployeeIsSentToAllDownstreamServices() throws Exception {

    fsdrUtils.ingestGsuit();
    fsdrUtils.ingestXma();
    fsdrUtils.ingestSnow();
    fsdrUtils.ingestGranby();
    fsdrUtils.lwsExtract();
    fsdrUtils.rcaExtract();
  }

  @When("the employee is sent to LWS")
  public void theEmployeeIsSentToLWS() throws Exception {

    fsdrUtils.lwsExtract();
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
