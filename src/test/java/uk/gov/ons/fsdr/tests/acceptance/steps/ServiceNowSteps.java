package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.fsdr.tests.acceptance.utils.ServiceNowMockUtils;

import java.time.LocalDate;
import java.util.Collection;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.COORDINATOR_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.FIELD_OFFICER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;
import static uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils.getLastRecord;

@Slf4j
@PropertySource("classpath:application.properties")
public class ServiceNowSteps {

  @Autowired
  private ServiceNowMockUtils serviceNowMockUtils;

  @Value("${service.rabbit.url}")
  private String rabbitLocation;

  @Value("${service.rabbit.username}")
  private String rabbitUsername;

  @Value("${service.rabbit.password}")
  private String rabbitPassword;

  @Then("the employee {string} is correctly created in ServiceNow with {string}")
  public void the_employee_is_correctly_create_in_ServiceNow_with(String employeeId, String roleId) {
    String[] records = serviceNowMockUtils.getRecords();
    String create = records[records.length-1];

    String lmName = "\"u_lm_first_name_2\":null,\"u_lm_last_name_2\":null";

    if(roleId.length() == FIELD_OFFICER_ROLE_ID_LENGTH) {
      lmName = "\"u_lm_first_name_2\":\"Bob\",\"u_lm_last_name_2\":\"Jones\"";
    }
    if(roleId.length() == COORDINATOR_ROLE_ID_LENGTH) {
      lmName = "\"u_lm_first_name_2\":\"Dave\",\"u_lm_last_name_2\":\"Davis\"";
    }
    String expectedMessageRootNode =
        "\"location\":\"London\",\"first_name\":\"Fransico\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,"+lmName+",\"employee_number\":\""
            + roleId
            + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"[0-9-]{10}\",\"u_contract_end_date\":\"[0-9-]{10}\",\"u_employment_status\":\"ACTIVE\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"Fransico.Buyo[0-9]{2}@domain\",\"u_ons_device_number\":null,\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true,\"user_name\":\""+employeeId+"\"";

    assertThat(create).containsPattern(expectedMessageRootNode);
  }

  @Then("the employee {string} is correctly moved in ServiceNow with {string}")
  public void the_employee_is_correctly_moved_in_ServiceNow_with(String employeeId, String roleId) {
    Collection<GatewayEventDTO> events = gatewayEventMonitor.grabEventsTriggered("SENDING_SERVICE_NOW_ACTION_RESPONSE", 10, 5000l);
    String[] records = serviceNowMockUtils.getRecords();
    String update = getLastRecord(records, roleId);
    String expectedMessageRootNode = "";
    String lineManager = "\"u_lm_first_name_2\":null,\"u_lm_last_name_2\":null";
    if( roleId.length() == FIELD_OFFICER_ROLE_ID_LENGTH) {
      lineManager = "\"u_lm_first_name_2\":\"Bob\",\"u_lm_last_name_2\":\"Jones\"";
    } else if (roleId.length() == COORDINATOR_ROLE_ID_LENGTH) {
      lineManager = "\"u_lm_first_name_2\":\"Dave\",\"u_lm_last_name_2\":\"Davis\"";
    }
    expectedMessageRootNode = "\"location\":\"London\",\"first_name\":\"Fransico"
        + "\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,"+lineManager+",\"employee_number\":\""
        + roleId
        + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"2020-01-01\",\"u_contract_end_date\":\""+ LocalDate.now().plusDays(5)+"\",\"u_employment_status\":\"ACTIVE\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"Fransico.Buyo[0-9]{2}@domain\",\"u_ons_device_number\":\""
        + "07234567890\",\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true,\"user_name\":\""+employeeId+"\"";

    Assertions.assertThat(update).containsPattern(expectedMessageRootNode);
  }

  @Then("the employee {string} is correctly updated in ServiceNow with {string} and name {string} and number {string}")
  public void the_employee_is_correctly_updated_in_ServiceNow_with(String id, String roleId, String name, String phoneNumber) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SERVICE_NOW_UPDATE_SENT", 10000L));
    String[] records = serviceNowMockUtils.getRecords();
    String update = getLastRecord(records,roleId);
    String expectedMessageRootNode = "";
    if(phoneNumber.equals("")) {
      phoneNumber = "null";
    }
    else {
      phoneNumber = "\""+phoneNumber+"\"";
    }
    String lineManager = "\"u_lm_first_name_2\":null,\"u_lm_last_name_2\":null";
    if( roleId.length() == FIELD_OFFICER_ROLE_ID_LENGTH) {
      lineManager = "\"u_lm_first_name_2\":\"Bob\",\"u_lm_last_name_2\":\"Jones\"";
    } else if (roleId.length() == COORDINATOR_ROLE_ID_LENGTH) {
      lineManager = "\"u_lm_first_name_2\":\"Dave\",\"u_lm_last_name_2\":\"Davis\"";
    }
    expectedMessageRootNode = "\"location\":\"London\",\"first_name\":\"" + name
        + "\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,"+lineManager+",\"employee_number\":\""
        + roleId
        + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"2020-01-01\",\"u_contract_end_date\":\""+ LocalDate.now().plusDays(5)+"\",\"u_employment_status\":\"ACTIVE\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"Fransico.Buyo[0-9]{2}@domain\",\"u_ons_device_number\":"
        + phoneNumber + ",\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true,\"user_name\":\""+id+"\"";
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_SERVICE_NOW_ACTION_RESPONSE", 10000L));
    assertThat(update).containsPattern(expectedMessageRootNode);
  }

  @Then("the employee {string} is correctly suspended in ServiceNow with {string}")
  public void theEmployeeIsCorrectlySuspendedInServiceNow(String id, String roleId) {
    gatewayEventMonitor.grabEventsTriggered("SENDING_SERVICE_NOW_ACTION_RESPONSE", 5, 3000L);
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_SERVICE_NOW_ACTION_RESPONSE", 3000L));

    String[] records = serviceNowMockUtils.getRecords();
    String suspended = getLastRecord(records, roleId);

    assertThat(suspended).containsPattern("\"active\":false");
    assertThat(suspended).containsPattern("\"u_employment_status\":\"Left\"");
    assertThat(suspended).containsPattern(".*\"employee_number\":\""+roleId+"\".*");
    assertThat(suspended).containsPattern(".*\"user_name\":\""+id+"\".*");
  }

  @Then("the employee {string} is not created in ServiceNow")
  public void the_employee_is_not_created_in_ServiceNow(String id) {
    gatewayEventMonitor.grabEventsTriggered("SENDING_SERVICE_NOW_ACTION_RESPONSE", 5, 3000L);
    assertFalse(gatewayEventMonitor.hasEventTriggered(id, "SENDING_SERVICE_NOW_ACTION_RESPONSE", 2000L));
    String[] records = serviceNowMockUtils.getRecords();
    assertThat(records).isEmpty();
  }
}
