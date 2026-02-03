package com.travelapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelAdvisory {
    private String id;
    private String sourceCountry;
    private String destinationCountry;
    private String type; // e.g., DIRECT_FLIGHT_BAN
    private String severity;
    private String message;
    private String timestamp;
}
