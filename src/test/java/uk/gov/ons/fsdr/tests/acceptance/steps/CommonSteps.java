package uk.gov.ons.fsdr.tests.acceptance.steps;

import static junit.framework.TestCase.assertTrue;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponse;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponseLeaver;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponseList;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponseManagers;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.LwsMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.MockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.QueueClient;
import uk.gov.ons.fsdr.tests.acceptance.utils.ServiceNowMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.XmaMockUtils;

@Slf4j
@PropertySource("classpath:application.properties")
public class CommonSteps {

  @Autowired
  private QueueClient queueClient;

  @Autowired
  private GsuiteMockUtils gsuiteMockUtils;

  @Autowired
  private AdeccoMockUtils adeccoMockUtils;

  @Autowired
  private MockUtils mockUtils;

  @Autowired
  private XmaMockUtils xmaMockUtils;

  @Autowired
  private LwsMockUtils lwsMockUtils;

  @Autowired
  private ServiceNowMockUtils serviceNowMockUtils;

  @Autowired
  private FsdrUtils fsdrUtils;

  @Autowired
  private SftpUtils sftpUtils;

  public static GatewayEventMonitor gatewayEventMonitor = new GatewayEventMonitor();
  public static final int AREA_MANAGER_ROLE_ID_LENGTH = 7;
  public static final int COORDINATOR_ROLE_ID_LENGTH = 10;
  public static final int FIELD_OFFICER_ROLE_ID_LENGTH = 13;

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
    queueClient.clearQueues();
    mockUtils.clearMock();
    mockUtils.cleardb();
    gsuiteMockUtils.clearMock();
    serviceNowMockUtils.clearMock();
    xmaMockUtils.clearMock();
    adeccoMockUtils.clearUpdates();
    sftpUtils.clerarSftp();

    lwsMockUtils.clearMock();
    adeccoResponseList.clear();
    adeccoResponseManagers.clear();
    mockUtils.enableRequestRecorder();

    gatewayEventMonitor.enableEventMonitor(rabbitLocation, rabbitUsername, rabbitPassword);
  }

  @After
  public void tearDownGatewayEventMonitor() throws IOException {
    mockUtils.disableRequestRecorder();
    gatewayEventMonitor.tearDownGatewayEventMonitor();
  }

  @Given("we ingest them")
  public void we_ingest_them() throws IOException {
    if(adeccoResponseList.size() == 0) {
      adeccoResponseList.add(adeccoResponse);
    }
    adeccoMockUtils.addUsersAdecco(adeccoResponseList);

    fsdrUtils.ingestAdecco();
    fsdrUtils.ingestRunFSDRProcess();
    fsdrUtils.ingestGranby();
  }

  @Given("we ingest managers")
  public void we_ingest_managers() throws IOException {
    adeccoMockUtils.addUsersAdecco(adeccoResponseManagers);

    fsdrUtils.ingestAdecco();
    fsdrUtils.ingestRunFSDRProcess();
    adeccoResponseManagers.clear();

    Collection<GatewayEventDTO> gsuiteEvents = gatewayEventMonitor.grabEventsTriggered("SENDING_GSUITE_ACTION_RESPONSE", 5, 3000l);
    Collection<GatewayEventDTO> snowEvents = gatewayEventMonitor.grabEventsTriggered("SENDING_SERVICE_NOW_ACTION_RESPONSE", 5, 3000l);
    Collection<GatewayEventDTO> xmaEvents = gatewayEventMonitor.grabEventsTriggered("SENDING_XMA_ACTION_RESPONSE", 5, 3000l);

  }

  @Given("we run create actions")
  public void we_run_create_actions() throws IOException {
    fsdrUtils.ingestRunFSDRProcess();
    fsdrUtils.ingestGranby();
  }

  //TODO Replace these steps with event checks in individual service steps when event driven is complete
  @When("the employee {string} is sent to all downstream services")
  public void theEmployeeIsSentToAllDownstreamServices(String id) throws Exception {

    //Waits for movers/leavers/updates as they all need to do an initial create that will also trigger the same events
    gatewayEventMonitor.grabEventsTriggered("SENDING_XMA_ACTION_RESPONSE", 6, 5000L);
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+adeccoResponse.getClosingReportId(), "SENDING_XMA_ACTION_RESPONSE", 5000L));
  }

  //TODO Remove when event driven is finished
  @When("the employee {string} is not sent to all downstream services")
  public void theEmployeeIsNotSentToAllDownstreamServices(String id) throws Exception {
  //Calling non-event based integrations to ensure that employee is not sent to them
    fsdrUtils.ingestGranby();
    //fsdrUtils.rcaExtract();
  }

  @And("we ingest the cancel")
  public void weIngestTheCancel() throws IOException {
    if (adeccoResponseLeaver.isPresent()) {
      AdeccoResponse adeccoResponse = adeccoResponseLeaver.get();

      adeccoMockUtils.addUsersAdecco(List.of(adeccoResponse));

    fsdrUtils.ingestAdecco();
    fsdrUtils.ingestRunFSDRProcess();
    adeccoResponseManagers.clear();

    gatewayEventMonitor.grabEventsTriggered("SENDING_GSUITE_ACTION_RESPONSE", 4, 3000l);
    gatewayEventMonitor.grabEventsTriggered("SENDING_SERVICE_NOW_ACTION_RESPONSE", 4, 3000l);
    gatewayEventMonitor.grabEventsTriggered("SENDING_XMA_ACTION_RESPONSE", 4, 10000l);
    }
  }

  @Given("we retrieve the roleIds from GSuite for {string}")
  public void we_retrieve_the_roleIds_from_GSuite(String id) throws IOException {
    fsdrUtils.retrieveHqRoleIds();
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "HQ_ROLE_ID_RECEIVED", 5000L));

  }

  @When("we run HQ actions")
  public void we_run_HQ_actions() throws IOException {
    fsdrUtils.sendHqActions();
    assertTrue(gatewayEventMonitor.hasEventTriggered("<N/A>", "HQ_ACTIONS_COMPLETE", 5000L));
  }

}
