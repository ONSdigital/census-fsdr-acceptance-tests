package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.XmaMockUtils;

import java.time.LocalDate;
import java.util.Arrays;

import static junit.framework.TestCase.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.AREA_MANAGER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.COORDINATOR_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.FIELD_OFFICER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;
import static uk.gov.ons.fsdr.tests.acceptance.steps.DeviceSteps.getDeviceCount;
import static uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils.getLastRecord;

@Slf4j
@PropertySource("classpath:application.properties")
public class XmaSteps {

    @Autowired
    private XmaMockUtils xmaMockUtils;

    @Autowired
    private FsdrUtils fsdrUtils;

    @Value("${service.rabbit.url}")
    private String rabbitLocation;

    @Value("${service.rabbit.username}")
    private String rabbitUsername;

    @Value("${service.rabbit.password}")
    private String rabbitPassword;

    private static final String uuidPattern = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})";

  @Then("the employee from {string} with roleId {string} is correctly created in XMA with group {string}")
    public void the_employee_with_roleId_is_correctly_updated_in_XMA(String source, String roleId, String group) {

      boolean hasManager = roleId.length() > AREA_MANAGER_ROLE_ID_LENGTH;

        String[] records = xmaMockUtils.getEmployeeRecords();
        int i = 0;
        for (String record : records) {
            boolean contains = record.contains(String.format("\"name\":\"_RoleID\",\"value\":\"%s\"", roleId));
            if (contains) break;
            i++;
        }

        assertThat(records[i]).contains(
                "{\"className\":\"System.EndUser\",\"formValues\":[{\"name\":\"_BadgeNumber\",\"value\":null},{\"name\":\"_EmploymentStatus\",\"value\":\"ACTIVE\"},{\"name\":\"_FirstName\",\"value\":"
                        + "\"Fransico\"},{");
        assertThat(records[i]).containsPattern("\"name\":\"_ContractStartDate\",\"value\":\"[0-9-]{10}");
        assertThat(records[i]).contains("\"},{\"name\":\"_ContractEndDate\",\"value\":\"" + LocalDate.now().plusDays(5)
                + "\"},{\"name\":\"_JobRole\",\"value\":null},{\"name\":\"_LocationString\",\"value\":\"London\"},{\"name\":\"_RoleID\",\"value\":\""
                + roleId + "\"}");
        assertThat(records[i]).contains("{\"name\":\"CurrentGroup\",\"value\":\"" + group + "\"},{\"name\":\"PrimaryGroup\",\"value\":\"" + group + "\"},{");
        assertThat(records[i]).containsPattern("\"name\":\"Name\",\"value\":\"fransico.buyo[0-9]{2}@domain\"");
        assertThat(records[i])
                .contains("},{\"name\":\"_PreferredName\",\"value\":null},{\"name\":\"_UserOrg\",\"value\":\"" + source
                        + "\"},{\"name\":\"_Surname\",\"value\":\"Buyo\"},{");
        assertThat(records[i]).containsPattern("\"name\":\"EMailAddress\",\"value\":\"fransico.buyo[0-9]{2}@domain\"");
        assertThat(records[i]).contains(
                "},{\"name\":\"Title\",\"value\":\"Fransico Buyo\"},{\"name\":\"_PersonalEmail\",\"value\":\"f.b@email.com\"},{\"name\":\"_PersonalPhone\",\"value\":\"0987654321\"},{\"name\":\"_Address\",\"value\":\"123, Fake Street, Faketon, Fakeside, FA43 1AB\"},{\"name\":\"_Postcode\",\"value\":\"FA43 1AB\"}]");
        if (hasManager) {
          assertThat(records[i]).containsPattern("\"name\":\"_LineManager\",\"value\":\"([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\"}");
        }
    }


  @Then("the employee  is not created in XMA")
    public void the_employee_is_not_created_in_XMA() {
        String[] records = xmaMockUtils.getEmployeeRecords();
        assertThat(records).isEmpty();
    }

    @Then("the employee {string} is not updated in XMA")
    public void the_employee_is_not_updated_in_XMA(String id) {
        int expextedCount = 0;
        if (id.length() == FIELD_OFFICER_ROLE_ID_LENGTH) expextedCount = 3;
        else if (id.length() == COORDINATOR_ROLE_ID_LENGTH) expextedCount = 2;
        else if (id.length() == AREA_MANAGER_ROLE_ID_LENGTH) expextedCount = 1;
        String[] records = xmaMockUtils.getEmployeeRecords();
        assertEquals(expextedCount, records.length);
    }

    @Then("the employee from {string} with old roleId {string} and new roleId {string} is correctly moved in XMA with group {string}")
    public void the_employee_from_with_roleId_is_correctly_moved_in_XMA_with_group(String source, String oldRoleId, String roleId, String group) {
      gatewayEventMonitor.grabEventsTriggered("SENDING_XMA_ACTION_RESPONSE", 6, 20000l);

        boolean hasManager = roleId.length() > AREA_MANAGER_ROLE_ID_LENGTH;
        String[] records = xmaMockUtils.getEmployeeRecords();
        String update = getLastRecord(records, roleId);

        assertThat(update).contains(
                "{\"className\":\"System.EndUser\",\"formValues\":[{\"name\":\"_BadgeNumber\",\"value\":null},{\"name\":\"_EmploymentStatus\",\"value\":\"ACTIVE\"},{\"name\":\"_FirstName\",\"value\":"
                        + "\"Fransico\"},{");
        assertThat(update).containsPattern("\"name\":\"_ContractStartDate\",\"value\":\"[0-9-]{10}");
        assertThat(update).contains("\"},{\"name\":\"_ContractEndDate\",\"value\":\"" + LocalDate.now().plusDays(5)
                + "\"},{\"name\":\"_JobRole\",\"value\":null},{\"name\":\"_LocationString\",\"value\":\"London\"},{\"name\":\"_RoleID\",\"value\":\""
                + roleId + "\"}");
        assertThat(update).contains("{\"name\":\"CurrentGroup\",\"value\":\"" + group + "\"},{\"name\":\"PrimaryGroup\",\"value\":\"" + group + "\"},{");
        assertThat(update).containsPattern("\"name\":\"Name\",\"value\":\"fransico.buyo[0-9]{2}@domain\"");
        assertThat(update)
                .contains("},{\"name\":\"_PreferredName\",\"value\":null},{\"name\":\"_UserOrg\",\"value\":\"" + source
                        + "\"},{\"name\":\"_Surname\",\"value\":\"Buyo\"},{");
        assertThat(update).containsPattern("\"name\":\"EMailAddress\",\"value\":\"fransico.buyo[0-9]{2}@domain\"");
        assertThat(update).contains("},{\"name\":\"Title\",\"value\":\"Fransico"
                + " Buyo\"},{\"name\":\"_PersonalEmail\",\"value\":\"f.b@email.com\"},{\"name\":\"_PersonalPhone\",\"value\":\"0987654321\"},{\"name\":\"_Address\",\"value\":\"123, Fake Street, Faketon, Fakeside, FA43 1AB\"},{\"name\":\"_Postcode\",\"value\":\"FA43 1AB\"}]");
        assertThat(update).containsPattern("\"key\":\"" + uuidPattern + "\",\"originalValues\":null,\"lockVersion\":1}");
        if (hasManager) {
            assertThat(update).containsPattern("\"name\":\"_LineManager\",\"value\":\"" + uuidPattern + "\"}");
        }
    }

    @Then("the employee from {string} with roleId {string} is correctly updated in XMA with name {string} and group {string}")
    public void the_employee_with_roleId_is_correctly_updated_in_XMA(String source, String roleId, String name,
                                                                     String group) {
        String id = xmaMockUtils.getId(roleId);
        boolean hasManager = roleId.length() > AREA_MANAGER_ROLE_ID_LENGTH;


        String[] records = xmaMockUtils.getEmployeeRecords();

        assertThat(records[records.length - 1]).contains(
                "{\"className\":\"System.EndUser\",\"formValues\":[{\"name\":\"_BadgeNumber\",\"value\":null},{\"name\":\"_EmploymentStatus\",\"value\":\"ACTIVE\"},{\"name\":\"_FirstName\",\"value\":"
                        + "\"" + name + "\"},{");
        assertThat(records[records.length - 1]).containsPattern("\"name\":\"_ContractStartDate\",\"value\":\"[0-9-]{10}");
        assertThat(records[records.length - 1]).contains("\"},{\"name\":\"_ContractEndDate\",\"value\":\"" + LocalDate.now().plusDays(5)
                + "\"},{\"name\":\"_JobRole\",\"value\":null},{\"name\":\"_LocationString\",\"value\":\"London\"},{\"name\":\"_RoleID\",\"value\":\""
                + roleId + "\"}");
        assertThat(records[records.length - 1]).contains("{\"name\":\"CurrentGroup\",\"value\":\"" + group + "\"},{\"name\":\"PrimaryGroup\",\"value\":\"" + group + "\"},{");
        assertThat(records[records.length - 1]).containsPattern("\"name\":\"Name\",\"value\":\"fransico.buyo[0-9]{2}@domain\"");
        assertThat(records[records.length - 1])
                .contains("},{\"name\":\"_PreferredName\",\"value\":null},{\"name\":\"_UserOrg\",\"value\":\"" + source
                        + "\"},{\"name\":\"_Surname\",\"value\":\"Buyo\"},{");
        assertThat(records[records.length - 1]).containsPattern("\"name\":\"EMailAddress\",\"value\":\"fransico.buyo[0-9]{2}@domain\"");
        assertThat(records[records.length - 1]).contains("},{\"name\":\"Title\",\"value\":\"" + name
                + " Buyo\"},{\"name\":\"_PersonalEmail\",\"value\":\"f.b@email.com\"},{\"name\":\"_PersonalPhone\",\"value\":\"0987654321\"},{\"name\":\"_Address\",\"value\":\"123, Fake Street, Faketon, Fakeside, FA43 1AB\"},{\"name\":\"_Postcode\",\"value\":\"FA43 1AB\"}],\"");
        assertThat(records[records.length - 1]).contains("key\":\"" + id + "\",\"originalValues\":null,\"lockVersion\":1}");
        if (hasManager) {
            assertThat(records[records.length - 1]).containsPattern("\"name\":\"_LineManager\",\"value\":\"" + uuidPattern + "\"}");
        }
    }

    @Then("the employee with roleId {string} is correctly suspended in XMA")
    public void theEmployeeIsCorrectlySuspendedInXMA(String roleId) {

        String id = xmaMockUtils.getId(roleId);

        String[] records = xmaMockUtils.getEmployeeRecords();

        assertEquals("{\"className\":\"RequestManagement.Request\",\"formValues\":[{\"name\":\"_DeletionUser\",\"value\":\"" + id + "\"}],\"lifecycle_name\":\"NewProcess8\"}", records[records.length - 1]);

    }

  @Then("the employee {string} with roleId {string} {string} device allocation details are sent to xma with ID {string}")
  public void theEmployeeDeviceAllocationDetailsAreSentToXma(String employeeId, String roleId, String deviceType, String deviceId)
      throws Exception {
    fsdrUtils.sendDeviceAllocation();

    gatewayEventMonitor.grabEventsTriggered("XMA_DEVICE_SENT", getDeviceCount(), 10000l);

    String id = xmaMockUtils.getId(roleId);
    final String[] records = xmaMockUtils.getDeviceAllocationRecords();
    Arrays.stream(records).forEach(System.out::println);

    String expectedRequest = null;
    if (deviceType.equals("phone")) {
      expectedRequest =
          "{\"className\":\"RequestManagement.Request\",\"formValues\":[{\"name\":\"RaiseUser\",\"value\":\"d2ba61a5-f0c7-4904-b02a-362a3b348899\"},{\"name\":\"_eTrackerAllocUserObj\",\"value\":\""
              + id + "\"},{\"name\":\"_eTrackerDevicePhoneNumber\",\"value\":\"" + deviceId
              + "\"},{\"name\":\"_SystemPartition\",\"value\":\"762a653c-35bf-456a-9de3-41444504e6d6\"},{\"name\":\"Title\",\"value\":\"eTracker API Device Allocation\"},{\"name\":\"Description\",\"value\":\"eTracker API Device Allocation\"}],\"lifecycle_name\":\"NewProcess12\"}";
    } else if (deviceType.equals("chromebook")) {
      expectedRequest =
          "{\"className\":\"RequestManagement.Request\",\"formValues\":[{\"name\":\"RaiseUser\",\"value\":\"d2ba61a5-f0c7-4904-b02a-362a3b348899\"},{\"name\":\"_eTrackerAllocUserObj\",\"value\":\""
              + id + "\"},{\"name\":\"_eTrackerIMEI\",\"value\":\"" + deviceId
              + "\"},{\"name\":\"_SystemPartition\",\"value\":\"762a653c-35bf-456a-9de3-41444504e6d6\"},{\"name\":\"Title\",\"value\":\"eTracker API Device Allocation\"},{\"name\":\"Description\",\"value\":\"eTracker API Device Allocation\"}],\"lifecycle_name\":\"NewProcess12\"}";
    } else fail();

    assertEquals(expectedRequest, records[records.length - 1]);


  }

  @And("the employee {string} device details are not sent to xma")
  public void theEmployeeDeviceDetailsAreNotSentToXma(String employeeId) {
    gatewayEventMonitor.grabEventsTriggered("XMA_DEVICE_SENT", 1, 3000L);
    assertFalse(gatewayEventMonitor.hasEventTriggered(employeeId, "XMA_DEVICE_SENT", 1000L));
    String[] records = xmaMockUtils.getDeviceAllocationRecords();
    assertThat(records).isEmpty();
  }
}
