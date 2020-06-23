package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.LwsMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.AREA_MANAGER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.COORDINATOR_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.FIELD_OFFICER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;

@Slf4j
@PropertySource("classpath:application.properties")
public class LwsSteps {

  @Autowired
  private SftpUtils sftpUtils;

  @Autowired
  private LwsMockUtils lwsMockUtils;

  @Value("${service.rabbit.url}")
  private String rabbitLocation;

  @Value("${service.rabbit.username}")
  private String rabbitUsername;

  @Value("${service.rabbit.password}")
  private String rabbitPassword;

  @Value("${lws.areaManagerInstruction}")
  private String areaManagerInstruction;

  @Value("${lws.operatorInstructionTwo}")
  private String operatorInstructionTwo;

  @Value("${lws.operatorInstructionThreeNisra}")
  private String operatorInstructionThreeNisra;

  @Then("the employee {string} is sent to LWS as an create with name {string} and phone number {string} and {string}")
  public void the_employee_in_the_LWS_CSV_as_an_createe(String id, String name, String number, String roleId) {
    gatewayEventMonitor.grabEventsTriggered("SENDING_LWS_ACTION_RESPONSE", 10, 5000L);
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_LWS_ACTION_RESPONSE", 1000L));
    String record = lwsMockUtils.getRecords();

    String lwsNumber = "44" + number.substring(1);

    assertThat(record).containsPattern("\"externalSystemPersonCode\":\"Fransico.Buyo[0-9]{2}@domain\"");
    assertThat(record).contains("\"isActivated\":1");
    assertThat(record).contains("\"personName\":\""+name+" Buyo\"");
    assertThat(record).contains("\"hierarchyItem1\":\"ONS\"");
    assertThat(record).contains("\"hierarchyItem2\":\"Wales\"");
    assertThat(record).contains("\"hierarchyItem3\":\"" + roleId.substring(0,4)+"\"");
    assertThat(record).contains("\"takeOnCode\":\"ONSAPP\"");
    assertThat(record).contains("\"phoneNumber\":\""+lwsNumber+"\"");
    assertThat(record).contains("\"personalMobileNumber\":\"0987654321\"");
    assertThat(record).contains("\"pinNumber\":-2");
    assertThat(record).contains("\"updateMode\":\"BULKLOADER\"");
    assertThat(record).contains("\"templatePersonId\":-1");
    assertThat(record).contains("\"newPersonId\":-1");
    assertThat(record).contains("\"operatorInstructions1\":\"a. Check Yellow/Safe Check messages for contextual information\\nb. Call the lone worker on "+number+"\"");
    assertThat(record).contains("\"operatorInstructions2\":\"a. Call on Personal mobile number "+ "0987654321"+" (leave voice mail)\\nb. Wait 10 minutes and repeat Operator Instructions No.1\"");
    assertThat(record).contains("\"operatorInstructions3\":\"Escalate to Field Staff Support - CFS\"");
    assertThat(record).doesNotContain("\"takeOnPassword\":null");
    assertThat(record).containsPattern("emailAddress\":\"Fransico.Buyo[0-9]{2}@domain");
    assertThat(record).contains("\"loginEnabled\":0");
    assertThat(record).contains("\"receiveAlertClosureReports\":1");
    assertThat(record).contains("\"loginPermissionTemplate\":\"ONSLINEMANAGER\"");
  }

  @Then("the employee {string} is sent to LWS as an update with name {string} and phone number {string} and {string}")
  public void the_employee_in_the_LWS_CSV_as_an_update(String id, String name, String number, String roleId) {
    String record = lwsMockUtils.getRecords();

    gatewayEventMonitor.grabEventsTriggered("SENDING_LWS_ACTION_RESPONSE", 10, 5000L);
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_LWS_ACTION_RESPONSE", 1000L));

    String lwsNumber = "44" + number.substring(1);

    assertThat(record).containsPattern("\"externalSystemPersonCode\":\"Fransico.Buyo[0-9]{2}@domain\"");
    assertThat(record).contains("\"isActivated\":1");
    assertThat(record).contains("\"personName\":\""+name+" Buyo\"");
    assertThat(record).contains("\"hierarchyItem1\":\"ONS\"");
    assertThat(record).contains("\"hierarchyItem2\":\"Wales\"");
    assertThat(record).contains("\"hierarchyItem3\":\"" + roleId.substring(0,4)+"\"");
    assertThat(record).contains("\"takeOnCode\":\"ONSAPP\"");
    assertThat(record).contains("\"phoneNumber\":\""+lwsNumber+"\"");
    assertThat(record).contains("\"personalMobileNumber\":\"0987654321\"");
    assertThat(record).contains("\"pinNumber\":-2");
    assertThat(record).contains("\"updateMode\":\"BULKLOADER\"");
    assertThat(record).contains("\"templatePersonId\":-1");
    assertThat(record).contains("\"newPersonId\":-1");
    assertThat(record).contains("\"operatorInstructions1\":\"a. Check Yellow/Safe Check messages for contextual information\\nb. Call the lone worker on "+number+"\"");
    assertThat(record).contains("\"operatorInstructions2\":\"a. Call on Personal mobile number "+ "0987654321"+" (leave voice mail)\\nb. Wait 10 minutes and repeat Operator Instructions No.1\"");
    assertThat(record).contains("\"operatorInstructions3\":\"Escalate to Field Staff Support - CFS\"");
    assertThat(record).doesNotContain("\"takeOnPassword\":null");
  }

