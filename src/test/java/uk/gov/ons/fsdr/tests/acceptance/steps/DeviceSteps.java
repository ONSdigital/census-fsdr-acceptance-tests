package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.common.dto.devicelist.DeviceDto;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponse;

@Slf4j
public class DeviceSteps {

  public static GatewayEventMonitor gatewayEventMonitor = new GatewayEventMonitor();

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${device.baseUrl}")
  private String mockDeviceUrl;

  @Autowired
  private GsuiteMockUtils gsuiteMockUtils;

  @Autowired
  private FsdrUtils fsdrUtils;

  private static int deviceCount;

  @Then("we ingest a device from pubsub for {string} with closing report id {string} with phone number {string} and IMEI number {string}")
  public void weIngestADeviceFromPubsubForWithPhoneNumber(String employeeId, String crId, String phoneNumber, String imeiNumber) throws Exception {
    String eId = employeeId + crId;
    String onsId = getOnsId(eId);
    if (onsId == null)
      fail("failed to find ons id for employee " + eId);
    postDevice(onsId, phoneNumber, imeiNumber);
    deviceCount++;
    Collection<GatewayEventDTO> devices = gatewayEventMonitor.grabEventsTriggered("SAVE_DEVICE_PHONE", deviceCount, 10000L);
    Optional<String> any = devices.stream().flatMap(meta -> meta.getMetadata().values().stream()).filter(number -> number.equals(phoneNumber)).findAny();
    assertTrue("Device event not found for " + phoneNumber, any.isPresent());
    assertTrue(gatewayEventMonitor.hasEventTriggered(eId, "SAVE_DEVICE_PHONE"));
  }

  private String getOnsId(String employeeId) throws Exception {
    String sql = String.format("SELECT ons_email_address FROM fsdr.employee WHERE unique_employee_id = '%s'", employeeId);
    String result = getDatabaseResult(sql, "ons_email_address");

    return result;
  }

  private int countDevices(String employeeId) throws Exception {
    String sql = String.format("select count(*) from fsdr.device where unique_employee_id = '%s';", employeeId);
    String result = getDatabaseResult(sql, "count");

    return Integer.parseInt(result);
  }

  private void postDevice(String onsId, String phoneNumber, String imeiNumber) {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockDeviceUrl + "createPubSub";
    log.info("createDevice-mock_url:" + url);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
        .queryParam("onsId", onsId)
        .queryParam("phoneNumber", phoneNumber)
        .queryParam("imeiNumber", imeiNumber);

    HttpEntity<?> entity = new HttpEntity<>(headers);

    restTemplate.exchange(
        builder.toUriString(),
        HttpMethod.POST,
        entity, HttpStatus.class);
  }

  @Then("the employee {string} with closing report id {string} will only have one phone")
  public void theEmployeeWillOnlyHaveOnePhone(String employeeId, String crId) throws Exception {
    int deviceCount = countDevices(employeeId + crId);
    assertEquals(deviceCount, 1);
  }

  private String getDatabaseResult(String statement, String column) throws Exception {
    Statement stmt = null;

    String result = null;
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      if (conn != null) {
        System.out.println("Connected to the database!");
        stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(statement);
        resultSet.next();
        result = resultSet.getString(column);
      }
    } finally {
      // finally block used to close resources
      try {
        if (stmt != null)
          stmt.close();
      } catch (SQLException se) {
      } // do nothing
    }
    return result;
  }

  @Then("we ingest a chromebook device for {string} with closing report id {string} with id {string}")
  public void weIngestAChromebookDeviceForThem(String employeeId, String crId, String deviceId) throws Exception {
    String onsId = getOnsId(employeeId + crId);
    if (onsId == null)
      fail("failed to find ons id for employee " + employeeId + crId);
    DeviceDto deviceDto = new DeviceDto();
    deviceDto.setOnsId(onsId);
    deviceDto.setDeviceId(deviceId);

    gsuiteMockUtils.addChromebook(deviceDto);

    fsdrUtils.ingestChromebooks();
    deviceCount++;
  }

  static int getDeviceCount() {
    return deviceCount;
  }

  static void resetDeviceCount() {
    deviceCount = 0;
  }
}
