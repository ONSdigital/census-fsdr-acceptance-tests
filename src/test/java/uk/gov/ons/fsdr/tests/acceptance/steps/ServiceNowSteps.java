package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.SnowMockUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@PropertySource("classpath:application.properties")
public class ServiceNowSteps {

  @Autowired
  private SnowMockUtils snowMockUtils;

  @Value("${service.rabbit.url}")
  private String rabbitLocation;

  @Value("${service.rabbit.username}")
  private String rabbitUsername;

  @Value("${service.rabbit.password}")
  private String rabbitPassword;

  @Then("the employee is correctly created in ServiceNow with {string}")
  public void the_employee_is_correctly_create_in_ServiceNow_with(String roleId) {
    String[] records = snowMockUtils.getRecords();
    String create = records[0];
    String expectedMessageRootNode =
        "\"location\":\"London\",\"first_name\":\"Fransico\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,\"u_lm_first_name_2\":null,\"u_lm_last_name_2\":null,\"user_name\":\""
            + roleId
            + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"[0-9-]{10}\",\"u_contract_end_date\":\"[0-9-]{10}\",\"u_employment_status\":\"ACTIVE\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"Fransico.Buyo[0-9]{2}@domain\",\"u_asset_number\":null,\"u_ons_device_number\":null,\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true";

    assertThat(create).containsPattern(expectedMessageRootNode);
  }

  @Then("the employee is correctly moved in ServiceNow with {string}")
  public void the_employee_is_correctly_moved_in_ServiceNow_with(String roleId) {
    String[] records = snowMockUtils.getRecords();
    String update = records[records.length - 1];
    String expectedMessageRootNode = "";
    expectedMessageRootNode = "\"location\":\"London\",\"first_name\":\"Fransico"
        + "\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,\"u_lm_first_name_2\":null,\"u_lm_last_name_2\":null,\"user_name\":\""
        + roleId
        + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"2020-01-01\",\"u_contract_end_date\":\""+ LocalDate.now().plusDays(5)+"\",\"u_employment_status\":\"ACTIVE\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"Fransico.Buyo[0-9]{2}@domain\",\"u_asset_number\":\"[0-9a-z-]{36}\",\"u_ons_device_number\":\""
        + "0123456789\",\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true";

    Assertions.assertThat(update).containsPattern(expectedMessageRootNode);
  }

  @Then("the employee is correctly updated in ServiceNow with {string} and name {string} and number {string}")
  public void the_employee_is_correctly_updated_in_ServiceNow_with(String roleId, String name, String phoneNumber) {
    String[] records = snowMockUtils.getRecords();
    String update = records[records.length - 1];
    String expectedMessageRootNode = "";
    String assetId;
    if(phoneNumber.equals("")) {
      phoneNumber = "null";
      assetId = "null";
    }
    else {
      phoneNumber = "\""+phoneNumber+"\"";
      assetId = "\"[0-9a-z-]{36}\"";
    }
    expectedMessageRootNode = "\"location\":\"London\",\"first_name\":\"" + name
        + "\",\"last_name\":\"Buyo\",\"u_preferred_name\":null,\"u_badge_number\":null,\"u_lm_first_name_2\":null,\"u_lm_last_name_2\":null,\"user_name\":\""
        + roleId
        + "\",\"u_job_role_2\":null,\"u_contract_start_date\":\"2020-01-01\",\"u_contract_end_date\":\""+ LocalDate.now().plusDays(5)+"\",\"u_employment_status\":\"ACTIVE\",\"zip\":\"FA43 1AB\",\"u_ons_id\":\"Fransico.Buyo[0-9]{2}@domain\",\"u_asset_number\":"+assetId+",\"u_ons_device_number\":"
        + phoneNumber + ",\"home_phone\":null,\"mobile_phone\":\"0987654321\",\"active\":true";

    assertThat(update).containsPattern(expectedMessageRootNode);
  }

  @Then("the employee is correctly suspended in ServiceNow with {string}")
  public void theEmployeeIsCorrectlySuspendedInSNow(String roleId) {
    String[] records = snowMockUtils.getRecords();
    String suspended = records[records.length-1];

    assertThat(suspended).containsPattern("\"active\":false");
    assertThat(suspended).containsPattern("\"u_employment_status\":\"Left\"");
    assertThat(suspended).containsPattern(".*\"user_name\":\""+roleId+"[0-9]{6}\".*");

  }

  @Then("the employee is not created in ServiceNow")
  public void the_employee_is_not_created_in_ServiceNow() {
    String[] records = snowMockUtils.getRecords();
    assertThat(records).isEmpty();
  }
}
