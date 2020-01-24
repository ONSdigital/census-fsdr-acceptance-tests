package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@Slf4j
@PropertySource("classpath:application.properties")
public class RcaSteps {

  @Autowired
  private SftpUtils sftpUtils;

  @Autowired
  private ResourceLoader resourceLoader;

  public static GatewayEventMonitor gatewayEventMonitor = new GatewayEventMonitor();

  @Value("${service.rabbit.url}")
  private String rabbitLocation;

  @Value("${service.rabbit.username}")
  private String rabbitUsername;

  @Value("${service.rabbit.password}")
  private String rabbitPassword;

  @Value("${rcaExtractLocation}")
  private String rcaExtractLocation;

  @Then("Check the employee {string} is sent to RCA")
  public void checkTheEmployeeSendToRCA(String employeeId) throws IOException {
    String csvFilename = sftpUtils.getRcaFileName();
    if (csvFilename == null) {
      fail("RCA csv filename not found in event log");
    }
    assertFalse(csvFilename.isBlank());
    String rcaFile = rcaExtractLocation + csvFilename;
    Resource resource = resourceLoader.getResource(rcaFile);
    String fileContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    assertThat(fileContent).contains("Employee ID number").contains(employeeId);

  }

  @Then("Check the employee {string} is not sent to RCA")
  public void checkTheEmployeeNotSendToRCA(String employeeId) throws IOException {
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
