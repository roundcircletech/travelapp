package com.travelapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "advisories")
public class Advisory {
    @Id
    private String id;
    private String sourceCountry;
    private String targetCountry;
    private String severity; // HIGH, MEDIUM, LOW
    private String description;

    public Advisory() {
    }

    public Advisory(String sourceCountry, String targetCountry, String severity, String description) {
        this.sourceCountry = sourceCountry;
        this.targetCountry = targetCountry;
        this.severity = severity;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public String getTargetCountry() {
        return targetCountry;
    }

    public void setTargetCountry(String targetCountry) {
        this.targetCountry = targetCountry;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
