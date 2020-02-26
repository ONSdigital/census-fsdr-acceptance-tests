package uk.gov.ons.fsdr.tests.acceptance.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;

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

  @Autowired StorageUtils storageUtils;

  @Then("Check the employee {string} is sent to RCA")
  public void checkTheEmployeeSendToRCA(String employeeId) throws IOException, URISyntaxException {
    String csvFilename = sftpUtils.getRcaFileName();
    if (csvFilename == null) {
      fail("RCA csv filename not found in event log");
    }
    assertFalse(csvFilename.isBlank());
    String rcaFile = rcaExtractLocation + csvFilename;
    InputStream rcaFileStream = storageUtils.getFileInputStream(new URI(rcaFile));
    String fileContent = new String(rcaFileStream.readAllBytes(), StandardCharsets.UTF_8);
    rcaFileStream.close();
    assertThat(fileContent).contains("Employee ID number").contains(employeeId);

  }

  @Then("Check the employee {string} is not sent to RCA")
  public void checkTheEmployeeNotSendToRCA(String employeeId) throws IOException, URISyntaxException {
    String csvFilename = null;
    gatewayEventMonitor.hasEventTriggered("<N/A>", "RCA_EXTRACT_COMPLETE", 2000l);
    Collection<GatewayEventDTO> logistics_extract_sent = gatewayEventMonitor.grabEventsTriggered("RCA_EXTRACT_COMPLETE", 1, 100l);
    for (GatewayEventDTO gatewayEventDTO : logistics_extract_sent) {
      csvFilename = gatewayEventDTO.getMetadata().get("CSV Filename");
    }
    if (csvFilename == null) {
      fail("RCA csv filename not found in event log");
    }
    assertFalse(csvFilename.isBlank());
    String rcaFile = rcaExtractLocation + csvFilename;
    InputStream rcaFileStream = storageUtils.getFileInputStream(new URI(rcaFile));
    String fileContent = new String(rcaFileStream.readAllBytes(), StandardCharsets.UTF_8);
    rcaFileStream.close();
    assertThat(fileContent).contains("Employee ID number").doesNotContain(employeeId);

  }
}
