package uk.gov.ons.fsdr.tests.acceptance.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;

import java.io.IOException;
import java.util.Collection;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.AREA_MANAGER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.COORDINATOR_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.FIELD_OFFICER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;
import static uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils.getLastRecord;

@Slf4j
@PropertySource("classpath:application.properties")
public class GSuiteSteps {

  @Autowired
  private GsuiteMockUtils gsuiteMockUtils;

  @Value("${service.rabbit.url}")
  private String rabbitLocation;

  @Value("${service.rabbit.username}")
  private String rabbitUsername;

  @Value("${service.rabbit.password}")
  private String rabbitPassword;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Then("the employee {string} is correctly created in gsuite with roleId {string} and orgUnit {string}")
  public void the_employee_is_correctly_updated_in_gsuite(String id, String roleId, String orgUnit) {
      String[] records = gsuiteMockUtils.getRecords();
      int i = 0;
      for (String record : records) {
        boolean contains = record.contains("{\"RoleID\":\"" + roleId + "\"}");
        if (contains) break;
        i++;
      }

    assertThat(records[i]).contains(
        "\"changePasswordAtNextLogin\":false,\"customSchemas\":{\"Employee_Information\":{\"RoleID\":\"" + roleId
            + "\"}},\"externalIds\":[{\"type\":\"organization\",\"value\":\""+id+"\"}],\"hashFunction\":\"SHA-1\",\"includeInGlobalAddressList\":true,\"ipWhitelisted\":false,\"name\":{\"familyName\":\"Buyo\",\"givenName\":\"Fransico\"},\"orgUnitPath\":\"/CFODS/Zero Access"
            + "\",\"organizations\":[{\"department\":\"Zero Access\",\"primary\":true}]");
    assertThat(records[i]).containsPattern(
        ",\"password\":\"[0-9a-zA-Z]{40}\",\"primaryEmail\":\"Fransico.Buyo[0-9]{2}@domain\",\"suspended\":false");
  }

  @Then("the HQ employee {string} is correctly created in gsuite with orgUnit {string}")
  public void the_hq_employee_is_correctly_created_in_gsuite(String id, String orgUnit) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_GSUITE_ACTION_RESPONSE", 5000L));
    String[] records = gsuiteMockUtils.getRecords();

