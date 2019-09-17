package uk.gov.ons.fsdr.tests.acceptance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.ons.fsdr.tests.acceptance.utils.GSuiteStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

  private String uniqueEmployeeId;

  private String firstName;

  private String surname;

  private String preferredName;

  private String address1;

  private String address2;

  private String town;

  private String county;

  private String postcode;

  private String country;

  private String personalEmailAddress;

  private String onsId;

  private String telephoneNumberContact1;

  private String telephoneNumberContact2;

  private String emergencyContactFirstName;

  private String emergencyContactSurname;

  private String emergencyContactMobileNo;

  private String emergencyContactFirstName2;

  private String emergencyContactSurname2;

  private String emergencyContactMobileNo2;

  @Getter
  private Boolean welshLanguageSpeaker;

  private String anyLanguagesSpoken;

  private String mobility;

  @Getter
  private Boolean mobileStaff;

  private String idBadgeNo;

  private String workRestrictions;

  private Double weeklyHours;

  private String reasonableAdjustments;

  private LocalDate contractStartDate;

  private LocalDate contractEndDate;

  private String status;

  @Getter
  private Boolean currentCivilServant;

  @Getter
  private Boolean previousCivilServant;

  @Getter
  private Boolean civilServicePensionRecipient;

  private LocalDate dob;

  private String drivingInformation;

  private String age;

  private String ethnicity;

  private String disability;

  private String ethnicityNotes;

  private String disabilityNotes;

  private String nationality;

  private String gender;
  
  private String sexualOrientation;

  private String religion;

  private String sexualOrientationNotes;

  private String religionNotes;

  private String venueAddress;

  private String hrCaseData;

  private String hrIndividualContract;

  private LocalDateTime ingestDate;

  private String serviceNowUserId;

  private String airwatchId;

  private GSuiteStatus gsuite;

  private String areaGroup;

  private String coordGroup;

  private String orgUnit;

  private String xmaId;

  private String dataSource;

  private boolean lwsCreated;

  private String gsuiteId;

  private LocalDate assignmentEndDate;

  private Set<Device> devices;

  private Set<JobRole> jobRoles;

}
