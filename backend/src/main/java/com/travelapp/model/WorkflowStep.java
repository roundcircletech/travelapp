package com.travelapp.model;

import java.util.Map;
import java.util.HashMap;

public class WorkflowStep {
    private String id;
    private String name;
    private String description;
    private StepStatus status;
    private boolean isCompleted;
    private Map<String, Object> metadata;
    private String warning; // New: Advisory Warning Message
    private String alternative; // New: AI Suggested Alternative

    public WorkflowStep() {
        this.metadata = new HashMap<>();
    }

    public WorkflowStep(String id, String name, String description, StepStatus status, boolean isCompleted,
            Map<String, Object> metadata) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.isCompleted = isCompleted;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    public WorkflowStep(String id, String name, String description, StepStatus status, boolean isCompleted,
            Map<String, Object> metadata, String warning, String alternative) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.isCompleted = isCompleted;
        this.metadata = metadata != null ? metadata : new HashMap<>();
        this.warning = warning;
        this.alternative = alternative;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getAlternative() {
        return alternative;
    }

    public void setAlternative(String alternative) {
        this.alternative = alternative;
    }

    public enum StepStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        SKIPPED
    }
}
