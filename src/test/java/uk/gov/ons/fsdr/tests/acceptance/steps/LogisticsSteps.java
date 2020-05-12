package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.SftpUtils;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@PropertySource("classpath:application.properties")
public class LogisticsSteps {

  @Autowired
  private SftpUtils sftpUtils;

  @Value("${service.rabbit.url}")
  private String rabbitLocation;

  @Value("${service.rabbit.username}")
  private String rabbitUsername;

  @Value("${service.rabbit.password}")
  private String rabbitPassword;

  @Then("the employee {string} in the Logisitics CSV with {string} as a create")
  public void the_employee_in_the_Logisitics_CSV_with_as_a_create(String inCsv, String roleId) throws Exception {

    String csvFilename = sftpUtils.getLogisticsFileName();
      String csv = sftpUtils.getCsv("logistics/", csvFilename);
    if (inCsv.contains("is not")) {
      assertThat(csv).doesNotContain("\""+roleId+"\"");
    } else {
      assertThat(csv).containsPattern(
          "\"Fransico\",\"Buyo\",,\"123\",\"Fake Street\",\"Faketon\",\"Fakeside\",\"FA43 1AB\",\"Wales\",\"f.b@email.com\",\"Fransico.Buyo[0-9]{2}@domain\",\"0987654321\",,,\""
              + roleId
              + "\",,\"ACTIVE\"");
    }
  }

  @Then("the employee {string} in the Logisitics CSV with {string} and phone number {string} as an update with name {string}")
  public void the_employee_in_the_Logisitics_CSV_with_and_phone_number_as_an_update(String inCsv, String roleId,
      String phoneNumber, String name) throws Exception {
    String csvFilename = sftpUtils.getLogisticsFileName();
      String csv = sftpUtils.getCsv("logistics/", csvFilename);
    if (!phoneNumber.equals(""))
      phoneNumber = "\"" + phoneNumber + "\"";
    if (inCsv.contains("is not")) {
      assertThat(csv).doesNotContain("\""+roleId+"\"");
    } else {
      assertThat(csv).containsPattern(
          "\"" + name
              + "\",\"Buyo\",,\"123\",\"Fake Street\",\"Faketon\",\"Fakeside\",\"FA43 1AB\",\"Wales\",\"f.b@email.com\",\"Fransico.Buyo[0-9]{2}@domain\",\"0987654321\","
              + phoneNumber + ",,\"" + roleId
              + "\",,\"ACTIVE\"");
    }
  }

  @Then("the employee {string} in the Logisitics CSV with {string}")
  public void the_employee_in_the_Logisitics_CSV_with(String inCsv, String roleId) throws Exception {
    String csvFilename = sftpUtils.getLogisticsFileName();
      String csv = sftpUtils.getCsv("logistics/", csvFilename);
    if (inCsv.contains("is not")) {
      assertThat(csv).doesNotContain("\""+roleId+"\"");
    } else {
      assertThat(csv).containsPattern(
          "\"Fransico\",\"Buyo\",,\"123\",\"Fake Street\",\"Faketon\",\"Fakeside\",\"FA43 1AB\",\"Wales\",\"f.b@email.com\",\"Fransico.Buyo[0-9]{2}@domain\",\"0987654321\",\"07234567890\",,\"" + roleId
              + "\",,\"ACTIVE\"");
    }
  }

  @Then("the employee {string} in the Logisitics CSV with {string} and phone number {string} as a leaver")
  public void theEmployeeIsCorrectInTheLogisticsCsv(String inCsv, String roleId, String phoneNumber) throws Exception {
    String csvFilename = sftpUtils.getLogisticsFileName();
    String csv = sftpUtils.getCsv("logistics/", csvFilename);
    if(inCsv.equals("is")) {
      assertThat(csv).containsPattern("\"Fransico\",\"Buyo\",,\"123\",\"Fake Street\",\"Faketon\",\"Fakeside\",\"FA43 1AB\",\"Wales\",\"f.b@email.com\",\"Fransico.Buyo[0-9]{2}@domain\",\"0987654321\",\""+phoneNumber+"\",,\""+roleId+"\",,\"LEFT\"");
    } else {
      assertThat(csv).doesNotContain("\""+roleId+"\"");
    }
  }

  @Then("the employee is not in the Logisitics CSV")
  public void the_employee_is_not_in_the_Logisitics_CSV() {
    String csvFilename = sftpUtils.getLogisticsFileName();

    assertThat(csvFilename).isBlank();
  }
}
