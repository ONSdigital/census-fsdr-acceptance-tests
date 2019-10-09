package uk.gov.ons.fsdr.tests.acceptance.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseContact;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseJob;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseWorker;

public class AdeccoPeopleFactory {
  
  public static AdeccoResponse buildFransicoBuyo(UUID uuid) {
    AdeccoResponseJob job = AdeccoResponseJob.builder()
        .build();
    AdeccoResponseContact contact = AdeccoResponseContact.builder()
        .employeeId(uuid.toString())
        .firstName("Fransico")
        .lastName("Buyo")
        .build();
    AdeccoResponseWorker worker = AdeccoResponseWorker.builder().employeeId(uuid.toString()).build();
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
