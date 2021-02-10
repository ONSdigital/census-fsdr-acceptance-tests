package uk.gov.ons.fsdr.tests.acceptance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@PropertySource("classpath:application.properties")
@Component
public class RoleIdCheck {

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${roleIdFilePath}")
  private String roleIdFilePath;

  public List<String> checkGsuiteLookup(List<String> roleIds) throws Exception {
    String sql = "SELECT role_id_type FROM gsuite.group_lookup where ? like role_id_type;";

    return checkRoleIds(roleIds, sql, "GSUITE");
  }

  public List<String> checkLwsLookup(List<String> roleIds) throws Exception {
    String sql = "SELECT region from lws.region_code_lookup where region_code = ?";

    return checkRoleIds(roleIds, sql, "LWS");
  }

  private List<String> checkRoleIds(List<String> roleIds, String sql, String test) throws Exception {
    PreparedStatement stmt = null;
    List<String> errors = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection(
        url, username, password)) {
      if (conn != null) {
        System.out.println("Connected to the database!");
        stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        switch(test) {
          case "GSUITE":
            gsuiteCheck(roleIds, stmt, errors);
            break;
          case "LWS":
            lwsCheck(roleIds, stmt, errors);
             break;
        }
      } else {
        System.out.println("Failed to make connection!");
      }
    } finally {
      try {
        if (stmt != null)
          stmt.close();
      } catch (SQLException se) {
        System.out.println("oops");
      } // do nothing
    }
    return errors;
  }

  private void gsuiteCheck(List<String> roleIds, PreparedStatement stmt, List<String> errors) throws SQLException {
    for(String roleId : roleIds) {
      System.out.println(roleId);
      stmt.setString(1,roleId.stripLeading());
      ResultSet results = stmt.executeQuery();

      int totalRows = getRowCount(results);
      if(totalRows == 0) {
        errors.add(roleId + "|NO_MATCH");
      } else if(totalRows > 1) {
        StringBuilder types = new StringBuilder(roleId + "|");
        while(results.next()) {
          types.append(results.getString("role_id_type")).append(",");
        }
        errors.add(types.substring(0, types.length() - 1));
      }
    }
  }

  private void lwsCheck(List<String> roleIds, PreparedStatement stmt, List<String> errors) throws SQLException {
    for(String roleId : roleIds) {
      String regionCode;
      if (roleId.startsWith("X")) {
        regionCode = roleId.stripLeading().substring(0, 6);
      } else {
        regionCode = roleId.stripLeading().substring(3, 6);
      }
      stmt.setString(1,regionCode);
      ResultSet results = stmt.executeQuery();

      int totalRows = getRowCount(results);
      if(totalRows != 1) {
        errors.add(roleId + "|NO_MATCH");
      }
    }
  }

  public List<String> getRoleIds() throws IOException, URISyntaxException {
    List<String> result;
    try (Stream<String> lines = Files.lines(Paths.get(new URI(roleIdFilePath)))) {
      result = lines.collect(Collectors.toList());
    }

    return result;
  }

  private int getRowCount(ResultSet results) throws SQLException {
    results.last();
    int totalRows = results.getRow();
    results.beforeFirst();
    return totalRows;
  }
}
