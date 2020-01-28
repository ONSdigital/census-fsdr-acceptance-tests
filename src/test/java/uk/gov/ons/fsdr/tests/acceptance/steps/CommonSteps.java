package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SnowMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.XmaMockUtils;

import java.io.IOException;

import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoSteps.adeccoResponse;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoSteps.adeccoResponseList;

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
  }

  @Given("we ingest them")
  public void we_ingest_them() throws IOException {
    if(adeccoResponseList.size() == 0) {
      adeccoResponseList.add(adeccoResponse);

    }
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

  @Given("we retrieve the devices from xma")
  public void we_retrieve_the_devices_from_xma() throws IOException {
    fsdrUtils.devices();
  }

}