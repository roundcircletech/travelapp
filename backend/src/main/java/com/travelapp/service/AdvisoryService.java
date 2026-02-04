package com.travelapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.model.Advisory;
import com.travelapp.model.BookingWorkflow;
import com.travelapp.model.WorkflowStep;
import com.travelapp.repository.AdvisoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvisoryService {

    @Autowired
    private AdvisoryRepository repository;

    @Autowired
    private LlmService llmService;

    @Autowired
    private ObjectMapper objectMapper;

    public boolean validateWorkflow(BookingWorkflow workflow) {
        List<Advisory> advisories = repository.findAll();
        // If no advisories or steps, minimal check
        if (advisories.isEmpty() || workflow.getSteps() == null) {
            return false;
        }

        // Optimized: Only check if steps don't have warnings yet, or maybe re-check
        // every time?
        // For efficiency in this demo, we'll re-check every time GET is called.
        // In prod, cache the result or check hash of steps.
        StringBuilder prompt = new StringBuilder();
        prompt.append(
                "You are a Travel Compliance Officer. Check the following itinerary steps against active Travel Advisories.\n\n");

        prompt.append("ACTIVE ADVISORIES:\n");
        for (Advisory adv : advisories) {
            prompt.append("- [").append(adv.getSeverity()).append("] From ").append(adv.getSourceCountry())
                    .append(" to ").append(adv.getTargetCountry()).append(": ").append(adv.getDescription())
                    .append("\n");
        }

        prompt.append("\nITINERARY STEPS:\n");
        for (WorkflowStep step : workflow.getSteps()) {
            prompt.append("- Step ID: ").append(step.getId()).append(" | Name: ").append(step.getName())
                    .append(" | Desc: ").append(step.getDescription()).append("\n");
        }

        prompt.append("INSTRUCTIONS:\n");
        prompt.append("1. Analyze EVERY step carefully. Identify ANY step that violates an advisory (e.g. flying from banned source to target).\n");
        prompt.append("2. Use geographic knowledge (e.g. Delhi is in India, Shanghai is in China). Map cities to countries explicitly.\n");
        prompt.append("3. For round trips, ensure BOTH outbound and return legs are checked independently.\n");
        prompt.append("4. For EACH violation, provide a 'warning' message and an 'alternative' route.\n");
        prompt.append(
                "5. Return a JSON object mapping Step IDs to violations: { \"step-id\": { \"warning\": \"...\", \"alternative\": \"...\" }, \"step-id-2\": { ... } }.\n");
        prompt.append("6. If a step has NO violation, do NOT include it in the JSON.\n");
        prompt.append("7. If NO violations in entire workflow, return {}.\n");
        prompt.append("Return ONLY valid JSON.");

        String jsonResponse = llmService.fetchLlmResponse(prompt.toString());
        if (jsonResponse == null) {
            return false;
        }

        boolean updated = false;
        try {
            String cleaned = jsonResponse.replace("```json", "").replace("```", "").trim();
            JsonNode root = objectMapper.readTree(cleaned);

            for (WorkflowStep step : workflow.getSteps()) {
                if (root.has(step.getId())) {
                    JsonNode violation = root.get(step.getId());
                    if (violation.has("warning")) {
                        // Only update if changed to avoid unnecessary saves
                        if (!violation.get("warning").asText().equals(step.getWarning())) {
                            step.setWarning(violation.get("warning").asText());
                            updated = true;
                        }
                    }
                    if (violation.has("alternative")) {
                        if (!violation.get("alternative").asText().equals(step.getAlternative())) {
                            step.setAlternative(violation.get("alternative").asText());
                            updated = true;
                        }
                    }
                } else {
                    // Clear warnings if issue resolved (or transient check)
                    // step.setWarning(null);
                    // step.setAlternative(null);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse Advisory LLM response: " + e.getMessage());
        }
        return updated;
    }

    public boolean checkAdvisoryImpact(BookingWorkflow workflow, Advisory advisory) {
        // Simple logic: Check if Source or Target country matches the advisory
        // In a real app, this would be more complex (cities mapping to countries, connection points, etc.)
        // Re-using the logic: if we can match countries, it's impacted.

        boolean sourceMatch = isLocationInCountry(workflow.getSource(), advisory.getSourceCountry())
                || isLocationInCountry(workflow.getSource(), advisory.getTargetCountry());

        boolean destMatch = isLocationInCountry(workflow.getDestination(), advisory.getSourceCountry())
                || isLocationInCountry(workflow.getDestination(), advisory.getTargetCountry());

        return sourceMatch || destMatch;
    }

    private boolean isLocationInCountry(String location, String country) {
        if (location == null || country == null) {
            return false;
        }
        // Basic heuristic for demo: String contains
        return location.trim().equalsIgnoreCase(country.trim())
                || location.toLowerCase().contains(country.toLowerCase());
    }

    public String generateAgentAdvisoryScript(BookingWorkflow workflow, Advisory advisory) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a Travel Strategy Consultant. A new Travel Advisory has been issued that affects a customer's booking.\n\n");

        prompt.append("ADVISORY DETAILS:\n");
        prompt.append("- Severity: ").append(advisory.getSeverity()).append("\n");
        prompt.append("- From: ").append(advisory.getSourceCountry()).append(" To: ").append(advisory.getTargetCountry()).append("\n");
        prompt.append("- Description: ").append(advisory.getDescription()).append("\n\n");

        prompt.append("BOOKING DETAILS:\n");
        prompt.append("- Customer: ").append(workflow.getCustomerName()).append("\n");
        prompt.append("- Route: ").append(workflow.getSource()).append(" to ").append(workflow.getDestination()).append("\n");
        if (workflow.getTravelDate() != null) {
            prompt.append("- Travel Date: ").append(workflow.getTravelDate()).append("\n");
        }

        prompt.append("\nINSTRUCTIONS:\n");
        prompt.append("1. Draft a script for the travel agent to read to the customer.\n");
        prompt.append("2. Explain the implications of the advisory clearly and empathetically.\n");
        prompt.append("3. Highlight potential costs (e.g. cancellation fees, higher fares for rerouting).\n");
        prompt.append("4. Suggest date changes if applicable.\n");
        prompt.append("5. Keep it professional but urgent if severity is HIGH.\n\n");

        prompt.append("Output JSON format: { \"script\": \"...\", \"estimatedCost\": \"$XXX\", \"estimatedTimeDelay\": \"X days/hours\" }");

        return llmService.fetchLlmResponse(prompt.toString());
    }
}
