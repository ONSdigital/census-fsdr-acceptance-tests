package uk.gov.ons.fsdr.tests.acceptance.steps;

import static junit.framework.TestCase.assertTrue;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponse;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponseList;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponseManagers;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.sentManagerIds;
import static uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoPeopleFactory.buildAreaManagerTypeManager;
import static uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoPeopleFactory.buildCoordinatorTypeManager;

import java.io.IOException;

import cucumber.api.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.MockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.LwsMockUtils;
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
    sentManagerIds.clear();
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
    fsdrUtils.rcaExtract();
  }

  @Given("the managers of {string} exist and have been sent downstream")
  public void theManagersOfExist(String roleId) throws IOException {
    String am, co;
    if(sentManagerIds.isEmpty()) {
      am = "AM1";
      co = "CO1";
    } else {
      am = "AM2";
      co = "CO2";
    }
      buildAreaManagerTypeManager(roleId, am);
      adeccoMockUtils.addUsersAdecco(adeccoResponseManagers);
      fsdrUtils.ingestAdecco();
      fsdrUtils.ingestRunFSDRProcess();
      adeccoResponseManagers.clear();
    if(roleId.length() > AREA_MANAGER_ROLE_ID_LENGTH) {
      assertTrue(gatewayEventMonitor.hasEventTriggered(am, "SENDING_XMA_ACTION_RESPONSE", 10000L));
    }

    if (roleId.length() > COORDINATOR_ROLE_ID_LENGTH) {
      buildCoordinatorTypeManager(roleId, co);
      assertTrue(gatewayEventMonitor.hasEventTriggered(co, "SENDING_XMA_ACTION_RESPONSE", 10000L));
    }
  }

  @Given("we run create actions")
  public void we_run_create_actions() throws IOException {
    fsdrUtils.ingestRunFSDRProcess();
    fsdrUtils.ingestGranby();
    fsdrUtils.rcaExtract();
  }

  @Given("we retrieve the roleIds from GSuite for {string}")
  public void we_retrieve_the_roleIds_from_GSuite(String id) throws IOException {
    fsdrUtils.retrieveHqRoleIds();
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "HQ_ROLE_ID_RECEIVED", 10000L));

  }

  @When("we run HQ actions")
  public void we_run_HQ_actions() throws IOException {
    fsdrUtils.sendHqActions();
    assertTrue(gatewayEventMonitor.hasEventTriggered("<N/A>", "HQ_ACTIONS_COMPLETE", 10000L));
  }

}
