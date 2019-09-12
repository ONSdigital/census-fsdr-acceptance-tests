package uk.gov.ons.fsdr.tests.acceptance.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseList;
import uk.gov.ons.fsdr.tests.acceptance.exceptions.MockInaccessibleException;

import javax.xml.bind.JAXBContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
public final class AdeccoMockUtils
{
    @Value("${service.fsdrservice.url}")
    private String fsdrServiceUrl;

    @Value("${service.fsdrservice.username}")
    private String fsdrServiceUsername;

    @Value("${service.fsdrservice.password}")
    private String fsdrServicePassword;

    @Value("${service.mockadecco.url}")
    private String mockAdeccoUrl;

    private RestTemplate restTemplate = new RestTemplate();

    private JAXBContext jaxbContext;

    public void clearMock() throws IOException {
        URL url = new URL(mockAdeccoUrl + "/clear");
        log.info("clear-mock_url:" + url.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        if (httpURLConnection.getResponseCode() != 200) {
            throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
        }
    }

    public AdeccoResponseList getRecords() {
        String url = mockAdeccoUrl + "/records";
        log.info("getRecords-mock_url:" + url);
        ResponseEntity<AdeccoResponseList> responseEntity;
        responseEntity = restTemplate.getForEntity(url, AdeccoResponseList.class);
        return responseEntity.getBody();
    }

    public int addContacts(String data) {
        HttpHeaders headers = createBasicAuthHeaders("user", "password");

        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        String postUrl = mockAdeccoUrl + "/addContacts";

        HttpEntity<String> post = new HttpEntity<>(data, headers);
        ResponseEntity<Void> response = restTemplate.exchange(postUrl, HttpMethod.POST, post, Void.class);

        return response.getStatusCode().value();
    }

    private HttpHeaders createBasicAuthHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        final String plainCreds = username + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }

    public void enableRequestRecorder() throws IOException {
        URL url = new URL(mockAdeccoUrl + "/logger/enableRequestRecorder");
        log.info("enableRequestRecorder-mock_url:" + url.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        if (httpURLConnection.getResponseCode() != 200) {
            throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
        }
    }

    public void disableRequestRecorder() throws IOException {
        URL url = new URL(mockAdeccoUrl + "/logger/disableRequestRecorder");
        log.info("disableRequestRecorder-mock_url:" + url.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        if (httpURLConnection.getResponseCode() != 200) {
            throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
        }
    }
}
