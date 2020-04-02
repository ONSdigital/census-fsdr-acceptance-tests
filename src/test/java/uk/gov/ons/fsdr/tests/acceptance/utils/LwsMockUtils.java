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
public class LwsMockUtils {

  @Value("${lws.baseUrl}")
  private String mockLwsUrl;

  public void clearMock() throws IOException {
    URL url = new URL(mockLwsUrl + "messages/reset");
    log.info("clear-mock_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("DELETE");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }

  public String getRecords() {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockLwsUrl + "messages/";
    log.info("getRecords-mock_url:" + url);
    ResponseEntity<String> responseEntity;
    responseEntity = restTemplate.getForEntity(url, String.class);

    System.out.println(responseEntity.getBody());
    return responseEntity.getBody();
  }

  public String[] getRecords(String email) {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockLwsUrl + "messages/" + email;
    log.info("getRecords-mock_url:" + url);
    ResponseEntity<String[]> responseEntity;
    responseEntity = restTemplate.getForEntity(url, String[].class);
    return responseEntity.getBody();
  }

}
