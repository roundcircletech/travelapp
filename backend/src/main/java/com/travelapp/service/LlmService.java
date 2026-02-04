package com.travelapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.model.WorkflowStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public String fetchLlmResponse(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("No AI API Key found. Skipping LLM generation.");
            return null;
        }

        try {
            // Construct Gemini-compatible payload
            Map<String, Object> content = new HashMap<>();
            content.put("parts", new Object[]{Map.of("text", prompt)});

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", new Object[]{content});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String targetUrl = apiUrl;
            if (targetUrl.contains("googleapis.com")) {
                targetUrl += "?key=" + apiKey;
            } else {
                headers.set("Authorization", "Bearer " + apiKey);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            try {
                String response = restTemplate.postForObject(targetUrl, entity, String.class);
                return extractContent(response);
            } catch (Exception e) {
                System.err.println("LLM API Call Failed: " + e.getMessage());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractContent(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.has("candidates") && root.get("candidates").isArray()) {
                JsonNode candidate = root.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")) {
                    return candidate.get("content").get("parts").get(0).get("text").asText();
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return jsonResponse;
    }

    public com.travelapp.model.ParsedItinerary generateItinerary(String userRequest) {
        String prompt = constructPrompt(userRequest);
        String responseText = fetchLlmResponse(prompt);
        if (responseText == null) {
            return null;
        }
        return parseLlmResponse(responseText);
    }

    private String constructPrompt(String userRequest) {
        return "You are an expert travel agent. Analyze this request: \"" + userRequest + "\". "
                + "Generate a premium, detailed JSON object with:"
                + "1. 'title': A short, catchy summary (e.g. 'Luxury Honeymoon in Bali'). "
                + "2. 'customerEmail': Extract email address if present in the request, otherwise return null. "
                + "3. 'travelDate': Extract the start date of the trip in YYYY-MM-DD format. If not explicitly stated, try to infer it (e.g. 'next Friday') or return null. "
                + "4. 'source': The starting country/city. "
                + "5. 'destination': The main destination country/city. "
                + "6. 'steps': A JSON array of logical workflow steps. "
                + "Include 'name' and 'description' for each step. "
                + "Steps MUST cover: Logistics (Flights, Transfers), Accommodation (Hotels), Key Activities (Tours), and Docs (Visa/Insurance). "
                + "For 'description', suggest specific details (e.g. 'Emirates Flight', 'Ritz Carlton', 'Scuba Diving at Great Barrier Reef'). "
                + "Return ONLY valid JSON.";
    }

    private com.travelapp.model.ParsedItinerary parseLlmResponse(String contentText) {
        try {
            // Cleanup
            contentText = contentText.replace("```json", "").replace("```", "").trim();

            JsonNode responseJson = objectMapper.readTree(contentText);

            String title = responseJson.has("title") ? responseJson.get("title").asText() : "New Itinerary";
            String customerEmail = responseJson.has("customerEmail") ? responseJson.get("customerEmail").asText() : null;
            String source = responseJson.has("source") ? responseJson.get("source").asText() : null;
            String destination = responseJson.has("destination") ? responseJson.get("destination").asText() : null;

            java.time.LocalDate travelDate = null;
            if (responseJson.has("travelDate") && !responseJson.get("travelDate").isNull()) {
                try {
                    travelDate = java.time.LocalDate.parse(responseJson.get("travelDate").asText());
                } catch (Exception e) {
                    System.err.println("Failed to parse travelDate: " + e.getMessage());
                }
            }

            JsonNode stepsArray = responseJson.has("steps") ? responseJson.get("steps") : null;

            List<WorkflowStep> steps = new ArrayList<>();
            if (stepsArray != null && stepsArray.isArray()) {
                for (JsonNode node : stepsArray) {
                    String name = node.has("name") ? node.get("name").asText() : "Unknown Step";
                    String desc = node.has("description") ? node.get("description").asText() : "";

                    steps.add(new WorkflowStep(
                            "step-" + System.nanoTime() + "-" + steps.size(),
                            name,
                            desc,
                            WorkflowStep.StepStatus.PENDING,
                            false,
                            new HashMap<>()));
                }
            }

            return new com.travelapp.model.ParsedItinerary(title, customerEmail, steps, travelDate, source, destination);

        } catch (Exception e) {
            System.err.println("Failed to parse LLM response: " + e.getMessage());
            return null;
        }
    }
}
