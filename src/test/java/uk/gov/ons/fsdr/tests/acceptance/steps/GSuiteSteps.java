package uk.gov.ons.fsdr.tests.acceptance.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.GsuiteMockUtils;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

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

  @Then("the employee is correctly created in gsuite with roleId {string} and orgUnit {string}")
  public void the_employee_is_correctly_updated_in_gsuite(String roleId, String orgUnit) {
      String[] records = gsuiteMockUtils.getRecords();
      int i = 0;
      for (String record : records) {
        boolean contains = record.contains("{\"RoleID\":\"" + roleId + "\"}");
        if (contains) break;
        i++;
      }

    assertThat(records[i]).contains(
        "\"changePasswordAtNextLogin\":true,\"customSchemas\":{\"Employee_Information\":{\"RoleID\":\"" + roleId
            + "\"}},\"externalIds\":[{\"type\":\"organization\",\"value\":\"123456789\"}],\"hashFunction\":\"SHA-1\",\"includeInGlobalAddressList\":true,\"ipWhitelisted\":false,\"name\":{\"familyName\":\"Buyo\",\"givenName\":\"Fransico\"},\"orgUnitPath\":\"/CFODS/"
            + orgUnit + "\",\"organizations\":[{\"department\":\"" + orgUnit + "\",\"primary\":true}]");
    assertThat(records[i]).containsPattern(
        ",\"password\":\"[0-9a-zA-Z]{40}\",\"primaryEmail\":\"Fransico.Buyo[0-9]{2}@domain\",\"suspended\":false");
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

  @Then("the employee is correctly moved in gsuite with roleId {string} to {string}")
  public void the_employee_is_correctly_moved_in_gsuite_with_roleId(String roleId, String orgUnit) throws IOException {
    String[] records = gsuiteMockUtils.getRecords();
    String update = records[1];

    JsonNode expectedMessageRootNode = objectMapper
        .readTree("{\"customSchemas\":{\"Employee_Information\":{\"RoleID\":\""+roleId+"\"}},\"name\":{\"familyName\":\"Buyo\",\"givenName\":\"Fransico\"},\"orgUnitPath\":\"/CFODS/"+orgUnit+"\",\"organizations\":[{\"department\":\""+orgUnit+"\",\"primary\":true}]}");
    JsonNode actualMessageRootNode = objectMapper.readTree(update);

    assertEquals(expectedMessageRootNode, actualMessageRootNode);
  }

  @Then("the employee is correctly suspended in gsuite")
  public void theEmployeeIsCorrectlySuspendedInGsuite() {
    String[] records = gsuiteMockUtils.getRecords();
    String suspended1 = records[1];
    String suspended2 = records[2];
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
    String[] records = gsuiteMockUtils.getRecords();
    assertEquals(1,records.length);
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
}
