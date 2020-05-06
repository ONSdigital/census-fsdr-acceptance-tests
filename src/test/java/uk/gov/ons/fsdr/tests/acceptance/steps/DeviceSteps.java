package uk.gov.ons.fsdr.tests.acceptance.steps;

import cucumber.api.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Slf4j
public class DeviceSteps {

    public static GatewayEventMonitor gatewayEventMonitor = new GatewayEventMonitor();

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${device.baseUrl}")
    private String mockDeviceUrl;

    @Then("we ingest a device from pubsub for {string} with phone number {string}")
    public void weIngestADeviceFromPubsubForWithPhoneNumber(String employeeId, String phoneNumber) throws Exception {
        String onsId = getOnsId(employeeId);
        if (onsId == null) fail("failed to find ons id for employee " + employeeId);
        postDevice(onsId, phoneNumber);
        assertTrue(gatewayEventMonitor.hasEventTriggered("<N/A>", "SAVE_DEVICE"));

    }

    private String getOnsId(String employeeId) throws Exception {
        Statement stmt = null;

        String result = null;

        try (Connection conn = DriverManager.getConnection(
                url, username, password)) {
            if (conn != null) {
                System.out.println("Connected to the database!");
                stmt = conn.createStatement();
                String sql = String.format("SELECT ons_email_address FROM fsdr.employee WHERE unique_employee_id = '%s'", employeeId);
                ResultSet resultSet = stmt.executeQuery(sql);
                resultSet.next();
                result = resultSet.getString("ons_email_address");
            }
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se) {
            } // do nothing
        }
        return result;
    }

    public void postDevice(String onsId, String phoneNumber) {
        RestTemplate restTemplate = new RestTemplate();
        String url = mockDeviceUrl + "createPubSub";
        log.info("createDevice-mock_url:" + url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("onsId", onsId)
                .queryParam("phoneNumber", phoneNumber);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, HttpStatus.class);
    }
}
