package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.LwsMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.AREA_MANAGER;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.COORDINATOR;

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

  @Then("the employee {string} in the LWS CSV as mover with {string}")
  public void the_employee_in_the_LWS_CSV_as_mover_with(String inCsv, String roleId) throws Exception {
    String csvFilename = sftpUtils.getLWSFileName();
    if (inCsv.equals("is")) {
      String csv = sftpUtils.getCsv("lws/", csvFilename);
      if (roleId.length() == AREA_MANAGER) {
        Assertions.assertThat(csv).containsPattern(
            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
                + "\"Fransico Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"0123456789\",\"N/A\",\"" + roleId
                + "\",\"Contact Lone Worker on mobile: 0123456789"
                + "\",\""+areaManagerInstruction+"\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"N/A\",\"N/A\",\"MOVER\"");
      } else if (roleId.length() == COORDINATOR) {
        Assertions.assertThat(csv).containsPattern(
            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
                + "\"Fransico Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"0123456789\",\"N/A\",\"" + roleId
                + "\",\"Contact Lone Worker on mobile: 0123456789"
                + "\",\"N/A\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"" + roleId
                .substring(0, AREA_MANAGER) + "\",\"N/A\",\"MOVER\"");
      } else {
        Assertions.assertThat(csv).containsPattern(
            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
                + "\"Fransico Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"0123456789\",\"N/A\",\"" + roleId
                + "\",\"Contact Lone Worker on mobile: 0123456789"
                + "\",\"N/A\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"" + roleId
                .substring(0, AREA_MANAGER) + "\",\""+roleId.substring(0,COORDINATOR)+"\",\"MOVER\"");
      }
    } else {
      Assertions.assertThat(csvFilename).isBlank();
    }
  }

//  @Then("the employee {string} in the LWS CSV as an update with name {string} and phone number {string} and {string}")
//  public void the_employee_in_the_LWS_CSV_as_an_update(String inCsv, String name, String number, String roleId)
//      throws Exception {
//    String csvFilename = sftpUtils.getLWSFileName();
//    if (inCsv.equals("is")) {
//      String csv = sftpUtils.getCsv("lws/", csvFilename);
//      if (roleId.length() == AREA_MANAGER) {
//        assertThat(csv).containsPattern(
//            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
//                + "\"" + name + " Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"" + number + "\",\"N/A\",\"" + roleId
//                + "\",\"Contact Lone Worker on mobile: " + number
//                + "\",\""+areaManagerInstruction+"\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"N/A\",\"N/A\",\"CREATE\"");
//      } else if (roleId.length() == COORDINATOR) {
//        assertThat(csv).containsPattern(
//            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
//                + "\"" + name + " Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"" + number + "\",\"N/A\",\"" + roleId
//                + "\",\"Contact Lone Worker on mobile: " + number
//                + "\",\"N/A\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"" + roleId
//                .substring(0, AREA_MANAGER) + "\",\"N/A\",\"CREATE\"");
//      } else {
//        assertThat(csv).containsPattern(
//            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
//                + "\"" + name + " Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"" + number + "\",\"N/A\",\"" + roleId
//                + "\",\"Contact Lone Worker on mobile: " + number
//                + "\",\"N/A\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"" + roleId
//                .substring(0, AREA_MANAGER) + "\",\"" + roleId.substring(0, COORDINATOR) + "\",\"CREATE\"");
//      }
//    } else {
//      assertThat(csvFilename).isBlank();
//    }
//  }

  @Then("the employee is sent to LWS as an update with name {string} and phone number {string} and {string}")
  public void the_employee_in_the_LWS_CSV_as_an_update(String name, String number, String roleId) {
    String record = lwsMockUtils.getRecords();

    String lmName = "";

    if(roleId.length() == 4) {
      assertThat(record).contains("\"operatorInstructions2\":\"areaManagerInstruction_CHANGE_ME\"");
    } else {
      if (roleId.length() == 10) {
        lmName = "Bob Jones";
      } else if (roleId.length() == 7) {
        lmName = "Dave Davis";
      }
      assertThat(record).contains("\"operatorInstructions2\":\"Check recent Yellow Alerts or Safe Check updates for additional information.\\\\n Contact the line manager (\""+lmName+"\") on \" 0112233445"+"\"");
    }

    assertThat(record).containsPattern("\"externalSystemPersonCode\":\"Fransico.Buyo[0-9]{2}@domain\"");
    assertThat(record).contains("\"isActivated\":1");
    assertThat(record).contains("\"personName\":\""+name+" Buyo\"");
    assertThat(record).contains("\"hierarchyItem1\":\"ONS\"");
    assertThat(record).contains("\"hierarchyItem2\":\"Wales\"");
    assertThat(record).contains("\"hierarchyItem3\":\"" + roleId.substring(0,4)+"\"");
    assertThat(record).contains("\"takeOnCode\":\"ONSTAKEonLIVE\"");
    assertThat(record).contains("\"phoneNumber\":\""+number+"\"");
    assertThat(record).contains("\"personalMobileNumber\":\"0987654321\"");
    assertThat(record).contains("\"pinNumber\":-2");
    assertThat(record).contains("\"updateMode\":\"BULKLOADER\"");
    assertThat(record).contains("\"templatePersonId\":-1");
    assertThat(record).contains("\"newPersonId\":-1");
    assertThat(record).contains("\"operatorInstructions1\":\"Contact Lone Worker on mobile: "+number+"\"");
    assertThat(record).containsPattern("\"operatorInstructions3\":\"Contact the Field Staff Contact Centre on: [0-9]{10}\"");
    assertThat(record).doesNotContain("\"takeOnPassword\":null");
  }

  @Then("the employee {string} in the LWS CSV as a leaver")
  public void theEmployeeIsCorrectInTheLwsCsv(String inCsv) throws Exception {
    String csvFilename = sftpUtils.getLWSFileName();
    if(inCsv.equals("is")) {
      String csv = sftpUtils.getCsv("lws/", csvFilename);
      assertThat(csv).containsPattern("\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
          + "\"Fransico Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"N/A\",\"ONS\",\"\",\"N/A\",\"N/A\",\"LEAVER\"");
    } else {
      assertThat(csvFilename).isBlank();
    }
  }

  @Then("the employee is not in the LWS CSV as a create")
  public void the_employee_in_the_LWS_CSV_as_a_create() throws Exception {
    String csvFilename = sftpUtils.getLWSFileName();
    assertThat(csvFilename).isBlank();
  }

}
