package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.AREA_MANAGER;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.COORDINATOR;

@Slf4j
@PropertySource("classpath:application.properties")
public class LwsSteps {

  @Autowired
  private SftpUtils sftpUtils;

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

  @Then("the employee {string} in the LWS CSV as an update with name {string} and phone number {string} and {string}")
  public void the_employee_in_the_LWS_CSV_as_an_update(String inCsv, String name, String number, String roleId)
      throws Exception {
    String csvFilename = sftpUtils.getLWSFileName();
    if (inCsv.equals("is")) {
      String csv = sftpUtils.getCsv("lws/", csvFilename);
      if (roleId.length() == AREA_MANAGER) {
        assertThat(csv).containsPattern(
            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
                + "\"" + name + " Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"" + number + "\",\"N/A\",\"" + roleId
                + "\",\"Contact Lone Worker on mobile: " + number
                + "\",\""+areaManagerInstruction+"\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"N/A\",\"N/A\",\"CREATE\"");
      } else if (roleId.length() == COORDINATOR) {
        assertThat(csv).containsPattern(
            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
                + "\"" + name + " Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"" + number + "\",\"N/A\",\"" + roleId
                + "\",\"Contact Lone Worker on mobile: " + number
                + "\",\"N/A\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"" + roleId
                .substring(0, AREA_MANAGER) + "\",\"N/A\",\"CREATE\"");
      } else {
        assertThat(csv).containsPattern(
            "\"Allocated User\",\"Email\",\"Device Telephone Number\",\"Allocated Manager\",\"Role ID\",\"Operator Instructions #1\",\"Operator Instructions #2\",\"Operator Instructions #3\",\"Organisation #1\",\"Organisation #2\",\"Organisation #3\",\"Organisation #4\",\"Action\"\n"
                + "\"" + name + " Buyo\",\"Fransico.Buyo[0-9]{2}@domain\",\"" + number + "\",\"N/A\",\"" + roleId
                + "\",\"Contact Lone Worker on mobile: " + number
                + "\",\"N/A\",\"Contact the Field Staff Contact Centre on: number\",\"ONS\",\"\",\"" + roleId
                .substring(0, AREA_MANAGER) + "\",\"" + roleId.substring(0, COORDINATOR) + "\",\"CREATE\"");
      }
    } else {
      assertThat(csvFilename).isBlank();
    }
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
