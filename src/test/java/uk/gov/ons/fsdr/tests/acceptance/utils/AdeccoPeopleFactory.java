package uk.gov.ons.fsdr.tests.acceptance.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseContact;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseJob;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseWorker;

public class AdeccoPeopleFactory {
  
  public static AdeccoResponse buildFransicoBuyo(String uuid) {
    AdeccoResponseJob job = AdeccoResponseJob.builder()
        .build();
    AdeccoResponseContact contact = AdeccoResponseContact.builder()
        .employeeId(uuid)
        .firstName("Fransico")
        .lastName("Buyo")
        .welshLanguageSpeaker("NO")
        .languages("None")
        .mobileStaff("no")
        .reasonableAdjustments("None")
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
        .drivingInfo("None")
        .build();
    AdeccoResponseWorker worker = AdeccoResponseWorker.builder().employeeId(uuid).build();
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

}
