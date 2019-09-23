package uk.gov.ons.fsdr.tests.acceptance.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseList;
import uk.gov.ons.fsdr.tests.acceptance.dto.Employee;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public final class FsdrUtils {

  @Value("${service.fsdrservice.url}")
  private String fsdrServiceUrl;

  @Value("${service.fsdrservice.username}")
  private String fsdrServiceUsername;

  @Value("${service.fsdrservice.password}")
  private String fsdrServicePassword;

  @Value("${addeco.baseUrl}")
  private String mockAdeccoUrl;

  public void ingestAdecco() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/adeccoIngest");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Adecco ingest");
      throw new RuntimeException();
    }
  }

  public ResponseEntity<AdeccoResponseList> getEmployeeBySource(String source) {
    RestTemplate restTemplate = new RestTemplate();

    String postHit = mockAdeccoUrl + "/getResponse";

    ResponseEntity<AdeccoResponseList> results = restTemplate.exchange(postHit, HttpMethod.GET,null, AdeccoResponseList.class);

    return results;
  }

  public ResponseEntity<AdeccoResponseList> getEmployeeById(String employeeId) {
    RestTemplate restTemplate = new RestTemplate();
    AdeccoResponseList adeccoResponseList = new AdeccoResponseList();

    String postHit = mockAdeccoUrl + "/getEmployeeId" + employeeId;

    ResponseEntity<AdeccoResponseList> results = restTemplate.exchange(postHit, HttpMethod.GET,null, AdeccoResponseList.class);

    return results;
  }
}