    assertThat(records[0]).contains("\"changePasswordAtNextLogin\":false,"
        + "\"hashFunction\":\"SHA-1\","
        + "\"includeInGlobalAddressList\":true,"
        + "\"ipWhitelisted\":false,"
        + "\"name\":{\"familyName\":\"Wardle\",\"givenName\":\"Kieran\"},"
        + "\"orgUnitPath\":\"/CFODS/" + orgUnit + "\","
        + "\"organizations\":[{\"department\":\"" + orgUnit + "\",\"primary\":true}]");
    assertThat(records[0]).containsPattern(",\"password\":\"[0-9a-zA-Z]{40}\","
        + "\"primaryEmail\":\"Kieran.Wardle[0-9]{2}@domain\","
        + "\"recoveryEmail\":\""+id+"@test\","
        + "\"suspended\":false");
  }

  @Then("the employee is correctly updated in gsuite with name {string}")
  public void the_employee_is_correctly_updated_in_gsuite(String name) throws IOException {
    String[] records = gsuiteMockUtils.getRecords();
    String update = records[records.length - 1];

    JsonNode expectedMessageRootNode = objectMapper
        .readTree("{\"name\":{\"familyName\":\"Buyo\",\"givenName\":\"" + name + "\"}}");
    JsonNode actualMessageRootNode = objectMapper.readTree(update);

    assertEquals(expectedMessageRootNode, actualMessageRootNode);
  }

  @Then("the employee is correctly moved in gsuite with roleId {string} to {string}")
  public void the_employee_is_correctly_moved_in_gsuite_with_roleId(String roleId, String orgUnit) throws IOException {
    Collection<GatewayEventDTO> events = gatewayEventMonitor.grabEventsTriggered("SENDING_GSUITE_ACTION_RESPONSE", 10, 5000L);

    String[] records = gsuiteMockUtils.getRecords();
    String update = getLastRecord(records, roleId);

    JsonNode expectedMessageRootNode = objectMapper
        .readTree("{\"customSchemas\":{\"Employee_Information\":{\"RoleID\":\""+roleId+"\"}},\"name\":{\"familyName\":\"Buyo\",\"givenName\":\"Fransico\"},\"orgUnitPath\":\"/CFODS/"+orgUnit+"\",\"organizations\":[{\"department\":\""+orgUnit+"\",\"primary\":true}]}");
    JsonNode actualMessageRootNode = objectMapper.readTree(update);

    assertEquals(expectedMessageRootNode, actualMessageRootNode);
  }

  @Then("the employee {string} is correctly suspended in gsuite")
  public void theEmployeeIsCorrectlySuspendedInGsuite(String id) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "GSUITE_USER_SUSPEND_COMPLETE", 5000L));
    String[] records = gsuiteMockUtils.getRecords();
    String suspended1 = records[records.length - 2];
    String suspended2 = records[records.length - 1];
    assertEquals("{\"changePasswordAtNextLogin\":true,\"suspended\":true}", suspended1);
    assertEquals("{\"changePasswordAtNextLogin\":false}", suspended2);
  }

  @Then("the employee {string} is not created in gsuite")
  public void the_employee_is_not_created_in_gsuite(String id) {
    String[] groups = gsuiteMockUtils.getGroups(id);
    String[] records = gsuiteMockUtils.getRecords();
    assertThat(groups).isNullOrEmpty();
    assertThat(records).isEmpty();
  }

  @Then("the employee {string} is not updated in gsuite")
  public void the_employee_is_not_updated_in_gsuite(String id) {
    Collection<GatewayEventDTO> events = gatewayEventMonitor.grabEventsTriggered("SENDING_GSUITE_ACTION_RESPONSE", 10, 3000l);
    int expextedCount = 0;
    if (id.length() == FIELD_OFFICER_ROLE_ID_LENGTH) expextedCount = 3;
    else if (id.length() == COORDINATOR_ROLE_ID_LENGTH) expextedCount = 2;
    else if (id.length() == AREA_MANAGER_ROLE_ID_LENGTH) expextedCount = 1;
    String[] records = gsuiteMockUtils.getRecords();
    assertEquals(expextedCount, events.size());
    assertEquals(expextedCount, records.length);
  }

  @Then("the employee {string} is no longer in the following groups {string}")
  public void the_employee_is_no_longer_in_the_following_groups(String id, String groups) {
    if (groups.equals("N/A")) assertTrue(true);
    else {
      String[] oldGroupList = groups.split(",");
      String[] currentMemberGroups = gsuiteMockUtils.getGroups(id);
      for (String group : oldGroupList) {
        assertThat(currentMemberGroups).doesNotContain(group + "-group@domain");
      }
    }
  }

  @Then("the employee {string} is now in the current groups {string}")
  public void the_employee_is_now_in_the_current_groups(String id, String groups) {
    String[] newGroupList = groups.split(",");
    String[] currentMemberGroups = gsuiteMockUtils.getGroups(id);
    for(String group : newGroupList) {
      assertThat(currentMemberGroups).contains(group+"@domain");
    }
    assertEquals(newGroupList.length, currentMemberGroups.length);
  }

  @Given("the roleId for {string} is set to {string} in gsuite")
  public void the_employees_roleId_is_set_to_in_gsuite(String id, String roleId) {
    gsuiteMockUtils.addRoleId(id, roleId);
  }

  @Then("the hq employee {string} is correctly updated in gsuite")
  public void the_hq_employee_is_correctly_updated_in_gsuite(String id) throws IOException {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "GSUITE_USER_UPDATE_COMPLETE", 5000L));

    String[] records = gsuiteMockUtils.getRecords();
    String update = records[records.length - 1];

    JsonNode expectedMessageRootNode = objectMapper
        .readTree("{\"name\":{\"familyName\":\"Smith\",\"givenName\":\"Kieran\"}}");
    JsonNode actualMessageRootNode = objectMapper.readTree(update);

    assertEquals(expectedMessageRootNode, actualMessageRootNode);
  }

  @Then("the user {string} is added to the following groups {string}")
  public void the_user_is_added_to_the_following_groups(String id, String grps) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "GSUITE_GROUPS_COMPLETE", 5000L));

    String[] groups = grps.split(",");
    String[] currentMemberGroups = gsuiteMockUtils.getGroups(id);
    System.out.println(currentMemberGroups.length);
    for(String group : groups) {
      assertThat(currentMemberGroups).contains(group+"@domain");
    }
  }
}
