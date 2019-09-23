package uk.gov.ons.fsdr.tests.acceptance.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseList;
import uk.gov.ons.fsdr.tests.acceptance.exceptions.MockInaccessibleException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Slf4j
@Component
public final class AdeccoMockUtils {

    @Value("${addeco.baseUrl}")
    private String mockAdeccoUrl;

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
        RestTemplate restTemplate = new RestTemplate();
        String url = mockAdeccoUrl + "/records";
        log.info("getRecords-mock_url:" + url);
        ResponseEntity<AdeccoResponseList> responseEntity;
        responseEntity = restTemplate.getForEntity(url, AdeccoResponseList.class);
        return responseEntity.getBody();
    }

    public void addUsersAdecco(List<AdeccoResponse> adeccoResponseList) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = createBasicAuthHeaders("user", "password");

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<AdeccoResponse>> response = new HttpEntity<>(adeccoResponseList, headers);

        String postUrl = mockAdeccoUrl + "/postResponse";

        restTemplate.exchange(postUrl, HttpMethod.POST, response, AdeccoResponseList.class);
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
        URL url = new URL(mockAdeccoUrl + "/enableLogger");
        log.info("enableRequestRecorder-mock_url:" + url.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        if (httpURLConnection.getResponseCode() != 200) {
            throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
        }
    }

    public void disableRequestRecorder() throws IOException {
        URL url = new URL(mockAdeccoUrl + "/disableLogger");
        log.info("disableRequestRecorder-mock_url:" + url.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        if (httpURLConnection.getResponseCode() != 200) {
            throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
        }
    }
}
