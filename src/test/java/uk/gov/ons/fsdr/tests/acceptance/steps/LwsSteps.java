package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.LwsMockUtils;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.FIELD_OFFICER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;

@Slf4j
@PropertySource("classpath:application.properties")
public class LwsSteps {

  @Autowired
  private LwsMockUtils lwsMockUtils;

  @Then("the employee {string} with closing report id {string} is sent to LWS as an create with name {string} and phone number {string} and {string} with expected hierarchy items {string} {string} {string} {string} {string} {string} {string}")
  public void the_employee_in_the_LWS_CSV_as_an_createe(String id, String crId, String name, String number, String roleId,
      String hierarchyItem1, String hierarchyItem2, String hierarchyItem3, String hierarchyItem4, String hierarchyItem5, String hierarchyItem6, String hierarchyItem7) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+crId, "LWS_CREATE_SENT", 15000L));
    String record = lwsMockUtils.getRecords();

    confirmFields(name, number, roleId, hierarchyItem1, hierarchyItem2, hierarchyItem3, hierarchyItem4, hierarchyItem5, hierarchyItem6, hierarchyItem7, record);
  }

  @Then("the employee {string} with closing report id {string} is sent to LWS as an update with name {string} and phone number {string} and {string} with expected hierarchy items {string} {string} {string} {string} {string} {string} {string}")
  public void the_employee_in_the_LWS_CSV_as_an_update(String id, String crId, String name, String number, String roleId,
      String hierarchyItem1, String hierarchyItem2, String hierarchyItem3, String hierarchyItem4, String hierarchyItem5, String hierarchyItem6, String hierarchyItem7) {
    String record = lwsMockUtils.getRecords();

    assertTrue(gatewayEventMonitor.hasEventTriggered(id+crId, "LWS_UPDATE_SENT", 15000L));

    confirmFields(name, number, roleId, hierarchyItem1, hierarchyItem2, hierarchyItem3, hierarchyItem4, hierarchyItem5, hierarchyItem6, hierarchyItem7, record);
  }

  @Then("the employee {string} with closing report id {string} is sent to LWS as a mover with roleId {string} and phone number {string} with expected hierarchy items {string} {string} {string} {string} {string} {string} {string}")
  public void the_employee_in_the_LWS_CSV_as_an_mover(String id, String crId, String roleId, String number, String hierarchyItem1,
      String hierarchyItem2, String hierarchyItem3, String hierarchyItem4, String hierarchyItem5, String hierarchyItem6, String hierarchyItem7) {
    String record = lwsMockUtils.getRecords();

    assertTrue(gatewayEventMonitor.hasEventTriggered(id+crId, "LWS_MOVER_SENT", 15000L));

    confirmFields("Fransico", number, roleId, hierarchyItem1, hierarchyItem2, hierarchyItem3, hierarchyItem4, hierarchyItem5, hierarchyItem6, hierarchyItem7, record);

  }

  private void confirmFields(String name, String number, String roleId, String hierarchyItem1, String hierarchyItem2, String hierarchyItem3, String hierarchyItem4,
      String hierarchyItem5, String hierarchyItem6, String hierarchyItem7, String record) {
    assertThat(record).containsPattern("\"externalSystemPersonCode\":\"fransico.buyo[0-9]{2}@domain\"");
    assertThat(record).contains("\"isActivated\":1");
    assertThat(record).contains("\"personName\":\"" + name + " Buyo\"");
    assertThat(record).contains("\"hierarchyItem1\":\"" + hierarchyItem1 + "\"");
    assertThat(record).contains("\"hierarchyItem2\":\"" + hierarchyItem2 + "\"");
    assertThat(record).contains("\"hierarchyItem3\":\"" + hierarchyItem3 + "\"");
    assertThat(record).contains("\"hierarchyItem4\":\"" + hierarchyItem4 + "\"");
    assertThat(record).contains("\"hierarchyItem5\":\"" + hierarchyItem5 + "\"");
    if (!hierarchyItem6.isBlank()) {
      assertThat(record).contains("\"hierarchyItem6\":\"" + hierarchyItem6 + "\"");
    }
    if (!hierarchyItem7.isBlank()) {
      assertThat(record).contains("\"hierarchyItem7\":\"" + hierarchyItem7 + "\"");
    }
    assertThat(record).contains("\"takeOnCode\":\"code\"");
    assertThat(record).contains("\"phoneNumber\":\"" + number + "\"");
    assertThat(record).contains("\"personalMobileNumber\":\"0987654321\"");
    assertThat(record).contains("\"pinNumber\":-2");
    assertThat(record).contains("\"updateMode\":\"BULKLOADER\"");
    assertThat(record).contains("\"templatePersonId\":-1");
    assertThat(record).contains("\"newPersonId\":-1");
    assertThat(record).contains(
        "\"operatorInstructions1\":\"a. Check Yellow/Safe Check messages for contextual information\\nb. Call the lone worker on "
            + number + "\"");
    assertThat(record).contains("\"operatorInstructions2\":\"a. Call on Personal mobile number " + "0987654321"
        + " (leave voice mail)\\nb. Wait 10 minutes and repeat Operator Instructions No.1\"");
    assertThat(record).contains("\"operatorInstructions3\":\"Escalate to Field Staff Support - CFS 0300 067 1505\"");
    assertThat(record).contains("\"takeOnPassword\":\"pass\"");
    assertThat(record).containsPattern("emailAddress\":\"fransico.buyo[0-9]{2}@domain");
    assertThat(record).contains("\"staffIdNumber\":\""+roleId+"\"");
    if (roleId.length() < FIELD_OFFICER_ROLE_ID_LENGTH) {
      assertThat(record).contains("\"loginEnabled\":1");
      assertThat(record).containsPattern("loginUsername\":\"fransico.buyo[0-9]{2}@domain");
      assertThat(record).contains("\"loginPermissionTemplate\":\"ONSLINEMANAGER\"");
    } else {
      assertThat(record).contains("\"loginEnabled\":0");
      assertThat(record).doesNotContainPattern("loginUsername\":\"fransico.buyo[0-9]{2}@domain");
      assertThat(record).doesNotContain("\"loginPermissionTemplate\":\"ONSLINEMANAGER\"");
    }
    assertThat(record).contains("\"receiveAlertClosureReports\":0");
  }

  @Then("the employee {string} with closing report id {string} is sent to LWS as an leaver with {string}")
  public void the_employee_in_the_LWS_CSV_as_an_Leaver(String id, String crId, String number) { String record = lwsMockUtils.getRecords();
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+crId, "LWS_LEAVER_SENT", 10000L));

    assertThat(record).containsPattern("\"externalSystemPersonCode\":\"fransico.buyo[0-9]{2}@domain\"");
    assertThat(record).contains("\"isActivated\":0");
    assertThat(record).contains("\"personName\":\"Fransico Buyo\"");
    assertThat(record).contains("\"hierarchyItem1\":null");
    assertThat(record).contains("\"hierarchyItem2\":null");
    assertThat(record).contains("\"hierarchyItem3\":null");
    assertThat(record).contains("\"takeOnCode\":\"code\"");
    assertThat(record).contains("\"phoneNumber\":null");
    assertThat(record).contains("\"personalMobileNumber\":\"0987654321\"");
    assertThat(record).contains("\"pinNumber\":-2");
    assertThat(record).contains("\"updateMode\":\"BULKLOADER\"");
    assertThat(record).contains("\"templatePersonId\":-1");
    assertThat(record).contains("\"newPersonId\":-1");
    assertThat(record).contains("\"operatorInstructions1\":null");
    assertThat(record).contains("\"operatorInstructions2\":null");
    assertThat(record).contains("\"operatorInstructions3\":null");
    assertThat(record).contains("\"takeOnPassword\":\"pass\"");
  }

  @Then("the employee {string} with closing report id {string} is not sent to LWS")
  public void the_employee_is_not_created_in_Lwsw(String id, String crId) {
    assertTrue(gatewayEventMonitor.hasEventTriggered("<N/A>", "FSDR_PROCESSES_ACTIONS_COMPLETE", 10000L));
    String records = lwsMockUtils.getRecords();
    assertThat(records).isEqualTo("[]");
  }

}
