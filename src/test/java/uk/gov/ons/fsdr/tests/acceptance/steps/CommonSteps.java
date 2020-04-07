package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SnowMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.XmaMockUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoSteps.adeccoResponse;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoSteps.adeccoResponseLeaver;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoSteps.adeccoResponseList;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoSteps.adeccoResponseManagers;

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

  public static GatewayEventMonitor gatewayEventMonitor = new GatewayEventMonitor();
  public static int AREA_MANAGER = 4;
  public static int COORDINATOR = 7;

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
    adeccoResponseList.clear();
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
    adeccoResponseList.clear();
    adeccoResponseManagers.clear();
  }

  @Given("we ingest them")
  public void we_ingest_them() throws IOException {
    if(adeccoResponseList.size() == 0) {
      adeccoResponseList.add(adeccoResponse);
    }
    adeccoMockUtils.addUsersAdecco(adeccoResponseList);
    adeccoMockUtils.addUsersAdecco(adeccoResponseManagers);

    fsdrUtils.ingestAdecco();
    fsdrUtils.ingestRunFSDRProcess();
    adeccoResponseManagers.clear();

  }

  @Given("we ingest managers")
  public void we_ingest_managers() throws IOException {
    adeccoMockUtils.addUsersAdecco(adeccoResponseManagers);

    fsdrUtils.ingestAdecco();
    fsdrUtils.ingestRunFSDRProcess();
    adeccoResponseManagers.clear();

    gatewayEventMonitor.grabEventsTriggered("SENDING_GSUITE_ACTION_RESPONSE", 5, 10000l);
    gatewayEventMonitor.grabEventsTriggered("SENDING_SERVICE_NOW_ACTION_RESPONSE", 5, 10000l);
    gatewayEventMonitor.grabEventsTriggered("SENDING_XMA_ACTION_RESPONSE", 5, 10000l);

  }

  @Given("we run create actions")
  public void we_run_create_actions() throws IOException {
    fsdrUtils.ingestRunFSDRProcess();
  }

  @When("the employee {string} is sent to all downstream services")
  public void theEmployeeIsSentToAllDownstreamServices(String id) throws Exception {

    //Waits for movers/leavers/updates as they all need to do an initial create that will also trigger the same events
    gatewayEventMonitor.grabEventsTriggered("SENDING_GSUITE_ACTION_RESPONSE", 6, 3000l);
    gatewayEventMonitor.grabEventsTriggered("SENDING_SERVICE_NOW_ACTION_RESPONSE", 6, 3000l);
    gatewayEventMonitor.grabEventsTriggered("SENDING_XMA_ACTION_RESPONSE", 6, 10000l);
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_GSUITE_ACTION_RESPONSE", 20000L));
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_SERVICE_NOW_ACTION_RESPONSE", 10000L));
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_XMA_ACTION_RESPONSE", 10000L));
    fsdrUtils.ingestGranby();
//    fsdrUtils.lwsExtract();
    fsdrUtils.rcaExtract();
  }

  @When("the employee {string} is not sent to all downstream services")
  public void theEmployeeIsNotSentToAllDownstreamServices(String id) throws Exception {
  //Calling non-event based integrations to ensure that employee is not sent to them
    fsdrUtils.ingestGranby();
    fsdrUtils.lwsExtract();
    fsdrUtils.rcaExtract();
  }

  @When("the employee is sent to LWS")
  public void theEmployeeIsSentToLWS() throws Exception {

    fsdrUtils.lwsExtract();
  }

  @Given("we retrieve the devices from xma")
  public void we_retrieve_the_devices_from_xma() throws IOException {
    fsdrUtils.devices();
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
}
