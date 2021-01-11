package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.ServiceNowMockUtils;

import java.time.LocalDate;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;
import static uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils.getLastRecord;

@Slf4j
@PropertySource("classpath:application.properties")
public class ServiceNowSteps {

  @Autowired
  private ServiceNowMockUtils serviceNowMockUtils;

  @Then("the employee {string} with closing report id {string} is correctly created in ServiceNow with {string}")
  public void the_employee_is_correctly_create_in_ServiceNow_with(String employeeId, String crId, String roleId) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(employeeId+crId, "SERVICE_NOW_CREATE_SENT", 10000L));

    String[] records = serviceNowMockUtils.getRecords();
    String create = records[records.length-1];

    String expectedMessageRootNode =
        "\"location\":\"London\",\"first_name\":\"Fransico\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,\"u_line_manager\":\"Bob Jones\",\"employee_number\":\""
            + roleId
            + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"[0-9-]{10}\",\"u_contract_end_date\":\"[0-9-]{10}\",\"u_employment_status\":\"ASSIGNED\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"fransico.buyo[0-9]{2}@domain\",\"u_ons_device_number\":null,\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true,\"user_name\":\"fransico.buyo[0-9]{2}@domain\"";

    assertThat(create).containsPattern(expectedMessageRootNode);
  }

  @Then("the employee {string} with closing report id {string} is correctly updated in ServiceNow with {string} and name {string} and number {string}")
  public void the_employee_is_correctly_updated_in_ServiceNow_with(String id, String crId, String roleId, String name, String phoneNumber) {
    checkSentEmployee(id+crId, roleId, name, phoneNumber, null);
  }

  @Then("the employee {string} with closing report id {string} is correctly updated in ServiceNow with {string} and name {string} and number {string} and contract start date {string}")
  public void the_employee_is_correctly_updated_in_ServiceNow_with(String id, String crId, String roleId, String name, String phoneNumber, String contractStartDate) {
    checkSentEmployee(id+crId, roleId, name, phoneNumber, contractStartDate);
  }

  public void checkSentEmployee(String id, String roleId, String name, String phoneNumber, String contractStartDate) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SERVICE_NOW_UPDATE_SENT", 10000L));
    String[] records = serviceNowMockUtils.getRecords();
    String update = getLastRecord(records,roleId);
    String expectedMessageRootNode = "";
    if(phoneNumber.equals("")) {
      phoneNumber = "null";
    }
    else {
      phoneNumber = "\"\\"+phoneNumber+"\"";
    }
    if(contractStartDate == null) {
      contractStartDate = "2020-01-01";
    }
    expectedMessageRootNode = "\"location\":\"London\",\"first_name\":\"" + name
        + "\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,\"u_line_manager\":\"Bob Jones\",\"employee_number\":\""
        + roleId
        + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"" + contractStartDate + "\",\"u_contract_end_date\":\"" + LocalDate.now().plusDays(5)+"\",\"u_employment_status\":\"ASSIGNED\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"fransico.buyo[0-9]{2}@domain\",\"u_ons_device_number\":"
        + phoneNumber + ",\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true,\"user_name\":\"fransico.buyo[0-9]{2}@domain\"";
    assertThat(update).containsPattern(expectedMessageRootNode);
  }

  @Then("the employee {string} with closing report id {string} is correctly suspended in ServiceNow with {string}")
  public void theEmployeeIsCorrectlySuspendedInServiceNow(String id, String crId, String roleId) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+crId, "SENDING_SERVICE_NOW_ACTION_RESPONSE", 10000L));

    String[] records = serviceNowMockUtils.getRecords();
    String suspended = getLastRecord(records, roleId);

    assertThat(suspended).containsPattern("\"active\":false");
    assertThat(suspended).containsPattern("\"u_employment_status\":\"Left\"");
    assertThat(suspended).containsPattern(".*\"employee_number\":\""+roleId+"\".*");
    assertThat(suspended).containsPattern(".*\"user_name\":\"fransico.buyo[0-9]{2}@domain\".*");
  }

  @Then("the employee {string} with closing report id {string} is not created in ServiceNow")
  public void the_employee_is_not_created_in_ServiceNow(String id, String crId) {
    assertTrue(gatewayEventMonitor.hasEventTriggered("<N/A>", "FSDR_PROCESSES_ACTIONS_COMPLETE", 10000L));
    String[] records = serviceNowMockUtils.getRecords();
    assertThat(records).isEmpty();
  }

  @Then("the employee {string} with closing report id {string} is not updated in ServiceNow")
  public void the_employee_is_not_updated_in_ServiceNow(String id, String crId) {
    assertFalse(gatewayEventMonitor.hasEventTriggered(id+crId, "SERVICE_NOW_UPDATE_SENT", 5000L));
  }
}
