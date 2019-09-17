package uk.gov.ons.fsdr.tests.acceptance.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.tests.acceptance.entity.Employee;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Slf4j
@PropertySource("classpath:application.properties")
public class GetWorkerSteps {

    @Autowired
    private AdeccoMockUtils adeccoMockUtils;

    @Autowired
    private FsdrUtils fsdrUtils;

    @Autowired
    private ObjectMapper objectMapper;

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
    }

    @Given("Adecco has created a worker with an employee")
    public void adeccoHasCreatedAWorkerWithAnEmployeeIDOf() throws IOException {
        AdeccoResponse adeccoResponse = objectMapper.readValue(adeccoWorker, AdeccoResponse.class);

        List<AdeccoResponse> adeccoResponseList = new ArrayList<>();
        adeccoResponseList.add(adeccoResponse);
        adeccoMockUtils.addUsersAdecco(adeccoResponseList);
    }

    @Then("as FSDR system I can pull off Adecco")
    public void asFSDRSystemICanPullOffAdecco() throws IOException {
        fsdrUtils.ingestAdecco();
    }

    @And("search database for {string} employee with ID {string}")
    public void searchDatabaseForEmployeeWithID(String source, String uniqueId) {
        List<Employee> results = fsdrUtils.getAllEmployeesBySource(source);
    }
}
