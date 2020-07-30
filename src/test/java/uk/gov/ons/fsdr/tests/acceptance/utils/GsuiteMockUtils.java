package uk.gov.ons.fsdr.tests.acceptance.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.common.dto.HqJobRole;
import uk.gov.ons.fsdr.tests.acceptance.exceptions.MockInaccessibleException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
public class GsuiteMockUtils {
  @Value("${gsuite.baseUrl}")
  private String mockGsuiteUrl;

  public void clearMock() throws IOException {
    URL url = new URL(mockGsuiteUrl + "messages/reset");
    log.info("clear-mock_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("DELETE");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }

  public String[] getRecords() {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockGsuiteUrl + "messages/";
    log.info("getRecords-mock_url:" + url);
    ResponseEntity<String[]> responseEntity;
    responseEntity = restTemplate.getForEntity(url, String[].class);
    return responseEntity.getBody();
  }

  public String[] getRecords(String email) {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockGsuiteUrl + "messages/" + email;
    log.info("getRecords-mock_url:" + url);
    ResponseEntity<String[]> responseEntity;
    responseEntity = restTemplate.getForEntity(url, String[].class);
    return responseEntity.getBody();
  }

  public String[] getGroups(String employeeId) {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockGsuiteUrl + "groups/" + employeeId;
    log.info("getRecords-mock_url:" + url);
    ResponseEntity<String[]> responseEntity;
    responseEntity = restTemplate.getForEntity(url, String[].class);
    return responseEntity.getBody();
  }

  public void addRoleId(String employeeId , String roleId) {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockGsuiteUrl + "addRoleId";
    log.info("getRecords-mock_url:" + url);
    HqJobRole jobRole = new HqJobRole(employeeId, roleId);
    HttpEntity<HqJobRole> role = new HttpEntity<>(jobRole);
    System.out.println(jobRole.getUniqueRoleId());
    restTemplate.postForEntity(url, role, ResponseEntity.class);
  }

}
