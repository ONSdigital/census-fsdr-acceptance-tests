package uk.gov.ons.fsdr.tests.acceptance.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Given;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.adeccoResponse;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;

@Slf4j
@PropertySource("classpath:application.properties")
public class UpdateSteps {

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

  private final ObjectMapper objectMapper = new ObjectMapper();

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

  @Given("we receive an update from adecco for employee {string} with new first name {string}")
  public void we_receive_an_update_from_adecco_for_employee_with_new_first_name(String id, String newFName) {
    adeccoResponse.getResponseContact().setFirstName(newFName);
  }

  @Then("the employee is correctly updated in gsuite with name {string}")
  public void the_employee_is_correctly_updated_in_gsuite(String name) throws IOException {
    String[] records = gsuiteMockUtils.getRecords();
    String update = records[1];

    JsonNode expectedMessageRootNode = objectMapper
        .readTree("{\"name\":{\"familyName\":\"Buyo\",\"givenName\":\"" + name + "\"}}");
    JsonNode actualMessageRootNode = objectMapper.readTree(update);

    assertEquals(expectedMessageRootNode, actualMessageRootNode);
  }

  @Then("the employee is correctly updated in ServiceNow with {string} and name {string} and number {string}")
  public void the_employee_is_correctly_updated_in_ServiceNow_with(String roleId, String name, String phoneNumber) {
    String[] records = snowMockUtils.getRecords();
    String update = records[records.length - 1];
    String expectedMessageRootNode = "";
    if (!phoneNumber.equals("")) {
      expectedMessageRootNode = "\"location\":\"London\",\"first_name\":\"" + name
          + "\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,\"u_lm_first_name_2\":null,\"u_lm_last_name_2\":null,\"user_name\":\""
          + roleId
          + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"2019-12-23\",\"u_contract_end_date\":\"2019-12-28\",\"u_employment_status\":\"ACTIVE\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"Fransico.Buyo[0-9]{2}@domain\",\"u_asset_number\":\"[0-9a-z-]{36}\",\"u_ons_device_number\":\""
          + phoneNumber + "\",\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true";
    } else
      expectedMessageRootNode = "\"location\":\"London\",\"first_name\":\"" + name
          + "\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,\"u_lm_first_name_2\":null,\"u_lm_last_name_2\":null,\"user_name\":\""
          + roleId
          + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"2019-12-23\",\"u_contract_end_date\":\"2019-12-28\",\"u_employment_status\":\"ACTIVE\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"Fransico.Buyo[0-9]{2}@domain\",\"u_asset_number\":null,\"u_ons_device_number\":null,\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true";

    assertThat(update).containsPattern(expectedMessageRootNode);
  }

  @Then("the employee from {string} with roleId {string} is correctly updated in XMA with name {string} and group {string}")
  public void the_employee_with_roleId_is_correctly_updated_in_XMA(String source, String roleId, String name,
      String group) {

    String id = xmaMockUtils.getId(roleId);

    System.out.println(id);
    String[] records = xmaMockUtils.getRecords();
    for (String record : records) {
      System.out.println(record);
    }

    assertEquals(2, records.length);
    assertThat(records[1]).contains(
        "{\"className\":\"System.EndUser\",\"formValues\":[{\"name\":\"_BadgeNumber\",\"value\":null},{\"name\":\"_EmploymentStatus\",\"value\":\"ACTIVE\"},{\"name\":\"_FirstName\",\"value\":\""
            + name
            + "\"},{\"name\":\"_ContractStartDate\",\"value\":\"2019-12-23\"},{\"name\":\"_ContractEndDate\",\"value\":\"2019-12-28\"},{\"name\":\"_JobRole\",\"value\":null},{\"name\":\"_LineManagerString\",\"value\":\"null null\"},{\"name\":\"_LocationString\",\"value\":\"London\"},{\"name\":\"_RoleID\",\"value\":\""
            + roleId + "\"},{\"name\":\"CurrentGroup\",\"value\":\"" + group
            + "\"},{\"name\":\"PrimaryGroup\",\"value\":\"" + group + "\"},{");
    assertThat(records[1]).containsPattern("\"name\":\"Name\",\"value\":\"Fransico.Buyo[0-9]{2}@domain\"");
    assertThat(records[1]).contains(
        "},{\"name\":\"_PreferredName\",\"value\":null},{\"name\":\"_UserOrg\",\"value\":\"" + source
            + "\"},{\"name\":\"_Surname\",\"value\":\"Buyo\"},{");
    assertThat(records[1]).containsPattern("\"name\":\"EMailAddress\",\"value\":\"Fransico.Buyo[0-9]{2}@domain\"");
    assertThat(records[1]).contains("},{\"name\":\"Title\",\"value\":\"" + name
        + " Buyo\"},{\"name\":\"_PersonalEmail\",\"value\":\"f.b@email.com\"},{\"name\":\"_PersonalPhone\",\"value\":\"0987654321\"},{\"name\":\"_Address\",\"value\":\"123, Fake Street, Fakeside, FA43 1AB\"}],\"key\":\""
        + id + "\",\"originalValues\":null,\"lockVersion\":1}");
  }

