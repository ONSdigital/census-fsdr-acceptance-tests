package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.gatewayEventMonitor;

public class AdeccoExtractSteps {

  @Autowired
  private AdeccoMockUtils adeccoMockUtils;

  @Then("the employee {string} with closing report id {string} is not sent to Adecco")
  public void the_employee_is_not_sent_to_Adecco(String id, String crId) {
    String[] messages = adeccoMockUtils.getAdeccoUpdateMessagesById(id);
    assertNull(messages);
  }

  @Then("the employee {string} with closing report id {string} is sent to Adecco")
  public void the_employee_is_sent_to_Adecco(String id, String crId) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+crId, "SENDING_ADECCO_ACTION_RESPONSE", 3000L));

    String[] messages = adeccoMockUtils.getAdeccoUpdateMessagesById(id);
    assertEquals(1, messages.length);
    assertThat(messages[0]).contains("\"Phone\":null,");
    assertThat(messages[0]).containsPattern("\"TR1__Work_Email__c\":\"fransico.buyo[0-9]{2}@domain\"");
  }

  @Then("the employee {string} with closing report id {string} is sent to Adecco with phone number {string}")
  public void the_employee_is_sent_to_Adecco_with_phone_number(String id, String crId, String number) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id+crId, "SENDING_ADECCO_ACTION_RESPONSE", 3000L));

    String[] messages = adeccoMockUtils.getAdeccoUpdateMessagesById(id);
    assertThat(messages[messages.length - 1]).contains("\"Phone\":\""+number+"\",");
    assertThat(messages[messages.length - 1]).containsPattern("\"TR1__Work_Email__c\":\"fransico.buyo[0-9]{2}@domain\"");
  }
}
