package uk.gov.ons.fsdr.tests.acceptance.release2.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.tests.acceptance.dto.Employee;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoMockUtils;
import uk.gov.ons.fsdr.tests.acceptance.utils.AdeccoPeopleFactory;
import uk.gov.ons.fsdr.tests.acceptance.utils.FsdrUtils;

public class ReleaseTwoSteps {
  private AdeccoResponse adeccoResponse1;

  private AdeccoResponse adeccoResponse2;

  @Autowired
  private AdeccoMockUtils adeccoUtils;

  @Autowired
  private FsdrUtils fsdrUtils;

  private Employee fsdrEmployee;

  // spring.datasource.url:
  // jdbc:postgresql://localhost/postgres?currentSchema=fsdr
  // spring.datasource.driver-class-name: org.postgresql.Driver
  // spring.datasource.schema: classpath:/schema.sql
  // spring.datasource.continue-on-error: false
  // spring.datasource.username: postgres
  // spring.datasource.password: postgres
  // spring.datasource.initialization-mode: always

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  private Map<String, String> actionMap = new HashMap<String, String>();

  private int jobRoleQty;

  private UUID employeeId;
  
  private String person;

  @Before
  public void beforeEach() throws Exception {
    adeccoUtils.clearMock();
    cleardb();
    actionMap.clear();
  }

  @Given("an employee {string}")
  public void adecco_has_an_employee(String person) {
    this.person = person;
    employeeId = UUID.randomUUID();
    adeccoResponse1 = AdeccoPeopleFactory.buildFransicoBuyo(employeeId);
    adeccoResponse1.getResponseJob().setJobRole("CA12");
    adeccoResponse1.getResponseJob().setRoleId("CA12-001");

    
  }

  @Given("has {string} Job Roles")
  public void has_Job_Roles(String qty) {
    jobRoleQty = Integer.parseInt(qty);
    if(jobRoleQty==2) {
      adeccoResponse2 = AdeccoPeopleFactory.buildFransicoBuyo(employeeId);
      adeccoResponse2.getResponseJob().setJobRole("CA13");
      adeccoResponse2.getResponseJob().setRoleId("CA13-001");

    }
  }
  
  @Given("CR-Status for Job Role {int} is {string}")
  public void cr_Status_for_Job_Role_is(Integer i, String crStatus) {
    if (Strings.isBlank(crStatus)) {
      return;
    }
    if (i==1) {
      adeccoResponse1.setCrStatus(crStatus);
    }
    if (i==2) {
      adeccoResponse2.setCrStatus(crStatus);
    }
  }

  @Given("Assignment-Status for Job Role {int} is {string}")
  public void assignment_Status_for_Job_Role_is(Integer i, String assignmentStatus) {
    if (Strings.isBlank(assignmentStatus)) {
      return;
    }
    if (i==1) {
      adeccoResponse1.setStatus(assignmentStatus);
    }
    if (i==2) {
      adeccoResponse2.setStatus(assignmentStatus);
    }
  }

  @Given("is added to Adecco")
  public void is_added_to_Adecco() {
    adeccoUtils.addUsersAdecco(Collections.singletonList(adeccoResponse1));
    if (jobRoleQty==2)
      adeccoUtils.addUsersAdecco(Collections.singletonList(adeccoResponse2));
  }

  @When("FSDR system pulls updates from Adecco")
  public void fsdr_system_pulls_updates_from_Adecco() throws IOException {
    fsdrUtils.ingestAdecco();
  }

  @When("FSDR system runs FSDR-Process")
  public void fsdr_system_runs_FSDR_Process() throws IOException {
    fsdrUtils.ingestRunFSDRProcess();
  }

  @Then("the employee whether it is updated is {string}")
  public void the_employee_whether_it_is_updated_is(String isReceivedFromAdecco) throws Exception {
    fsdrEmployee = fsdrUtils.retrieveEmployee(adeccoResponse1.getResponseContact().getEmployeeId());
    assertEquals("ADECCO", fsdrEmployee.getDataSource());
  }

  @Then("isActive is {string}")
  public void isactive_is(String isActiveExpected) {
    String status = fsdrEmployee.getStatus();
    boolean isActiveActual = "ACTIVE".equals(status);
    assertTrue(Boolean.valueOf(isActiveExpected) == isActiveActual);
  }

  @Then("whether Interface Action Row Exists is {string}")
  public void whether_Interface_Action_Row_Exists_is(String actionRowExists) throws Exception {
    retrieveActionsForEmployee(fsdrEmployee.getUniqueEmployeeId());
    assertTrue(Boolean.valueOf(actionRowExists) == (actionMap.size()>0));
  }

  @Then("GSuite Action is {string}")
  public void gsuite_Action_is(String gsuiteAction) {
    if (Strings.isBlank(gsuiteAction)) gsuiteAction=null;
    assertEquals(gsuiteAction, actionMap.get("guite"));
  }

  @Then("XMA Action    is {string}")
  public void xma_Action_is(String xmaAction) {
    if (Strings.isBlank(xmaAction)) xmaAction=null;
   assertEquals(xmaAction, actionMap.get("xma"));
  }

  @Then("LWS Action    is {string}")
  public void lws_Action_is(String lwsAction) {
    if (Strings.isBlank(lwsAction)) lwsAction=null;
    assertEquals(lwsAction, actionMap.get("lws"));
  }

  @Then("Granby Action is {string}")
  public void granby_Action_is(String granbyAction) {
    if (Strings.isBlank(granbyAction)) granbyAction=null;
    assertEquals(granbyAction, actionMap.get("granby"));
  }

  @Then("SNOW Action   is {string}")
  public void snow_Action_is(String snowAction) {
    if (Strings.isBlank(snowAction)) snowAction=null;
    assertEquals(snowAction, actionMap.get("snow"));
  }

  private void retrieveActionsForEmployee(String id) throws Exception {
    System.out.println("CLEARDB" + url + username + password);
    String sql = "SELECT unique_employee_id, xma_status, granby_status, lone_worker_solution_status, service_now_status, gsuite_status"
        +
        "  FROM fsdr.action_indicator WHERE unique_employee_id = ?;";
    Statement stmt = null;
    try (Connection conn = DriverManager.getConnection(
        url, username, password);
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, id);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        actionMap.put("guite", rs.getString("gsuite_status"));
        actionMap.put("xma", rs.getString("xma_status"));
        actionMap.put("lws", rs.getString("lone_worker_solution_status"));
        actionMap.put("granby", rs.getString("granby_status"));
        actionMap.put("snow", rs.getString("service_now_status"));
      }
    }
  }

  private void cleardb() throws Exception {
    System.out.println("CLEARDB" + url + username + password);
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
        sql = "DELETE FROM user_authentication ";
        stmt.executeUpdate(sql);

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

}
