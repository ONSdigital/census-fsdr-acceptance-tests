package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SnowMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.XmaMockUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;

@Slf4j
@PropertySource("classpath:application.properties")
public class LeaverSteps {

  @Autowired
  private GsuiteMockUtils gsuiteMockUtils;

  @Autowired
  private XmaMockUtils xmaMockUtils;

  @Autowired
  private SnowMockUtils snowMockUtils;

  @Autowired
  private SftpUtils sftpUtils;

  @Autowired
  private ResourceLoader resourceLoader;

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

  @Then("the employee is correctly suspended in gsuite")
  public void theEmployeeIsCorrectlySuspendedInGsuite() {
    String[] records = gsuiteMockUtils.getRecords();
    String suspended1 = records[1];
    String suspended2 = records[2];
    assertEquals("{\"changePasswordAtNextLogin\":true,\"suspended\":true}", suspended1);
    assertEquals("{\"changePasswordAtNextLogin\":false}", suspended2);
  }

  @Then("the employee with roleId {string} is correctly suspended in XMA")
  public void theEmployeeIsCorrectlySuspendedInXMA(String roleId) {

    String id = xmaMockUtils.getId(roleId);

    System.out.println(id);
    String[] records = xmaMockUtils.getRecords();

    assertEquals(2, records.length);
    assertEquals("{\"className\":\"RequestManagement.Request\",\"formValues\":[{\"name\":\"_DeletionUser\",\"value\":\"" + id + "\"}],\"lifecycle_name\":\"NewProcess8\"}", records[1]);

  }

  @Then("the employee is correctly suspended in ServiceNow with {string}")
  public void theEmployeeIsCorrectlySuspendedInSNow(String roleId) {
    String[] records = snowMockUtils.getRecords();
    String suspended = records[records.length-1];

    assertThat(suspended).containsPattern("\"active\":false");
    assertThat(suspended).containsPattern("\"u_employment_status\":\"Left\"");
    assertThat(suspended).containsPattern(".*\"user_name\":\""+roleId+"[0-9]{6}\".*");

  }

  @Then("the employee {string} in the Logisitics CSV with {string} and phone number {string} as a leaver")
  public void theEmployeeIsCorrectInTheLogisticsCsv(String inCsv, String roleId, String phoneNumber) throws Exception {
    String csvFilename = null;
    List<GatewayEventDTO> logistics_extract_sent = gatewayEventMonitor.getEventsForEventType("LOGISTICS_EXTRACT_SENT", 10);
    for (GatewayEventDTO gatewayEventDTO : logistics_extract_sent) {
      csvFilename = gatewayEventDTO.getMetadata().get("logisticsFilename");
    }
    if(inCsv.equals("is")) {
      String csv = sftpUtils.getCsv("logistics/", csvFilename);
      assertThat(csv).containsPattern("\"Fransico\",\"Buyo\",,\"123\",\"Fake Street\",\"Faketon\",\"Fakeside\",\"FA43 1AB\",\"\",\"f.b@email.com\",\"Fransico.Buyo[0-9]{2}@domain\",\"0987654321\",\""+phoneNumber+"\",,\""+roleId+"\",,\"LEFT\"");
    } else {
      assertThat(csvFilename).isBlank();
    }
  }

  @Then("the employee {string} in the LWS CSV as a leaver")
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

  @Then("Check the employee {string} is not sent to RCA")
  public void checkTheEmployeeSendToRCA(String employeeId) throws IOException {
    String csvFilename = null;
    List<GatewayEventDTO> logistics_extract_sent = gatewayEventMonitor.getEventsForEventType("RCA_EXTRACT_COMPLETE", 10);
    for (GatewayEventDTO gatewayEventDTO : logistics_extract_sent) {
      csvFilename = gatewayEventDTO.getMetadata().get("CSV Filename");
    }
    if (csvFilename == null) {
      fail("RCA csv filename not found in event log");
    }
    assertFalse(csvFilename.isBlank());
    String rcaFile = rcaExtractLocation + csvFilename;
    Resource resource = resourceLoader.getResource(rcaFile);
    String fileContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    assertThat(fileContent).contains("Employee ID number").doesNotContain(employeeId);

  }

}
