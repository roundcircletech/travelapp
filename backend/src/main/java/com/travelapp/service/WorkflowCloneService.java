package com.travelapp.service;

import com.travelapp.model.BookingWorkflow;
import com.travelapp.model.WorkflowStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkflowCloneService {

    public BookingWorkflow cloneForAdvisoryReview(BookingWorkflow original) {
        BookingWorkflow clone = new BookingWorkflow();

        // Basic fields
        clone.setId(UUID.randomUUID().toString()); // New ID
        clone.setAgentId(original.getAgentId());
        clone.setCustomerName(original.getCustomerName());
        clone.setCustomerEmail(original.getCustomerEmail());
        clone.setSource(original.getSource());
        clone.setDestination(original.getDestination());
        clone.setTravelDate(original.getTravelDate());
        clone.setFinished(false); // Draft mode

        // Deep copy steps
        List<WorkflowStep> clonedSteps = new ArrayList<>();
        if (original.getSteps() != null) {
            clonedSteps = original.getSteps().stream()
                    .map(this::cloneStep)
                    .collect(Collectors.toList());
        }
        clone.setSteps(clonedSteps);

        return clone;
    }

    private WorkflowStep cloneStep(WorkflowStep original) {
        WorkflowStep step = new WorkflowStep();
        step.setId(UUID.randomUUID().toString()); // New ID for step to avoid conflicts
        step.setName(original.getName());
        step.setDescription(original.getDescription());
        step.setStatus(WorkflowStep.StepStatus.PENDING); // Reset status
        step.setCompleted(false);
        step.setMetadata(original.getMetadata() != null ? new HashMap<>(original.getMetadata()) : new HashMap<>());
        // Warning and Alternative are cleared as they will be re-evaluated
        return step;
    }
}
