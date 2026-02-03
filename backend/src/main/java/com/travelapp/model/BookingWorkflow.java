package com.travelapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "workflows")
public class BookingWorkflow {

    @Id
    private String id;
    private String agentId;
    private String customerName;
    private String customerEmail;
    private String source;
    private String destination;
    private List<WorkflowStep> steps;
    private boolean isFinished;
    private java.time.LocalDate travelDate;

    public BookingWorkflow() {
    }

    public BookingWorkflow(String id, String agentId, String customerName, String customerEmail, String source, String destination, List<WorkflowStep> steps, boolean isFinished, java.time.LocalDate travelDate) {
        this.id = id;
        this.agentId = agentId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.source = source;
        this.destination = destination;
        this.steps = steps;
        this.isFinished = isFinished;
        this.travelDate = travelDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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

    public List<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public java.time.LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(java.time.LocalDate travelDate) {
        this.travelDate = travelDate;
    }
}
