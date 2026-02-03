package com.travelapp.model;

import java.util.List;

public class ParsedItinerary {

    private String title;
    private String customerEmail;
    private List<WorkflowStep> steps;
    private java.time.LocalDate travelDate;
    private String source;
    private String destination;

    public ParsedItinerary() {
    }

    public ParsedItinerary(String title, String customerEmail, List<WorkflowStep> steps, java.time.LocalDate travelDate, String source, String destination) {
        this.title = title;
        this.customerEmail = customerEmail;
        this.steps = steps;
        this.travelDate = travelDate;
        this.source = source;
        this.destination = destination;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }

    public java.time.LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(java.time.LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
