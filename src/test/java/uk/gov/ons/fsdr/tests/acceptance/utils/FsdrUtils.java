package uk.gov.ons.fsdr.tests.acceptance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.tests.acceptance.dto.Employee;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

  @Autowired
  private RestTemplate restTemplate;

  public void ingestAdecco() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/adeccoIngest");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Adecco ingest");
      throw new RuntimeException();
    }
  }

  public List<Employee> getAllEmployeesBySource(String source) {
    List<Employee> results;

     String postHit = fsdrServiceUrl + "/getAllAdeccoEmployees/" + source;

     results = (List<Employee>) restTemplate.getForObject(postHit, Employee.class);

    return results;
  }
}
