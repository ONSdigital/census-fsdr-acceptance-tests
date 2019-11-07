package uk.gov.ons.fsdr.tests.acceptance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.tests.acceptance.exceptions.MockInaccessibleException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
public class SnowMockUtils {
    @Value("${snow.baseUrl}")
    private String mockSnowUrl;

    public void clearMock() throws IOException {
        URL url = new URL(mockSnowUrl + "messages/reset");
        log.info("clear-mock_url:" + url.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("DELETE");
        if (httpURLConnection.getResponseCode() != 200) {
            throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
        }
    }

    public  String[] getRecords() {
        RestTemplate restTemplate = new RestTemplate();
        String url = mockSnowUrl + "messages";
        log.info("getRecords-mock_url:" + url);
        ResponseEntity<String[]> responseEntity;
        responseEntity = restTemplate.getForEntity(url, String[].class);
        return responseEntity.getBody();
    }

    public String[] getRecords(String sysId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = mockSnowUrl + "messages/" + sysId;
        log.info("getRecords-mock_url:" + url);
        ResponseEntity<String[]> responseEntity;
        responseEntity = restTemplate.getForEntity(url, String[].class);
        return responseEntity.getBody();
    }




}