  @Then("the employee {string} is sent to LWS as a mover with roleId {string}")
  public void the_employee_in_the_LWS_CSV_as_an_mover(String id, String roleId) {
    String record = lwsMockUtils.getRecords();

    gatewayEventMonitor.grabEventsTriggered("SENDING_LWS_ACTION_RESPONSE", 10, 5000L);
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_LWS_ACTION_RESPONSE", 1000L));

    assertThat(record).containsPattern("\"externalSystemPersonCode\":\"Fransico.Buyo[0-9]{2}@domain\"");
    assertThat(record).contains("\"isActivated\":1");
    assertThat(record).contains("\"personName\":\"Fransico Buyo\"");
    assertThat(record).contains("\"hierarchyItem1\":\"ONS\"");
    assertThat(record).contains("\"hierarchyItem2\":\"Wales\"");
    assertThat(record).contains("\"hierarchyItem3\":\"" + roleId.substring(0,4)+"\"");
    assertThat(record).contains("\"takeOnCode\":\"ONSAPP\"");
    assertThat(record).contains("\"phoneNumber\":\"447234567890\"");
    assertThat(record).contains("\"personalMobileNumber\":\"0987654321\"");
    assertThat(record).contains("\"pinNumber\":-2");
    assertThat(record).contains("\"updateMode\":\"BULKLOADER\"");
    assertThat(record).contains("\"templatePersonId\":-1");
    assertThat(record).contains("\"newPersonId\":-1");
    assertThat(record).contains("\"operatorInstructions1\":\"a. Check Yellow/Safe Check messages for contextual information\\nb. Call the lone worker on 07234567890\"");
    assertThat(record).contains("\"operatorInstructions2\":\"a. Call on Personal mobile number "+ "0987654321"+" (leave voice mail)\\nb. Wait 10 minutes and repeat Operator Instructions No.1\"");
    assertThat(record).contains("\"operatorInstructions3\":\"Escalate to Field Staff Support - CFS\"");
    assertThat(record).doesNotContain("\"takeOnPassword\":null");
  }

  @Then("the employee {string} is sent to LWS as an leaver with {string}")
  public void the_employee_in_the_LWS_CSV_as_an_Leaver(String id, String number) {
    String record = lwsMockUtils.getRecords();

    System.out.println(record);

    gatewayEventMonitor.grabEventsTriggered("SENDING_LWS_ACTION_RESPONSE", 10, 3000L);
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_LWS_ACTION_RESPONSE", 1000L));

    String lwsNumber = "44" + number.substring(1);

    assertThat(record).containsPattern("\"externalSystemPersonCode\":\"Fransico.Buyo[0-9]{2}@domain\"");
    assertThat(record).contains("\"isActivated\":0");
    assertThat(record).contains("\"personName\":\"Fransico Buyo\"");
    assertThat(record).contains("\"hierarchyItem1\":null");
    assertThat(record).contains("\"hierarchyItem2\":null");
    assertThat(record).contains("\"hierarchyItem3\":null");
    assertThat(record).contains("\"takeOnCode\":\"ONSAPP\"");
    assertThat(record).contains("\"phoneNumber\":\""+lwsNumber+"\"");
    assertThat(record).contains("\"personalMobileNumber\":\"0987654321\"");
    assertThat(record).contains("\"pinNumber\":-2");
    assertThat(record).contains("\"updateMode\":\"BULKLOADER\"");
    assertThat(record).contains("\"templatePersonId\":-1");
    assertThat(record).contains("\"newPersonId\":-1");
    assertThat(record).contains("\"operatorInstructions1\":null");
    assertThat(record).contains("\"operatorInstructions2\":null");
    assertThat(record).contains("\"operatorInstructions3\":null");
    assertThat(record).doesNotContain("\"takeOnPassword\":null");

  }

  @Then("the employee {string} is not sent to LWS")
  public void the_employee_is_not_created_in_Lwsw(String id) {
    gatewayEventMonitor.grabEventsTriggered("SENDING_LWS_ACTION_RESPONSE", 10, 3000L);
    assertFalse(gatewayEventMonitor.hasEventTriggered(id, "SENDING_LWS_ACTION_RESPONSE", 1000L));
    String records = lwsMockUtils.getRecords();
    assertThat(records).isEqualTo("[]");
  }

}
