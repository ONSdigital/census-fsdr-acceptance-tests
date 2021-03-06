package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseJob;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseJobRoleCode;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseReportsTo;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseWorker;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoPeopleFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.AREA_MANAGER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.COORDINATOR_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.FIELD_OFFICER_ROLE_ID_LENGTH;

@Slf4j
@PropertySource("classpath:application.properties")
public class AdeccoIngestSteps {

  public static AdeccoResponse adeccoResponse = new AdeccoResponse();
  public static List<AdeccoResponse> adeccoResponseList = new ArrayList<>();
  public static List<AdeccoResponse> adeccoResponseManagers = new ArrayList<>();
  public static Optional<AdeccoResponse> adeccoResponseLeaver = Optional.empty();

  @Given("An employee exists in {string} with an id of {string}")
  public void we_recieve_an_employee_with_an_id_of(String source, String id) {

    adeccoResponse = AdeccoPeopleFactory.buildFransicoBuyo(id);
    adeccoResponse.setContractStartDate("2020-01-01");
  }

  @Given("an assignment status of {string}")
  public void an_assignment_status_of(String assignmentStatus) {
    adeccoResponse.setStatus(assignmentStatus);
  }

  @When("the employee assignment status changes to {string}")
  public void theEmployeeAssignmentStatusChangesTo(String assignmentStatus) {
    adeccoResponse.setStatus(assignmentStatus);
  }

  @Given("a closing report status of {string}")
  public void a_closing_report_status_of(String crStatus) {
    adeccoResponse.setCrStatus(crStatus);
  }

  @Given("a closing report id of {string}")
  public void a_closing_report_id_of(String crId) {
    adeccoResponse.setClosingReportId(crId);
  }

  @Given("a role id of {string}")
  public void a_role_id_of(String roleId) {
    AdeccoResponseJobRoleCode adeccoResponseJobRoleCode = new AdeccoResponseJobRoleCode();
    adeccoResponseJobRoleCode.setRoleId(roleId);
    AdeccoResponseReportsTo manager = new AdeccoResponseReportsTo();
    adeccoResponse.setAdeccoResponseJobRoleCode(adeccoResponseJobRoleCode);
    manager.setLineManagerFirstName("Bob");
    manager.setLineManagerSurName("Jones");
    adeccoResponse.setReportsTo(manager);
  }

  @Given("an operational end date of {string}")
  public void an_operational_end_date_of(String date) {
    adeccoResponse.setOperationalEndDate(date);
  }

  @Given("a contract start date of {string}")
  public void a_contract_start_date_of(String date) {
    adeccoResponse.setContractStartDate(date);
  }

  @When("we receive a job role update from adecco for employee  {string}")
  public void we_receive_a_job_role_update_from_adecco_for_employee(String id) {
    adeccoResponse.setContractStartDate(LocalDate.now().toString());
  }

  @Given("we receive a new active job role from adecco for employee {string} with new role_id {string} and status {string}")
  public void we_receive_an_update_from_adecco_for_employee_with_new_role_id_and_status(String id, String roleId, String status) {
    createSecondJobRole(id, roleId, status);
  }

  @Given("we receive a new active job role from adecco for employee {string} with new role_id {string}")
  public void we_receive_an_update_from_adecco_for_employee_with_new_role_id(String id, String roleId){
    createSecondJobRole(id, roleId, "ASSIGNED");
  }

  private void createSecondJobRole(String id, String roleId, String status) {
    AdeccoResponse moverResponse = new AdeccoResponse();
    moverResponse.setAdeccoResponseWorker(new AdeccoResponseWorker(id));
    moverResponse.setResponseContact(adeccoResponse.getResponseContact());
    AdeccoResponseJobRoleCode adeccoResponseJobRoleCode = new AdeccoResponseJobRoleCode();
    adeccoResponseJobRoleCode.setRoleId(roleId);
    moverResponse.setAdeccoResponseJobRoleCode(adeccoResponseJobRoleCode);
    moverResponse.setResponseJob(new AdeccoResponseJob(null, null, "parentJobRole"));
    moverResponse.setStatus(status);
    moverResponse.setCrStatus("ACTIVE");
    moverResponse.setOperationalEndDate(adeccoResponse.getOperationalEndDate());
    moverResponse.setContractStartDate(adeccoResponse.getContractStartDate());
    moverResponse.setContractEndDate(adeccoResponse.getContractEndDate());
    moverResponse.setClosingReportId(UUID.randomUUID().toString());
    AdeccoResponseReportsTo manager = new AdeccoResponseReportsTo();
    manager.setLineManagerFirstName("Bob");
    manager.setLineManagerSurName("Jones");
    moverResponse.setReportsTo(manager);
    adeccoResponseList.add(moverResponse);
  }

  @Given("we receive an update from adecco for employee {string} with new first name {string}")
  public void we_receive_an_update_from_adecco_for_employee_with_new_first_name(String id, String newFName) throws InterruptedException {
    adeccoResponse.getResponseContact().setFirstName(newFName);
  }

  @Given("a contract start date {int} days in the future")
  public void a_contract_start_date_more_than_days_away(int days) {
    adeccoResponse.setContractStartDate(LocalDate.now().plusDays(days).toString());
  }

  @Given("the previous {string} gets cancelled")
  public void thePreviousGetsCancelled(String arg0) {
    adeccoResponseLeaver.ifPresent(response -> response.setStatus("ASSIGNMENT_CANCELLED"));
  }

  @And("we receive an update from adecco for employee {string} with multiple closing reports for role id {string} updating name to {string}")
  public void weReceiveAnUpdateFromAdeccoForEmployeeWithMultipleClosingReportsForRoleId(String employeeId, String roleId, String newFName) {
    adeccoResponse.setCrStatus("INACTIVE");
    adeccoResponse.setStatus("ASSIGNMENT CANCELLED");

    AdeccoResponse newClosingReport = AdeccoPeopleFactory.buildFransicoBuyo(employeeId);

    newClosingReport.setContractStartDate("2020-02-01");
    newClosingReport.setStatus("ASSIGNED");
    newClosingReport.setCrStatus("ACTIVE");
    newClosingReport.getResponseContact().setFirstName(newFName);

    AdeccoResponseJobRoleCode adeccoResponseJobRoleCode = new AdeccoResponseJobRoleCode();
    adeccoResponseJobRoleCode.setRoleId(roleId);
    newClosingReport.setAdeccoResponseJobRoleCode(adeccoResponseJobRoleCode);

    adeccoResponseList.add(newClosingReport);
  }

}
