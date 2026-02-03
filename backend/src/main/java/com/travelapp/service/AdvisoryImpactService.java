package com.travelapp.service;

import com.travelapp.model.Advisory;
import com.travelapp.model.BookingWorkflow;
import com.travelapp.model.WorkflowStep;
import com.travelapp.repository.BookingWorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;

@Service
public class AdvisoryImpactService {

    @Autowired
    private BookingWorkflowRepository workflowRepository;

    @Autowired
    private WorkflowCloneService cloneService;

    @Autowired
    private AdvisoryService advisoryService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Autowired
    private com.travelapp.service.LlmService llmService;

    public void processNewAdvisory(Advisory advisory) {
        LocalDate dateLimit = LocalDate.now().minusDays(1);
        List<BookingWorkflow> futureBookings = workflowRepository.findByTravelDateAfter(dateLimit);

        for (BookingWorkflow booking : futureBookings) {
            if (advisoryService.checkAdvisoryImpact(booking, advisory)) {

                BookingWorkflow clonedBooking = cloneService.cloneForAdvisoryReview(booking);
                clonedBooking.setCustomerName(booking.getCustomerName() + " (Advisory Review)");

                // Generate Agent Script (expecting JSON now)
                String llmResponse = advisoryService.generateAgentAdvisoryScript(booking, advisory);
                String agentScript = "";
                String estimatedCost = "Unknown";
                String estimatedTimeDelay = "Unknown";

                try {
                    String cleaned = llmResponse.replace("```json", "").replace("```", "").trim();
                    com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(cleaned);
                    agentScript = root.has("script") ? root.get("script").asText() : llmResponse;
                    estimatedCost = root.has("estimatedCost") ? root.get("estimatedCost").asText() : "Unknown";
                    estimatedTimeDelay = root.has("estimatedTimeDelay") ? root.get("estimatedTimeDelay").asText() : "Unknown";
                } catch (Exception e) {
                    System.err.println("Failed to parse Advisory JSON: " + e.getMessage());
                    agentScript = llmResponse; // Fallback
                }

                WorkflowStep guidanceStep = new WorkflowStep();
                guidanceStep.setId("advisory-guidance-" + System.nanoTime());
                guidanceStep.setName("Advisory Impact Review");
                guidanceStep.setDescription("Review new advisory execution with customer. Estimated Cost: " + estimatedCost + ", Delay: " + estimatedTimeDelay);
                guidanceStep.setStatus(WorkflowStep.StepStatus.PENDING);
                guidanceStep.setCompleted(false);

                HashMap<String, Object> metadata = new HashMap<>();
                metadata.put("advisoryId", advisory.getId());
                metadata.put("agentScript", agentScript);
                metadata.put("estimatedCost", estimatedCost);
                metadata.put("estimatedTimeDelay", estimatedTimeDelay);
                metadata.put("isAdvisoryTask", true);
                guidanceStep.setMetadata(metadata);

                if (clonedBooking.getSteps() == null) {
                    clonedBooking.setSteps(new java.util.ArrayList<>());
                }
                clonedBooking.getSteps().add(0, guidanceStep);

                workflowRepository.save(clonedBooking);

                // Send Email
                String emailBody = "Dear " + booking.getCustomerName() + ",\n\n"
                        + "An important travel advisory has been issued that affects your trip.\n"
                        + "Advisory: " + advisory.getDescription() + "\n\n"
                        + "Impact Analysis:\n"
                        + "Estimated Cost Impact: " + estimatedCost + "\n"
                        + "Estimated Delay: " + estimatedTimeDelay + "\n\n"
                        + "Our agent will be in touch shortly to discuss options.\n\n"
                        + "Sincerely,\nTravel App Team";

                String recipient = booking.getCustomerEmail() != null && !booking.getCustomerEmail().isEmpty() ? booking.getCustomerEmail() : "customer@example.com";
                notificationService.sendEmail(recipient, "Travel Advisory Alert: " + advisory.getSourceCountry() + " to " + advisory.getTargetCountry(), emailBody);
            }
        }
    }

    public String handleCustomerResponse(String workflowId, String responseText) {
        java.util.Optional<BookingWorkflow> optionalWorkflow = workflowRepository.findById(workflowId);
        if (optionalWorkflow.isEmpty()) {
            return "Workflow not found";
        }
        BookingWorkflow workflow = optionalWorkflow.get();

        String prompt = "Analyze this customer response to a travel advisory change: \"" + responseText + "\". "
                + "Classify as POSITIVE (accepts changes/proceeds) or NEGATIVE (cancels/rejects). "
                + "Return ONLY the word POSITIVE or NEGATIVE.";

        String sentiment = llmService.fetchLlmResponse(prompt).trim().toUpperCase();
        if (sentiment.contains("POSITIVE")) {
            if (workflow.getSteps() != null && !workflow.getSteps().isEmpty()) {
                WorkflowStep firstStep = workflow.getSteps().get(0);
                if (Boolean.TRUE.equals(firstStep.getMetadata().get("isAdvisoryTask"))) {
                    firstStep.setCompleted(true);
                    firstStep.setStatus(WorkflowStep.StepStatus.COMPLETED);
                }
            }
            workflowRepository.save(workflow);

            String recipient = workflow.getCustomerEmail() != null && !workflow.getCustomerEmail().isEmpty() ? workflow.getCustomerEmail() : "customer@example.com";

            notificationService.sendEmail(recipient, "Trip Updated: " + workflow.getCustomerName(),
                    "Great! We have updated your itinerary based on the advisory changes. Safe travels!");
            return "Positive response processed. Itinerary updated.";
        } else {
            String recipient = workflow.getCustomerEmail() != null && !workflow.getCustomerEmail().isEmpty() ? workflow.getCustomerEmail() : "customer@example.com";
            notificationService.sendEmail(recipient, "Trip Update Acknowledged",
                    "We understand your concern. An agent will contact you to discuss cancellation or alternative options.");
            return "Negative response processed. Agent notified.";
        }
    }
}
