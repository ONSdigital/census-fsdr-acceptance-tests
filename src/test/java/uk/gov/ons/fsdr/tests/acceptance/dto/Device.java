package uk.gov.ons.fsdr.tests.acceptance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Device {

    String deviceId;

    private String fieldDevicePhoneNumber;

    private String uniqueEmployeeId;

    private String deviceType;

}