  @Then("the employee {string} in the LWS CSV as an update with name {string} and phone number {string} and {string}")
  public void the_employee_in_the_LWS_CSV_as_an_update(String inCsv, String name, String number, String roleId)
      throws Exception {
    String csvFilename = null;
    List<GatewayEventDTO> lws_extract_sent = gatewayEventMonitor.getEventsForEventType("LWS_EXTRACT_SENT", 10);
    for (GatewayEventDTO gatewayEventDTO : lws_extract_sent) {
      csvFilename = gatewayEventDTO.getMetadata().get("lwsFilename");
    }
    if (inCsv.equals("is")) {
      String csv = sftpUtils.getCsv("lws/", csvFilename);
      if (roleId.length() == 4) {
        assertThat(csv).containsPattern(
            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
                + "\"" + name + " Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"" + number + "\",\"N/A\",\"" + roleId
                + "\",\"Contact Lone Worker on mobile: " + number
                + "\",\"instruction\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"N/A\",\"N/A\",\"CREATE\"");
      } else if (roleId.length() == 7) {
        assertThat(csv).containsPattern(
            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
                + "\"" + name + " Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"" + number + "\",\"N/A\",\"" + roleId
                + "\",\"Contact Lone Worker on mobile: " + number
                + "\",\"N/A\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"" + roleId
                .substring(0, 4) + "\",\"N/A\",\"CREATE\"");
      } else {
        assertThat(csv).containsPattern(
            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
                + "\"" + name + " Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"" + number + "\",\"N/A\",\"" + roleId
                + "\",\"Contact Lone Worker on mobile: " + number
                + "\",\"N/A\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"" + roleId
                .substring(0, 4) + "\",\""+roleId.substring(0,7)+"\",\"CREATE\"");
      }
    } else {
      assertThat(csvFilename).isBlank();
    }
  }

  @Then("the employee {string} in the Logisitics CSV with {string} and phone number {string} as an update with name {string}")
  public void the_employee_in_the_Logisitics_CSV_with_and_phone_number_as_an_update(String inCsv, String roleId,
      String phoneNumber, String name)
      throws Exception {
    String csvFilename = null;
    List<GatewayEventDTO> logistics_extract_sent = gatewayEventMonitor
        .getEventsForEventType("LOGISTICS_EXTRACT_SENT", 10);
    for (GatewayEventDTO gatewayEventDTO : logistics_extract_sent) {
      csvFilename = gatewayEventDTO.getMetadata().get("logisticsFilename");
    }

    if (!phoneNumber.equals(""))
      phoneNumber = "\"" + phoneNumber + "\"";
    if (inCsv.contains("is not")) {
      assertThat(csvFilename).isBlank();
    } else {
      String csv = sftpUtils.getCsv("logistics/", csvFilename);
      assertThat(csv).containsPattern(
          "\"" + name + "\",\"Buyo\",,\"123\",\"Fake Street\",\"Faketon\",\"Fakeside\",\"FA43 1AB\",\"\",\"f.b@email.com\",\"Fransico.Buyo[0-9]{2}@domain\",\"0987654321\"," + phoneNumber + ",,\"" + roleId
              + "\",,\"ACTIVE\"");
    }
  }

  @Then("Check the employee {string} is sent to RCA")
  public void checkTheEmployeeSendToRCA(String employeeId) throws IOException {
    String csvFilename = null;
    List<GatewayEventDTO> logistics_extract_sent = gatewayEventMonitor
        .getEventsForEventType("RCA_EXTRACT_COMPLETE", 10);
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
    assertThat(fileContent).contains("Employee ID number").contains(employeeId);

  }
}
