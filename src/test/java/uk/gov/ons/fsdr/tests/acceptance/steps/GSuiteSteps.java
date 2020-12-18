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
import uk.gov.ons.fsdr.common.util.JsonCompareUtil;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;

import java.io.IOException;
import java.util.Collection;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponse;
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

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Then("the employee {string} with closing report id {string} is correctly created in gsuite with roleId {string}")
  public void the_employee_is_correctly_created_in_gsuite(String id, String closingReportId, String roleId) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+closingReportId, "SENDING_GSUITE_ACTION_RESPONSE", 10000L));
    String[] records = gsuiteMockUtils.getRecords();
      int i = 0;
      for (String record : records) {
        boolean contains = record.contains("{\"RoleID\":\"" + roleId + "\"}");
        if (contains) break;
        i++;
      }

      String expected = "{\"changePasswordAtNextLogin\":false,\"customSchemas\":{\"Employee_Information\":{\"RoleID\":\"" + roleId
              + "\"}},\"externalIds\":[{\"type\":\"organization\",\"value\":\""+closingReportId+"\"}],\"hashFunction\":\"SHA-1\",\"includeInGlobalAddressList\":true,\"ipWhitelisted\":false,\"name\":{\"familyName\":\"Buyo\",\"givenName\":\"Fransico\"},\"orgUnitPath\":\"/Zero Access"
              + "\",\"organizations\":[{\"department\":\"/Zero Access\",\"primary\":true}], \"password\" : \"b308edffc594a97b8d2ed10b22e6e4385bc1ef76\",  \n" + 
              "                                          \"primaryEmail\" : \"fransico.buyo49@domain\",                \n" + 
              "                                          \"suspended\" : false  }";
      assertTrue(JsonCompareUtil.isEquals(expected, records[i], "$['primaryEmail']", "$['password']"));
      assertTrue(JsonCompareUtil.matches(records[i], "$['primaryEmail']", "fransico.buyo[0-9]{2}@domain"));
      assertTrue(JsonCompareUtil.matches(records[i], "$['password']", "[0-9a-zA-Z]{40}"));
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
        + "\"primaryEmail\":\"kieran.wardle[0-9]{2}@domain\","
        + "\"recoveryEmail\":\""+id+"@test\","
        + "\"suspended\":false");
  }

  @Then("the employee {string} with closing report id {string} is correctly updated in gsuite with name {string} and roleId {string}")
  public void the_employee_is_correctly_updated_in_gsuite(String id, String closingReportId, String name, String roleId) throws IOException {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+closingReportId, "GSUITE_USER_UPDATE_COMPLETE", 10000L));
    String[] records = gsuiteMockUtils.getRecords();
    String update = records[records.length - 1];

    JsonNode expectedMessageRootNode = objectMapper
        .readTree("{\"customSchemas\":{\"Employee_Information\":{\"RoleID\":\""+roleId+"\"}},\"name\":{\"familyName\":\"Buyo\",\"givenName\":\"" + name + "\"}}");
    JsonNode actualMessageRootNode = objectMapper.readTree(update);
    assertEquals(expectedMessageRootNode, actualMessageRootNode);
  }

  @Then("the employee {string} with closing report id {string} is correctly suspended in gsuite")
  public void theEmployeeIsCorrectlySuspendedInGsuite(String id, String closingReportId) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+closingReportId, "GSUITE_USER_SUSPEND_COMPLETE", 5000L));
    String[] records = gsuiteMockUtils.getRecords();
    String suspended1 = records[records.length - 2];
    String suspended2 = records[records.length - 1];
    assertEquals("{\"changePasswordAtNextLogin\":true,\"suspended\":true}", suspended1);
    assertEquals("{\"changePasswordAtNextLogin\":false}", suspended2);
  }

  @Then("the employee {string} with closing report id {string} is not created in gsuite")
  public void the_employee_is_not_created_in_gsuite(String id, String closingReportId) {
    String[] groups = gsuiteMockUtils.getGroups(id+closingReportId);
    String[] records = gsuiteMockUtils.getRecords();
    assertThat(groups).isNullOrEmpty();
    assertThat(records).isEmpty();
  }

  @Then("the employee {string} with closing report id {string} is not updated in gsuite")
  public void the_employee_is_not_updated_in_gsuite(String id, String crId) {
    if(crId.equals("")){
      assertFalse(gatewayEventMonitor.hasEventTriggered(id, "GSUITE_USER_UPDATE_NA", 5000L));
    } else assertTrue(gatewayEventMonitor.hasEventTriggered(id+crId, "GSUITE_USER_UPDATE_NA", 5000L));
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
        .readTree("{\"customSchemas\":{\"Employee_Information\":{\"RoleID\":\"xx-RMTx\"}},\"name\":{\"familyName\":\"Smith\",\"givenName\":\"Kieran\"}}");
    JsonNode actualMessageRootNode = objectMapper.readTree(update);

    assertEquals(expectedMessageRootNode, actualMessageRootNode);
  }

  @Then("the user {string} with closing report id {string} is added to the following groups {string}")
  public void the_user_is_added_to_the_following_groups(String id, String closingReportId, String grps) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+closingReportId, "GSUITE_GROUPS_COMPLETE", 5000L));

    String[] groups = grps.split(",");
    String[] currentMemberGroups = gsuiteMockUtils.getGroups(closingReportId);
    for(String group : groups) {
      assertThat(currentMemberGroups).contains(group+"@domain");
    }
  }

  @Then("the employee {string} with closing report id {string} is correctly setup in gsuite with orgUnit {string} with name {string} and roleId {string}")
  public void the_employee_is_correctly_setup_in_gsuite(String id, String closingReportId, String orgUnit, String name, String roleId) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+closingReportId, "GSUITE_USER_SETUP_COMPLETE", 10000L));
    String[] records = gsuiteMockUtils.getRecords();
    int i = 0;
    for (String record : records) {
      boolean notZeroAccess = !record.contains("Zero Access");

      if (notZeroAccess) break;
      i++;
    }
    assertThat(records[i]).contains(
        "{\"changePasswordAtNextLogin\":true,"
            + "\"customSchemas\":{\"Employee_Information\":{\"RoleID\":\""+ roleId + "\"}},"
            + "\"name\":{\"familyName\":\"Buyo\",\"givenName\":\""+name+"\"},"
            + "\"orgUnitPath\":\"/CFODS/"+orgUnit+"\","
            + "\"organizations\":[{\"department\":\""+orgUnit+"\",\"primary\":true}]}");
  }
}
