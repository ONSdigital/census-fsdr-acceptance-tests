package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseJob;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseWorker;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoPeopleFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.AREA_MANAGER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.COORDINATOR_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.FIELD_OFFICER_ROLE_ID_LENGTH;

@Slf4j
@PropertySource("classpath:application.properties")
public class AdeccoSteps {

  public static AdeccoResponse adeccoResponse = new AdeccoResponse();
  public static List<AdeccoResponse> adeccoResponseList = new ArrayList<>();
  public static List<AdeccoResponse> adeccoResponseManagers = new ArrayList<>();
  public static Optional<AdeccoResponse> adeccoResponseLeaver = Optional.empty();
  public Set<String> sentManagerIds = new HashSet<>();

  @Given("An employee exists in {string} with an id of {string}")
  public void we_recieve_an_employee_with_an_id_of(String source, String id) {

    adeccoResponse = AdeccoPeopleFactory.buildFransicoBuyo(id);
    adeccoResponse.setContractStartDate("2020-01-01");
  }

  @Given("an assignment status of {string}")
  public void an_assignment_status_of(String assignmentStatus) {
    adeccoResponse.setStatus(assignmentStatus);
  }

  @Given("a closing report status of {string}")
  public void a_closing_report_status_of(String crStatus) {
    adeccoResponse.setCrStatus(crStatus);
  }

  @Given("a role id of {string}")
  public void a_role_id_of(String roleId) {
    adeccoResponse.getResponseJob().setRoleId(roleId);
    if(roleId.length() == FIELD_OFFICER_ROLE_ID_LENGTH) {
      adeccoResponse.getResponseJob().setLineManagerFirstName("Bob");
      adeccoResponse.getResponseJob().setLineManagerSurName("Jones");
    } else if (roleId.length() == COORDINATOR_ROLE_ID_LENGTH) {
      adeccoResponse.getResponseJob().setLineManagerFirstName("Dave");
      adeccoResponse.getResponseJob().setLineManagerSurName("Davis");
    }
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

  @Given("we receive a new active job role from adecco for employee {string} with new role_id {string}")
  public void we_receive_an_update_from_adecco_for_employee_with_new_role_id(String id, String roleId) {
    AdeccoResponse moverResponse = new AdeccoResponse();
    moverResponse.setAdeccoResponseWorker(new AdeccoResponseWorker(id));
    moverResponse.setResponseContact(adeccoResponse.getResponseContact());
    moverResponse.setResponseJob(new AdeccoResponseJob(null,null,null,null,roleId));
    moverResponse.setStatus("ASSIGNED");
    moverResponse.setCrStatus("ACTIVE");
    moverResponse.setOperationalEndDate(adeccoResponse.getOperationalEndDate());
    moverResponse.setContractStartDate(adeccoResponse.getContractStartDate());
    moverResponse.setContractEndDate(adeccoResponse.getContractEndDate());
    if(roleId.length() == FIELD_OFFICER_ROLE_ID_LENGTH) {
      moverResponse.getResponseJob().setLineManagerFirstName("Bob");
      moverResponse.getResponseJob().setLineManagerSurName("Jones");
    } else if (roleId.length() == COORDINATOR_ROLE_ID_LENGTH) {
      moverResponse.getResponseJob().setLineManagerFirstName("Dave");
      moverResponse.getResponseJob().setLineManagerSurName("Davis");
    }
    adeccoResponseList.add(moverResponse);
  }

  @Given("their old job role gets cancelled")
  public void their_old_job_role_gets_cancelled() {
    adeccoResponseList.get(0).setStatus("ASSIGNMENT_CANCELLED");
  }

  @Given("we receive an update from adecco for employee {string} with new first name {string}")
  public void we_receive_an_update_from_adecco_for_employee_with_new_first_name(String id, String newFName) {
    adeccoResponse.getResponseContact().setFirstName(newFName);
  }

  @Given("a contract start date {int} days in the future")
  public void a_contract_start_date_more_than_days_away(int days) {
    adeccoResponse.setContractStartDate(LocalDate.now().plusDays(days).toString());
  }

  @Given("the managers of {string} exist")
  public void theManagersOfExist(String roleId) {
    Random random = new Random();
    if (roleId.length() == 13) {
      buildAreaManagerTypeManager(roleId, random.nextInt(1000));
      buildCoordinatorTypeManager(roleId, random.nextInt(1000));
    }
    if (roleId.length() == 10) {
      buildAreaManagerTypeManager(roleId, random.nextInt(1000));
    }


  }

  private void buildCoordinatorTypeManager(String roleId, int id) {
    String managerRoleId = roleId.substring(0, COORDINATOR_ROLE_ID_LENGTH);
    if(!sentManagerIds.contains(managerRoleId)) {
      AdeccoResponse managerAdeccoResponse = AdeccoPeopleFactory.buildFransicoBuyo(String.valueOf(id));
      managerAdeccoResponse.setContractStartDate("2020-01-01");
      managerAdeccoResponse.setStatus("ASSIGNED");
      managerAdeccoResponse.setCrStatus("ACTIVE");
      managerAdeccoResponse.getResponseContact().setFirstName("Dave");
      managerAdeccoResponse.getResponseContact().setLastName("Davis");
      managerAdeccoResponse.getResponseContact().setTelephoneNo1("0112233445");
      managerAdeccoResponse.getResponseJob().setRoleId(managerRoleId);
      sentManagerIds.add(managerRoleId);

      adeccoResponseManagers.add(managerAdeccoResponse);
    }
  }

  private void buildAreaManagerTypeManager(String roleId, int id) {
    String managerRoleId = roleId.substring(0, AREA_MANAGER_ROLE_ID_LENGTH);
    if(!sentManagerIds.contains(managerRoleId)) {
      AdeccoResponse managerAdeccoResponse = AdeccoPeopleFactory.buildFransicoBuyo(String.valueOf(id));
      managerAdeccoResponse.setContractStartDate("2020-01-01");
      managerAdeccoResponse.setStatus("ASSIGNED");
      managerAdeccoResponse.setCrStatus("ACTIVE");
      managerAdeccoResponse.getResponseContact().setFirstName("Bob");
      managerAdeccoResponse.getResponseContact().setLastName("Jones");
      managerAdeccoResponse.getResponseContact().setTelephoneNo1("0112233445");
      managerAdeccoResponse.getResponseJob().setRoleId(managerRoleId);
      sentManagerIds.add(managerRoleId);
      adeccoResponseManagers.add(managerAdeccoResponse);
    }
  }

  @Given("the managers of {string} exist before moving to {string}")
  public void theManagersOfExistBeforeMovingTo(String roleId, String newRoleId) {
    theManagersOfExist(roleId);
    adeccoResponseLeaver = adeccoResponseManagers.stream().filter(adecco -> adecco.getResponseJob().
            getRoleId().equals(newRoleId)).findFirst();
  }

  @Given("the managers of {string} exist before moving from {string}")
  public void theManagersOfExistBeforeMovingFrom(String roleId, String newRoleId) {
    if (roleId.startsWith(newRoleId)) {
      return;
    }
      theManagersOfExist(newRoleId);
  }

  @Given("the previous {string} gets cancelled")
  public void thePreviousGetsCancelled(String arg0) {
    adeccoResponseLeaver.ifPresent(response -> response.setStatus("ASSIGNMENT_CANCELLED"));
  }
}
