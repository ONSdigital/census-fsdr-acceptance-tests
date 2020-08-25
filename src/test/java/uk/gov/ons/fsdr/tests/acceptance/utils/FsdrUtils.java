package uk.gov.ons.fsdr.tests.acceptance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.tests.acceptance.dto.Employee;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Component
public final class FsdrUtils {

  @Value("${service.fsdrservice.url}")
  private String fsdrServiceUrl;

  @Value("${service.fsdrservice.username}")
  private String fsdrServiceUsername;

  @Value("${service.fsdrservice.password}")
  private String fsdrServicePassword;

  private void addBasicAuthentication(HttpURLConnection httpURLConnection) {
    String encoded = Base64.getEncoder()
        .encodeToString((fsdrServiceUsername + ":" + fsdrServicePassword).getBytes(StandardCharsets.UTF_8));
    httpURLConnection.setRequestProperty("Authorization", "Basic " + encoded);
  }

  private HttpHeaders createHeaders() {
    return new HttpHeaders() {
      {
        String encoded = Base64.getEncoder()
            .encodeToString((fsdrServiceUsername + ":" + fsdrServicePassword).getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encoded;
        set("Authorization", authHeader);
      }
    };
  }

  public void ingestAdecco() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/adeccoIngest");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Adecco ingest" + httpURLConnection.getResponseCode()
              + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void ingestGranby() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/logistics");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate granby extract" + httpURLConnection.getResponseCode()
              + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void ingestRunFSDRProcess() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/createActions");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Adecco ingest" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public ResponseEntity<Employee> retrieveEmployee(String id) {
    RestTemplate restTemplate = new RestTemplate();

    String url = fsdrServiceUrl + "/fieldforce/byId/" + id;
    ResponseEntity<Employee> employeeEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders()), Employee.class);
    return employeeEntity;
  }

  public void rcaExtract() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/rca");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) { log.error("failed to initiate rca extract" + httpURLConnection.getResponseCode()
              + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public static String getLastRecord(String[] records, String search) {
    return Arrays.stream(records).filter(x -> x.contains(search))
            .reduce((first, second) -> second).get();
  }

  public void ingestHqCsv() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/hq/ingest");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate HQ ingest" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void sendHqActions() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/hq/actions");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate HQ ingest" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void retrieveHqRoleIds() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/hq/roleId");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Hq roleId retrievl" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void sendDeviceAllocation() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/deviceAllocation");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate device allocation sending" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }


  public void ingestChromebooks() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/devices/startChromebook");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Adecco ingest" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

}
