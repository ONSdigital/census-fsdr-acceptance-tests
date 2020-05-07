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

  @Then("the employee {string} is not sent to Adecco")
  public void the_employee_is_not_sent_to_Adecco(String id) {
    String[] messages = adeccoMockUtils.getAdeccoUpdateMessagesById(id);
    assertNull(messages);
  }

  @Then("the employee {string} is sent to Adecco")
  public void the_employee_is_sent_to_Adecco(String id) {
    assertTrue(gatewayEventMonitor.hasEventTriggered(id, "SENDING_ADECCO_ACTION_RESPONSE", 3000L));

    String[] messages = adeccoMockUtils.getAdeccoUpdateMessagesById(id);
    assertEquals(1, messages.length);
    assertThat(messages[0]).contains("\"Phone\":null,");
    assertThat(messages[0]).containsPattern("\"TR1__Work_Email__c\":\"Fransico.Buyo[0-9]{2}@domain\"");
  }
}
