package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.XmaMockUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoSteps.AREA_MANAGER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;
import static uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils.getLastRecord;

@Slf4j
@PropertySource("classpath:application.properties")
public class XmaSteps {

    @Autowired
    private XmaMockUtils xmaMockUtils;

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

        String[] records = xmaMockUtils.getRecords();
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
        assertThat(records[i]).containsPattern("\"name\":\"Name\",\"value\":\"Fransico.Buyo[0-9]{2}@domain\"");
        assertThat(records[i])
                .contains("},{\"name\":\"_PreferredName\",\"value\":null},{\"name\":\"_UserOrg\",\"value\":\"" + source
                        + "\"},{\"name\":\"_Surname\",\"value\":\"Buyo\"},{");
        assertThat(records[i]).containsPattern("\"name\":\"EMailAddress\",\"value\":\"Fransico.Buyo[0-9]{2}@domain\"");
        assertThat(records[i]).contains(
                "},{\"name\":\"Title\",\"value\":\"Fransico Buyo\"},{\"name\":\"_PersonalEmail\",\"value\":\"f.b@email.com\"},{\"name\":\"_PersonalPhone\",\"value\":\"0987654321\"},{\"name\":\"_Address\",\"value\":\"123, Fake Street, Fakeside, FA43 1AB\"}]");
        if (hasManager) {
          assertThat(records[i]).containsPattern("\"name\":\"_LineManager\",\"value\":\"([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\"}");
        }
    }


  @Then("the employee  is not created in XMA")
    public void the_employee_is_not_created_in_XMA() {
        String[] records = xmaMockUtils.getRecords();
        assertThat(records).isEmpty();
    }

    @Then("the employee {string} is not updated in XMA")
    public void the_employee_is_not_updated_in_XMA(String id) {
        int expextedCount = 0;
        if (id.length() == 10) expextedCount = 3;
        else if (id.length() == 7) expextedCount = 2;
        else if (id.length() == 4) expextedCount = 1;
        String[] records = xmaMockUtils.getRecords();
        assertEquals(expextedCount, records.length);
    }

    @Then("the employee from {string} with old roleId {string} and new roleId {string} is correctly moved in XMA with group {string}")
    public void the_employee_from_with_roleId_is_correctly_moved_in_XMA_with_group(String source, String oldRoleId, String roleId, String group) {
      gatewayEventMonitor.grabEventsTriggered("SENDING_XMA_ACTION_RESPONSE", 6, 20000l);

        boolean hasManager = roleId.length() > AREA_MANAGER_ROLE_ID_LENGTH;
        String[] records = xmaMockUtils.getRecords();
        String update = getLastRecord(records, roleId);

        assertThat(update).contains(
                "{\"className\":\"System.EndUser\",\"formValues\":[{\"name\":\"_BadgeNumber\",\"value\":null},{\"name\":\"_EmploymentStatus\",\"value\":\"ACTIVE\"},{\"name\":\"_FirstName\",\"value\":"
                        + "\"Fransico\"},{");
        assertThat(update).containsPattern("\"name\":\"_ContractStartDate\",\"value\":\"[0-9-]{10}");
        assertThat(update).contains("\"},{\"name\":\"_ContractEndDate\",\"value\":\"" + LocalDate.now().plusDays(5)
                + "\"},{\"name\":\"_JobRole\",\"value\":null},{\"name\":\"_LocationString\",\"value\":\"London\"},{\"name\":\"_RoleID\",\"value\":\""
                + roleId + "\"}");
        assertThat(update).contains("{\"name\":\"CurrentGroup\",\"value\":\"" + group + "\"},{\"name\":\"PrimaryGroup\",\"value\":\"" + group + "\"},{");
        assertThat(update).containsPattern("\"name\":\"Name\",\"value\":\"Fransico.Buyo[0-9]{2}@domain\"");
        assertThat(update)
                .contains("},{\"name\":\"_PreferredName\",\"value\":null},{\"name\":\"_UserOrg\",\"value\":\"" + source
                        + "\"},{\"name\":\"_Surname\",\"value\":\"Buyo\"},{");
        assertThat(update).containsPattern("\"name\":\"EMailAddress\",\"value\":\"Fransico.Buyo[0-9]{2}@domain\"");
        assertThat(update).contains("},{\"name\":\"Title\",\"value\":\"Fransico"
                + " Buyo\"},{\"name\":\"_PersonalEmail\",\"value\":\"f.b@email.com\"},{\"name\":\"_PersonalPhone\",\"value\":\"0987654321\"},{\"name\":\"_Address\",\"value\":\"123, Fake Street, Fakeside, FA43 1AB\"}]");
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


        String[] records = xmaMockUtils.getRecords();

        assertThat(records[records.length - 1]).contains(
                "{\"className\":\"System.EndUser\",\"formValues\":[{\"name\":\"_BadgeNumber\",\"value\":null},{\"name\":\"_EmploymentStatus\",\"value\":\"ACTIVE\"},{\"name\":\"_FirstName\",\"value\":"
                        + "\"" + name + "\"},{");
        assertThat(records[records.length - 1]).containsPattern("\"name\":\"_ContractStartDate\",\"value\":\"[0-9-]{10}");
        assertThat(records[records.length - 1]).contains("\"},{\"name\":\"_ContractEndDate\",\"value\":\"" + LocalDate.now().plusDays(5)
                + "\"},{\"name\":\"_JobRole\",\"value\":null},{\"name\":\"_LocationString\",\"value\":\"London\"},{\"name\":\"_RoleID\",\"value\":\""
                + roleId + "\"}");
        assertThat(records[records.length - 1]).contains("{\"name\":\"CurrentGroup\",\"value\":\"" + group + "\"},{\"name\":\"PrimaryGroup\",\"value\":\"" + group + "\"},{");
        assertThat(records[records.length - 1]).containsPattern("\"name\":\"Name\",\"value\":\"Fransico.Buyo[0-9]{2}@domain\"");
        assertThat(records[records.length - 1])
                .contains("},{\"name\":\"_PreferredName\",\"value\":null},{\"name\":\"_UserOrg\",\"value\":\"" + source
                        + "\"},{\"name\":\"_Surname\",\"value\":\"Buyo\"},{");
        assertThat(records[records.length - 1]).containsPattern("\"name\":\"EMailAddress\",\"value\":\"Fransico.Buyo[0-9]{2}@domain\"");
        assertThat(records[records.length - 1]).contains("},{\"name\":\"Title\",\"value\":\"" + name
                + " Buyo\"},{\"name\":\"_PersonalEmail\",\"value\":\"f.b@email.com\"},{\"name\":\"_PersonalPhone\",\"value\":\"0987654321\"},{\"name\":\"_Address\",\"value\":\"123, Fake Street, Fakeside, FA43 1AB\"}],\"");
        assertThat(records[records.length - 1]).contains("key\":\"" + id + "\",\"originalValues\":null,\"lockVersion\":1}");
        if (hasManager) {
            assertThat(records[records.length - 1]).containsPattern("\"name\":\"_LineManager\",\"value\":\"" + uuidPattern + "\"}");
        }
    }

    @Then("the employee with roleId {string} is correctly suspended in XMA")
    public void theEmployeeIsCorrectlySuspendedInXMA(String roleId) {

        String id = xmaMockUtils.getId(roleId);

        System.out.println(id);
        String[] records = xmaMockUtils.getRecords();

        assertEquals("{\"className\":\"RequestManagement.Request\",\"formValues\":[{\"name\":\"_DeletionUser\",\"value\":\"" + id + "\"}],\"lifecycle_name\":\"NewProcess8\"}", records[records.length - 1]);

    }

    @Given("a device exists in XMA with {string}, {string} and {string}")
    public void a_device_exists_in_XMA_with_and(String roleId, String phoneNumber, String status) {
        xmaMockUtils.postDevice(roleId, phoneNumber, status);
    }

}
