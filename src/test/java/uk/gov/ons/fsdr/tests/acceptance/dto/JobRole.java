package uk.gov.ons.fsdr.tests.acceptance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class JobRole {

  private Long id;

  private String uniqueRoleId;

  private String jobRole;

  private String jobRoleType;

  private String lineManagerFirstName;

  private String lineManagerSurname;

  private String areaLocation;

  private String uniqueEmployeeId;

  private LocalDate operationalEndDate;

  private String jobRoleShort;

  private Boolean active;

  private String crStatus;

  private String assignmentStatus;

  private LocalDate contractStartDate;

  private LocalDate contractEndDate;

}
