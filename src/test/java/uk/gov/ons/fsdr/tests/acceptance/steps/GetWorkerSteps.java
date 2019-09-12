package uk.gov.ons.fsdr.tests.acceptance.steps;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Slf4j
@PropertySource("classpath:application.properties")
public class GetWorkerSteps {

    @Autowired
    private AdeccoMockUtils adeccoMockUtils = new AdeccoMockUtils();

    @Value("${service.mockadecco.url}")
    private String mockAdeccoUrl;

    private String adeccoWorker;

    @Before
    public void setup() throws IOException {
        adeccoWorker = Resources.toString(Resources.getResource("files/adeccoPut.json"), Charsets.UTF_8);

        adeccoMockUtils.clearMock();
        adeccoMockUtils.enableRequestRecorder();
    }

    @After
    public void tearDownGatewayEventMonitor() throws IOException {
        adeccoMockUtils.disableRequestRecorder();
        throw new cucumber.api.PendingException();
    }

    @Given("Adecco has created a worker with an employee ID of {string}")
    public void adeccoHasCreatedAWorkerWithAnEmployeeIDOf(String employeeId) {
        adeccoMockUtils.addContacts(adeccoWorker);
        throw new cucumber.api.PendingException();
    }

    @Then("I can retrieve the workers information for an employee with ID {string}")
    public void iCanRetrieveTheWorkersInformationForAnEmployeeWithID(String employeeId) {
        throw new cucumber.api.PendingException();
    }
}
