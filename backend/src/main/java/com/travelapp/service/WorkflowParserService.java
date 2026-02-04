package com.travelapp.service;

import com.travelapp.model.WorkflowStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WorkflowParserService {

    @Autowired
    private LlmService llmService;

    public com.travelapp.model.ParsedItinerary parseItinerary(String text) {
        // Try LLM first
        com.travelapp.model.ParsedItinerary llmResult = llmService.generateItinerary(text);
        if (llmResult != null && llmResult.getSteps() != null && !llmResult.getSteps().isEmpty()) {
            return llmResult;
        }

        // Fallback to Heuristics if LLM fails or no key
        List<WorkflowStep> steps = new ArrayList<>();
        String lowerText = text.toLowerCase();

        // Generate a basic title for fallback
        String title = "Itinerary Request";
        if (text.length() > 30) {
            title = text.substring(0, 27) + "...";
        } else if (!text.isEmpty()) {
            title = text;
        }

        // Heuristic 1: Detect Flight request
        if (lowerText.contains("flight") || lowerText.contains("fly")) {
            steps.add(new WorkflowStep(
                    "step-" + System.nanoTime() + "-1",
                    "Flight Booking",
                    extractDetails(text, "flight to ([a-zA-Z\\s]+)", "Book flight"),
                    WorkflowStep.StepStatus.PENDING,
                    false,
                    new java.util.HashMap<>()));
        }

        // Heuristic 2: Detect Hotel request
        if (lowerText.contains("hotel") || lowerText.contains("stay")) {
            steps.add(new WorkflowStep(
                    "step-" + System.nanoTime() + "-2",
                    "Hotel Booking",
                    extractDetails(text, "hotel in ([a-zA-Z\\s]+)", "Book hotel accommodation"),
                    WorkflowStep.StepStatus.PENDING,
                    false,
                    new java.util.HashMap<>()));
        }

        // Heuristic 3: Detect Cab/Transfer
        if (lowerText.contains("cab") || lowerText.contains("taxi") || lowerText.contains("transfer")) {
            steps.add(new WorkflowStep(
                    "step-" + System.nanoTime() + "-3",
                    "Transfer Arrangement",
                    "Arrange local transportation",
                    WorkflowStep.StepStatus.PENDING,
                    false,
                    new java.util.HashMap<>()));
        }

        // Always add Payment and Finalize at the end
        steps.add(new WorkflowStep(
                "step-" + System.nanoTime() + "-99",
                "Payment & Finalize",
                "Complete payment and send itinerary",
                WorkflowStep.StepStatus.PENDING,
                false,
                new java.util.HashMap<>()));

        return new com.travelapp.model.ParsedItinerary(title, null, steps, null, null, null);
    }

    private String extractDetails(String text, String regex, String defaultValue) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return defaultValue + " " + matcher.group(0); // e.g. "Book flight flight to London"
        }
        return defaultValue;
    }
}
