package uk.gov.ons.fsdr.tests.acceptance.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseList;
import uk.gov.ons.fsdr.tests.acceptance.exceptions.MockInaccessibleException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Component

public final class MockUtils {

  @Value("${addeco.baseUrl}")
  private String mockAdeccoUrl;

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  public void clearMock() throws IOException {
    URL url = new URL(mockAdeccoUrl + "mock/reset");
    log.info("clear-mock_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }

  public void cleardb() throws Exception {
    Statement stmt = null;
    try (Connection conn = DriverManager.getConnection(
        url, username, password)) {

      if (conn != null) {
        System.out.println("Connected to the database!");
        stmt = conn.createStatement();
        String sql = "DELETE FROM action_indicator";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM device ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM device_history ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM job_role ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM job_role_history ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM employee ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM employee_history ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM request_log ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM update_state ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM user_authentication ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM gsuite ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM service_now ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM lws ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM xma ";
        stmt.executeUpdate(sql);
//         sql = "DELETE FROM adecco ";
//         stmt.executeUpdate(sql);
      } else {
        System.out.println("Failed to make connection!");
      }

    } finally {
      // finally block used to close resources
      try {
        if (stmt != null)
          stmt.close();
      } catch (SQLException se) {
      } // do nothing
    }

  }

  public void enableRequestRecorder() throws IOException {
    URL url = new URL(mockAdeccoUrl + "mock/enable");
    log.info("enableRequestRecorder-mock_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }

  public void disableRequestRecorder() throws IOException {
    URL url = new URL(mockAdeccoUrl + "mock/disable");
    log.info("disableRequestRecorder-mock_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }

}
