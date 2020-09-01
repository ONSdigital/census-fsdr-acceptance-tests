package uk.gov.ons.fsdr.tests.acceptance.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseContact;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseJob;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseJobRoleCode;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseWorker;
import uk.gov.ons.fsdr.common.dto.LocalAuthority;
import uk.gov.ons.fsdr.common.dto.LocalAuthorityArea;

import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.adeccoResponseManagers;
import static uk.gov.ons.fsdr.tests.acceptance.steps.AdeccoIngestSteps.sentManagerIds;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.AREA_MANAGER_ROLE_ID_LENGTH;
import static uk.gov.ons.fsdr.tests.acceptance.steps.CommonSteps.COORDINATOR_ROLE_ID_LENGTH;

public class AdeccoPeopleFactory {
  
  public static AdeccoResponse buildFransicoBuyo(String uuid) {
    AdeccoResponseJob job = AdeccoResponseJob.builder()
        .build();
    LocalAuthority localAuthority = LocalAuthority.builder()
        .country("Wales")
        .build();
    LocalAuthorityArea localAuthorityArea = LocalAuthorityArea.builder()
        .localAuthority(localAuthority)
        .build();
    AdeccoResponseContact contact = AdeccoResponseContact.builder()
        .employeeId(uuid)
        .firstName("Fransico")
        .lastName("Buyo")
        .areaLocation("London")
        .addressLine1("123")
        .addressLine2("Fake Street")
        .town("Faketon")
        .county("Fakeside")
        .postcode("FA43 1AB")
        .personalEmail("f.b@email.com")
        .telephoneNo1("0987654321")
        .emergencyContact("James Bouyo")
        .emergencyContactNumber1("02345678901")
        .mobility("10-15 miles")
        .dob("1995-07-20")
        .localAuthorityArea(localAuthorityArea)
        .build();
    AdeccoResponseWorker worker = AdeccoResponseWorker.builder()
        .employeeId(uuid)
        .build();
    AdeccoResponse adeccoResponse = AdeccoResponse.builder()
        .contractStartDate(LocalDate.now().minus(5, ChronoUnit.DAYS).toString())
        .contractEndDate(LocalDate.now().plus(5, ChronoUnit.DAYS).toString())
        .operationalEndDate(LocalDate.now().plus(5, ChronoUnit.DAYS).toString())
        .responseContact(contact)
        .adeccoResponseWorker(worker)
        .responseJob(job)
        .build();
   
    return adeccoResponse;
  }


  public static void buildCoordinatorTypeManager(String roleId, String id) {
    String managerRoleId = roleId.substring(0, COORDINATOR_ROLE_ID_LENGTH);
    if (!sentManagerIds.contains(managerRoleId)) {
      AdeccoResponse managerAdeccoResponse = AdeccoPeopleFactory.buildFransicoBuyo(id);
      managerAdeccoResponse.setContractStartDate("2020-01-01");
      managerAdeccoResponse.setStatus("ASSIGNED");
      managerAdeccoResponse.setCrStatus("ACTIVE");
      managerAdeccoResponse.getResponseContact().setFirstName("Dave");
      managerAdeccoResponse.getResponseContact().setLastName("Davis");
      managerAdeccoResponse.getResponseContact().setTelephoneNo1("0112233445");
      AdeccoResponseJobRoleCode adeccoResponseJobRoleCode = new AdeccoResponseJobRoleCode();
      adeccoResponseJobRoleCode.setRoleId(managerRoleId);
      managerAdeccoResponse.setAdeccoResponseJobRoleCode(adeccoResponseJobRoleCode);
      sentManagerIds.add(managerRoleId);

      adeccoResponseManagers.add(managerAdeccoResponse);
    }
  }

  public static void buildAreaManagerTypeManager(String roleId, String id) {
    String managerRoleId = roleId.substring(0, AREA_MANAGER_ROLE_ID_LENGTH);
    if (!sentManagerIds.contains(managerRoleId)) {
      AdeccoResponse managerAdeccoResponse = AdeccoPeopleFactory.buildFransicoBuyo(id);
      managerAdeccoResponse.setContractStartDate("2020-01-01");
      managerAdeccoResponse.setStatus("ASSIGNED");
      managerAdeccoResponse.setCrStatus("ACTIVE");
      managerAdeccoResponse.getResponseContact().setFirstName("Bob");
      managerAdeccoResponse.getResponseContact().setLastName("Jones");
      managerAdeccoResponse.getResponseContact().setTelephoneNo1("0112233445");
      AdeccoResponseJobRoleCode adeccoResponseJobRoleCode = new AdeccoResponseJobRoleCode();
      adeccoResponseJobRoleCode.setRoleId(managerRoleId);
      managerAdeccoResponse.setAdeccoResponseJobRoleCode(adeccoResponseJobRoleCode);
      sentManagerIds.add(managerRoleId);
      adeccoResponseManagers.add(managerAdeccoResponse);
    }
  }

}
