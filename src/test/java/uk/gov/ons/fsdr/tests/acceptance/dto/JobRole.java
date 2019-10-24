package uk.gov.ons.fsdr.tests.acceptance.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private Long id;

    private String uniqueRoleId;

    private String jobRole;

    private String jobRoleType;

    private String lineManagerFirstName;

    private String lineManagerSurname;

    private String areaLocation;

    @JsonIgnore
    private String uniqueEmployeeId;

    private LocalDate operationalEndDate;

    private String jobRoleShort;

}
